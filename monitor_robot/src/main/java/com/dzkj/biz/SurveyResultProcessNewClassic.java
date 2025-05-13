package com.dzkj.biz;

import Jama.Matrix;
import com.dzkj.bean.Point3d;
import com.dzkj.bean.SurveyLine;
import com.dzkj.bean.SurveyStation;
import com.dzkj.common.Angle;
import com.dzkj.common.BaseFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 10:47
 * @description 测量成果处理类(新计算方法-经典平差+Helmert分量方差定权)
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class SurveyResultProcessNewClassic {

    //region 平差计算

    /**
     * 完整的平差处理过程
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 精度 [长度1]
     */
    public static boolean adjust(List<Point3d> ptList, List<SurveyStation> stations, double[] m02)
    {
        boolean isOk = ptList.size() > 2 && stations.size()>0;
        if (!isOk) {
            return false;
        }

        int n = 0; //观测方程数
        int m; //必要观测数m(测点坐标：3x未知测点数u；全站仪0刻度线方位角：测站数；球气差改正数：1)
        for (SurveyStation st : stations) {
            n += st.getSurveyLines().size() * 3;
        }

        //未知点数
        int u;
        List<Point3d> uPtList = ptList.stream().filter(item -> !item.isAsFixed()).collect(Collectors.toList());
        u = uPtList.size();
        m =  u*3+stations.size() + 1;

        double[][] arrayB = new double[n][m];
        double[] arrayL = new double[n];
        double[][] arrayP = new double[n][n];
        double[] arrayX0 = new double[m]; //未知数初值

        calcParams(ptList, stations, arrayX0, arrayB, arrayL);

        // 首次定权
        setPArray1(stations, arrayP);

        double[] arrayVx = new double[m];
        double[] arrayV = new double[n];
        double[][] arrayDxx = new double[m][m];
        double[][] arrayDll = new double[n][n];

        //平差计算
        adjustCalc(arrayB, arrayL, arrayP,  arrayVx, arrayV, arrayDxx, arrayDll, m02);

        //赋值坐标平差值及精度
        for (int i = 0; i < u; i++) {
            uPtList.get(i).setX(arrayX0[3 * i] + arrayVx[3 * i]);
            uPtList.get(i).setY(arrayX0[3 * i + 1] + arrayVx[3 * i + 1]);
            uPtList.get(i).setZ(arrayX0[3 * i + 2] + arrayVx[3 * i + 2]);
            double mx = Math.sqrt(arrayDxx[3 * i][3 * i]);
            double my = Math.sqrt(arrayDxx[3 * i + 1][3 * i + 1]);
            double mz = Math.sqrt(arrayDxx[3 * i + 2][3 * i + 2]);
            uPtList.get(i).setMx(Double.isNaN(mx) ? 0.0 : mx);
            uPtList.get(i).setMy(Double.isNaN(my) ? 0.0 : my);
            uPtList.get(i).setMz(Double.isNaN(mz) ? 0.0 : mz);
        }
        //赋值观测值平差值及精度
        int rIdx = 0;
        for (SurveyStation st : stations) {
            for (SurveyLine theLine : st.getSurveyLines()) {
                theLine.setSd(theLine.getSd0() + arrayV[rIdx]);
                theLine.setHa(theLine.getHa0() + arrayV[rIdx + 1]);
                theLine.setVa(theLine.getVa0() + arrayV[rIdx + 2]);
                theLine.setMs(Math.sqrt(arrayDll[rIdx][rIdx]));
                theLine.setMs(Double.isNaN(theLine.getMs())? 0.0 : theLine.getMs());
                theLine.setMa(Math.sqrt(arrayDll[rIdx + 1][rIdx + 1]));
                theLine.setMa(Double.isNaN(theLine.getMa())? 0.0 : theLine.getMa());
                theLine.setMb(Math.sqrt(arrayDll[rIdx + 2][rIdx + 2]));
                theLine.setMb(Double.isNaN(theLine.getMb())? 0.0 : theLine.getMb());

                //矩阵行数加3
                rIdx += 3;
            }
        }
        return true;
    }

    /**
     * 计算系数阵中的参数
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param arrayX0 测点坐标初始值矩阵
     * @param arrayB 观测方程系数矩阵
     * @param arrayL 观测方程闭合差矩阵
     */
    private static void calcParams(List<Point3d> ptList, List<SurveyStation> stations, double[] arrayX0,
                                   double[][] arrayB, double[] arrayL) {
        //未知点列表
        List<Point3d> uPtList = ptList.stream().filter(item -> !item.isAsFixed()).collect(Collectors.toList());
        int u = uPtList.size();

        //行列号
        int rIdx = 0;
        //遍历测站
        for (int i = 0; i < stations.size(); i++) {
            SurveyStation st = stations.get(i);
            //计算测站0刻度线方位角初始估计值w0
            double w0 = 0.0;
            List<Double> w0List = new ArrayList<>();
            for (int j = 0; j < st.getSurveyLines().size(); j++) {
                SurveyLine theLine = st.getSurveyLines().get(j);
                double deltL = BaseFunction.alphaAb(theLine.getPoint1().getX0(), theLine.getPoint1().getY0(),
                        theLine.getPoint2().getX0(), theLine.getPoint2().getY0()) - theLine.getHa0();
                if (deltL < 0) {
                    deltL += 2 * Math.PI;
                }
                w0List.add(deltL);
            }
            //处理0度左右与359度左右等价偏差问题
            for (int j = 0; j < w0List.size()-1; j++) {
                for (int k = j+1; k < w0List.size(); k++) {
                    double deltW0 = w0List.get(j) - w0List.get(k);
                    if (Math.abs(deltW0) < 60.0 / 206265.0) {
                        continue;
                    }
                    //插值绝对值超过60秒时，较小者增加2PI
                    if (deltW0 < 0){
                        w0List.set(j,w0List.get(j) + 2 * Math.PI);
                    }else {
                        w0List.set(k,w0List.get(k) + 2 * Math.PI);
                    }
                }
            }
            //获取w0,并归化到[0,2PI]范围
            w0 = w0List.stream().mapToDouble(num -> num).average().orElse(0.0);
            if (w0 > 2 * 2 * Math.PI) {
                w0 -= 2 * 2 * Math.PI;
            }

            for (int j = 0; j < st.getSurveyLines().size(); j++) {
                SurveyLine theLine = st.getSurveyLines().get(j);
                double deltX = theLine.getPoint2().getX0() - theLine.getPoint1().getX0();
                double deltY = theLine.getPoint2().getY0() - theLine.getPoint1().getY0();
                double deltZ = theLine.getPoint2().getZ0() - theLine.getPoint1().getZ0() - (st.getHi() - theLine.getHt());
                //斜距
                double deltS = Math.sqrt(deltX * deltX + deltY * deltY + deltZ * deltZ);
                //平距
                double deltD = BaseFunction.distAb(theLine.getPoint1().getX0(), theLine.getPoint1().getY0(),
                        theLine.getPoint2().getX0(),theLine.getPoint2().getY0());

                double a1 = deltX / deltS;
                double b1 = deltY / deltS;
                double c1 = deltZ / deltS;
                double l1 = (deltS - theLine.getSd0());

                double a2 = deltY / (deltD * deltD);
                double b2 = deltX / (deltD * deltD);
                double deltL = BaseFunction.alphaAb(theLine.getPoint1().getX0(), theLine.getPoint1().getY0(),
                        theLine.getPoint2().getX0(),theLine.getPoint2().getY0()) - theLine.getHa0();
                if (deltL < 0) {
                    deltL += 2 * Math.PI;
                }

                double l2 = deltL - w0;
                //闭合差值绝对值超过60秒时，若闭合差为负，+2Pi；为正，-2Pi;
                if (Math.abs(l2) > 60.0 / 206265.0) {
                    if (l2 < 0) {
                        l2 += 2 * Math.PI;
                    } else {
                        l2 -= 2 * Math.PI;
                    }
                }

                double a3 = deltX * deltZ / (deltS * deltS * deltD);
                double b3 = deltY * deltZ / (deltS * deltS * deltD);
                double c3 = deltD / (deltS * deltS);
                double d3 = deltD / (2 * 6371000.0);
                double l3 = theLine.getVa0() - Math.acos(deltZ / deltS);

                //赋值系数矩阵B和L中的值
                //起点系数(必须是未知点，已知点无需改正)
                int cIdx = -1;
                for (int k = 0; k < uPtList.size(); k++) {
                    if (uPtList.get(k).getId() == theLine.getPoint1().getId()) {
                        cIdx = k;
                    }
                }
                cIdx *= 3;
                if (cIdx >=0) {
                    arrayB[rIdx][cIdx] = -a1;
                    arrayB[rIdx][cIdx + 1] = -b1;
                    arrayB[rIdx][cIdx + 2] = -c1;
                    arrayB[rIdx + 1][cIdx] = a2;
                    arrayB[rIdx + 1][cIdx + 1] = -b2;
                    arrayB[rIdx + 1][cIdx + 2] = 0;
                    arrayB[rIdx + 2][cIdx] = -a3;
                    arrayB[rIdx + 2][cIdx + 1] = -b3;
                    arrayB[rIdx + 2][cIdx + 2] = c3;

                    //未知数初始值
                    arrayX0[cIdx] = theLine.getPoint1().getX0();
                    arrayX0[cIdx + 1] = theLine.getPoint1().getY0();
                    arrayX0[cIdx + 2] = theLine.getPoint1().getZ0();
                }

                //终点系数(必须是未知点，已知点无需改正)
                cIdx = -1;
                for (int k = 0; k < uPtList.size(); k++) {
                    if (uPtList.get(k).getId() == theLine.getPoint2().getId()) {
                        cIdx = k;
                    }
                }
                cIdx *= 3;
                if (cIdx >=0) {
                    arrayB[rIdx][cIdx] = a1;
                    arrayB[rIdx][cIdx + 1] = b1;
                    arrayB[rIdx][cIdx + 2] = c1;
                    arrayB[rIdx + 1][cIdx] = -a2;
                    arrayB[rIdx + 1][cIdx + 1] = b2;
                    arrayB[rIdx + 1][cIdx + 2] = 0;
                    arrayB[rIdx + 2][cIdx] = a3;
                    arrayB[rIdx + 2][cIdx + 1] = b3;
                    arrayB[rIdx + 2][cIdx + 2] = -c3;

                    //未知数初始值
                    arrayX0[cIdx] = theLine.getPoint2().getX0();
                    arrayX0[cIdx + 1] = theLine.getPoint2().getY0();
                    arrayX0[cIdx + 2] = theLine.getPoint2().getZ0();
                }

                //w系数（测站0刻度线方位角初始值,放到所有测点未知数之后）
                arrayB[rIdx + 1][u * 3 + i] = -1;
                //k系数(球气差未知数，全网1个，放到w之后)
                arrayB[rIdx + 2][u * 3 + stations.size()] = -d3;

                //闭合差系数
                arrayL[rIdx] = l1;
                arrayL[rIdx + 1] = l2;
                arrayL[rIdx + 2] = l3;

                //矩阵行数加3
                rIdx += 3;
            }
        }
    }

    /**
     * 首次赋值权阵
     * @param stations 测站列表
     * @param arrayP 权阵
     */
    private static void setPArray1(List<SurveyStation> stations, double[][] arrayP) {
        int rIdx = 0;
        for (SurveyStation st : stations) {
            for (SurveyLine theLine : st.getSurveyLines()){
                arrayP[rIdx][rIdx] = Math.pow(1.0 / (206.265 * (1.0 + 0.001 * theLine.getSd0())), 2);
                arrayP[rIdx + 1][rIdx + 1] = 1.0;
                arrayP[rIdx + 2][rIdx + 2] = 1.0;

                //矩阵行数加3
                rIdx += 3;
            }
        }
    }

    /**
     * 平差计算-全部
     * @param arrayB 观测方程系数矩阵
     * @param arrayL 观测方程闭合差矩阵
     * @param arrayP 权阵
     * @param arrayVx 坐标改正数矩阵
     * @param arrayV 观测值改正数矩阵
     * @param arrayDxx 坐标改正数方差阵
     * @param arrayDll 观测值平差值方差阵
     * @param m02 验后单位权方差 [数组长度1]
     */
    private static void adjustCalc(double[][] arrayB, double[] arrayL, double[][] arrayP,
                                   double[] arrayVx, double[] arrayV, double[][] arrayDxx, double[][] arrayDll, double[] m02) {
        //平差计算+Helmert方差分量估计定权
        double ee = 1.0e-10; //退出循环最小差值
        double ms2 = 2.0;
        double ma2 = 1.0;
        double mb2 = 1.0;
        double[] mValues = {2.0, 1.0, 1.0};
        int maxCalcNum = 99; //最大迭代计算次数
        int calcNum = 0; //当前迭代计算次数

        double deltm1 = Math.abs(1.0 - ms2 / ma2);
        double deltm2 = Math.abs(1.0 - ms2 / mb2);
        double deltm3 = Math.abs(1.0 - ma2 / mb2);

        Matrix nbbInv = null;
        Matrix rX = null;
        Matrix v = null;
        Matrix[] matrices = new Matrix[3];
        //
        while ((deltm1 > ee || deltm2 > ee || deltm3 > ee) && calcNum < maxCalcNum) {
//            adjustCalcOne(arrayB, arrayL, arrayP,  nbbInv, rX, v, ms2, ma2, mb2);
            try {
                adjustCalcOne(arrayB, arrayL, arrayP, matrices, mValues);
                //赋值
                ms2 = mValues[0];
                ma2 = mValues[1];
                mb2 = mValues[2];
                setPArray2(arrayP, ms2, ma2, mb2);
                deltm1 = Math.abs(ms2 - ma2);
                deltm2 = Math.abs(ms2 - mb2);
                deltm3 = Math.abs(ma2 - mb2);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                calcNum++;
            }
        }
        //赋值
        nbbInv = matrices[0];
        rX = matrices[1];
        v = matrices[2];
        //未知数改正数
//        arrayVx = rX.getColumnPackedCopy();
        BaseFunction.copyArray1(arrayVx, rX.transpose().getArray()[0]);
        //观测值改正数
//        arrayV = v.getColumnPackedCopy();
        BaseFunction.copyArray1(arrayV, v.transpose().getArray()[0]);

        //精度评定
        Matrix p = new Matrix(arrayP);
        int r = arrayB.length - arrayB[0].length;
        //单位权中误差
        m02[0] = (v.transpose().times(p).times(v)).transpose().getArray()[0][0] / r;
        //求未知数协因数阵
        Matrix qxx = nbbInv.copy();
//        arrayDxx = (qxx.times(m02[0])).getArrayCopy();
        BaseFunction.copyArray2(arrayDxx, (qxx.times(m02[0])).getArray());
        //求观测值平差值协因数阵
        Matrix b = new Matrix(arrayB);
        Matrix qll = b.times(nbbInv).times(b.transpose());
//        arrayDll = (qll.times(m02[0])).getArrayCopy();
        BaseFunction.copyArray2(arrayDll, (qll.times(m02[0])).getArray());

    }

    /**
     * 平差计算-单次
     * @param arrayB 观测方程系数矩阵
     * @param arrayL 观测方程闭合差矩阵
     * @param arrayP 权阵
     * @param matrices 下三个矩阵
     *  nbbInv 含附加条件的法方程矩阵
     *  vx 坐标改正数矩阵
     *  v 观测值改正数矩阵
     * @param mValues 下三个数值
     *  ms2 斜距方差
     *  ma2 水平角方差
     *  mb2 垂直角方差
     */
    private static void adjustCalcOne(double[][] arrayB, double[] arrayL, double[][] arrayP,
                                      Matrix[] matrices, double[] mValues) {
        Matrix b = new Matrix(arrayB);
        Matrix l = new Matrix(arrayL, 1).transpose();
        Matrix p = new Matrix(arrayP).transpose();
        Matrix nbb = b.transpose().times(p).times(b);
        Matrix w = b.transpose().times(p).times(l);
        Matrix nbbInv = nbb.inverse();
        Matrix vx = nbbInv.times(w);
        Matrix v = b.times(vx).minus(l);
        matrices[0] = nbbInv;
        matrices[1] = vx;
        matrices[2] = v;
        //获取单项改正数数组
        int n = arrayL.length;
        int k = n / 3;
        double[] arrayV = v.transpose().getArray()[0];
        double[] arrayVs = new double[k];
        double[] arrayVa = new double[k];
        double[] arrayVb = new double[k];
        for (int i = 0; i < k; i++) {
            arrayVs[i] = arrayV[3 * i];
            arrayVa[i] = arrayV[3 * i + 1];
            arrayVb[i] = arrayV[3 * i + 2];
        }
        //单项单项权阵
        double[][] arrayPs = new double[k][k];
        double[][] arrayPa = new double[k][k];
        double[][] arrayPb = new double[k][k];
        for (int i = 0; i < k; i++) {
            arrayPs[i][i] = arrayP[3 * i][3 * i];
            arrayPa[i][i] = arrayP[3 * i + 1][3 * i + 1];
            arrayPb[i][i] = arrayP[3 * i + 2][3 * i + 2];
        }

        Matrix vs = new Matrix(arrayVs, 1).transpose();
        Matrix va = new Matrix(arrayVa, 1).transpose();
        Matrix vb = new Matrix(arrayVb, 1).transpose();
        Matrix ps = new Matrix(arrayPs);
        Matrix pa = new Matrix(arrayPa);
        Matrix pb = new Matrix(arrayPb);
        double ms2 = (vs.transpose().times(ps).times(vs)).transpose().getArray()[0][0]/ k;
        double ma2 = (va.transpose().times(pa).times(va)).transpose().getArray()[0][0] / k;
        double mb2 = (vb.transpose().times(pb).times(vb)).transpose().getArray()[0][0] / k;
        mValues[0] = ms2;
        mValues[1] = ma2;
        mValues[2] = mb2;
    }

    /**
     * 后续赋值权阵
     * @param arrayP 权阵
     * @param ms2 斜距方差
     * @param ma2 水平角方差
     * @param mb2 垂直角方差
     */
    private static void setPArray2(double[][] arrayP, double ms2, double ma2, double mb2) {
        for (int i = 0; i < arrayP.length; i += 3) {
            double p1 = ma2 / ms2 * arrayP[i][i];
            double p2 = arrayP[i + 1][i + 1];
            double p3 = ma2 / mb2 * arrayP[i + 2][i + 2];

            arrayP[i][i] = p1;
            arrayP[i + 1][i + 1] = p2;
            arrayP[i + 2][i + 2] = p3;
        }
    }
    //endregion

    /**
     * 显示平差成果
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 单位权方差
     */
    public static String showResult(List<Point3d> ptList, List<SurveyStation> stations, double m02) {
        StringBuilder sb = new StringBuilder();
        String fillChar = "-";
        int strLen = 90;
        String line = BaseFunction.getStringByRepeatChar(fillChar, strLen);
        sb.append(line).append("\r\n");
        String mainString = "三维网平差处理报告";
        int leftFillLen = (strLen - mainString.length()) / 2;
        fillChar = " ";
        String info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");

        mainString = "概略坐标";
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");
        info = BaseFunction.getStringByLen("点号", 10, fillChar, 8);
        info += BaseFunction.getStringByLen("X(m)", 20, fillChar, 16);
        info += BaseFunction.getStringByLen("Y(m)", 20, fillChar, 16);
        info += BaseFunction.getStringByLen("Z(m)", 20, fillChar, 16);
        info += BaseFunction.getStringByLen("备注", 10, fillChar, 8);
        mainString = info;
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        for (Point3d pt : ptList) {
            info = BaseFunction.getStringByLen(pt.getName(), 10, fillChar, 10 - pt.getName().length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getX0()), 20, fillChar,
                    20 - String.format("%.5f", pt.getX0()).length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getY0()), 20, fillChar,
                    20 - String.format("%.5f", pt.getY0()).length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getZ0()), 20, fillChar,
                    20 - String.format("%.5f", pt.getZ0()).length());
            if (pt.isAsFixed()) {
                info += BaseFunction.getStringByLen("固定", 10, fillChar, 8);
            } else {
                info += BaseFunction.getStringByRepeatChar(" ", 10);
            }//end if
            mainString = info;
            leftFillLen = (strLen - mainString.length()) / 2;
            info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
            sb.append(info).append("\r\n");
        }
        sb.append(line).append("\r\n");
        sb.append("\r\n");

        mainString = "平差总体信息";
        leftFillLen = (strLen -mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");
        mainString = "平差方法:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        info += "经典约束网平差(Helmert方差分量估计定权)";
        sb.append(info).append("\r\n");
        mainString = "测站总数:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        int stNum = stations.size();
        info += stNum;
        sb.append(info).append("\r\n");
        mainString = "已知点数:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        long knownNum = ptList.stream().filter(Point3d::isAsFixed).count();
        info += knownNum;
        sb.append(info).append("\r\n");
        mainString = "未知点数:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        long unknownNum = ptList.stream().filter(item -> !item.isAsFixed()).count();
        info += unknownNum;
        sb.append(info).append("\r\n");
        mainString = "总观测数:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        int totalVnum = 0;
        for (SurveyStation st : stations) {
            totalVnum += st.getSurveyLines().size() * 3;
        }
        info += totalVnum;
        sb.append(info).append("\r\n");
        mainString = "多余观测:";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        info += totalVnum - (unknownNum * 3+stations.size()+1);
        sb.append(info).append("\r\n");
        mainString = "验后单位权中误差(sec):";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        String m0Str = String.format("%.2f", Angle.rad2Deg(Math.sqrt(m02), false) * 3600);
        info += m0Str;
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");
        sb.append("\r\n");

        mainString = "坐标平差结果";
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");
        info = BaseFunction.getStringByLen("点号", 10, fillChar, 8);
        info += BaseFunction.getStringByLen("X(m)", 15, fillChar, 10);
        info += BaseFunction.getStringByLen("Y(m)", 15, fillChar, 10);
        info += BaseFunction.getStringByLen("Z(m)", 15, fillChar, 10);
        info += BaseFunction.getStringByLen("Mx(mm)", 8, fillChar, 2);
        info += BaseFunction.getStringByLen("My(mm)", 8, fillChar, 2);
        info += BaseFunction.getStringByLen("Mz(mm)", 8, fillChar, 2);
        info += BaseFunction.getStringByLen("Mp(mm)", 8, fillChar, 2);
        mainString = info;
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        for (Point3d pt : ptList) {
            String strMx = String.format("%.2f",pt.getMx() * 1000);
            String strMy = String.format("%.2f",pt.getMy() * 1000);
            String strMz = String.format("%.2f",pt.getMz() * 1000);
            String strMp = String.format("%.2f",pt.getMp() * 1000);
            info = BaseFunction.getStringByLen(pt.getName(), 10, fillChar, 10 - pt.getName().length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getX()), 15, fillChar,
                    15 - String.format("%.5f", pt.getX()).length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getY()), 15, fillChar,
                    15 - String.format("%.5f", pt.getY()).length());
            info += BaseFunction.getStringByLen(String.format("%.5f", pt.getZ()), 15, fillChar,
                    15 - String.format("%.5f", pt.getZ()).length());
            if(pt.isAsFixed()){
                info += BaseFunction.getStringByRepeatChar(" ", 32);
            }else {
                info += BaseFunction.getStringByLen(strMy, 8, fillChar, 8 - strMy.length());
                info += BaseFunction.getStringByLen(strMx, 8, fillChar, 8 - strMx.length());
                info += BaseFunction.getStringByLen(strMz, 8, fillChar, 8 - strMz.length());
                info += BaseFunction.getStringByLen(strMp, 8, fillChar, 8 - strMp.length());
            }
            mainString = info;
            leftFillLen = (strLen -mainString.length()) / 2;
            info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
            sb.append(info).append("\r\n");
        } //end for
        sb.append(line).append("\r\n");
        sb.append("\r\n");

        mainString = "观测值平差结果";
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        sb.append(line).append("\r\n");
        info = BaseFunction.getStringByLen("起点", 10, fillChar, 8);
        info += BaseFunction.getStringByLen("终点", 10, fillChar, 8);
        info += BaseFunction.getStringByLen("Ha(dms)", 15, fillChar, 8);
        info += BaseFunction.getStringByLen("Ma(sec)", 8, fillChar, 1);
        info += BaseFunction.getStringByLen("Va(dms)", 15, fillChar, 8);
        info += BaseFunction.getStringByLen("Mb(sec)", 8, fillChar, 1);
        info += BaseFunction.getStringByLen("Sd(m)", 15, fillChar, 10);
        info += BaseFunction.getStringByLen("Ms(mm)", 8, fillChar, 2);
        mainString = info;
        leftFillLen = (strLen - mainString.length()) / 2;
        info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
        sb.append(info).append("\r\n");
        for (SurveyStation st : stations) {
            for (SurveyLine theLine : st.getSurveyLines()) {
                String strHa = String.format("%.6f", Angle.rad2Dms(theLine.getHa(), false));
                String strMa = String.format("%.2f",Angle.rad2Deg(theLine.getMa(), false) * 3600);
                String strVa = String.format("%.6f",Angle.rad2Dms(theLine.getVa(), false));
                String strMb = String.format("%.2f",Angle.rad2Deg(theLine.getMb(), false) * 3600);
                String strSd = String.format("%.5f",theLine.getSd());
                String strMs = String.format("%.2f",theLine.getMs() * 1000);
                info = BaseFunction.getStringByLen(theLine.getPoint1().getName(), 10, fillChar, 10 - theLine.getPoint1().getName().length());
                info += BaseFunction.getStringByLen(theLine.getPoint2().getName(), 10, fillChar, 10 - theLine.getPoint2().getName().length());
                info += BaseFunction.getStringByLen(strHa, 15, fillChar, 15 - strHa.length());
                info += BaseFunction.getStringByLen(strMa, 8, fillChar, 8 - strMa.length());
                info += BaseFunction.getStringByLen(strVa, 15, fillChar, 15 - strVa.length());
                info += BaseFunction.getStringByLen(strMb, 8, fillChar, 8 - strMb.length());
                info += BaseFunction.getStringByLen(strSd, 15, fillChar, 15 - strSd.length());
                info += BaseFunction.getStringByLen(strMs, 8, fillChar, 8 - strMs.length());
                mainString = info;
                leftFillLen = (strLen - mainString.length()) / 2;
                info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
                sb.append(info).append("\r\n");
            } //end for
        } //end for

        sb.append(line).append("\r\n");
        sb.append("\r\n");

        return sb.toString();
    }

}
