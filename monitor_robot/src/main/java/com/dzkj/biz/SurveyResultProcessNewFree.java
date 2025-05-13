package com.dzkj.biz;

import Jama.Matrix;
import com.dzkj.bean.Point3d;
import com.dzkj.bean.SurveyLine;
import com.dzkj.bean.SurveyStation;
import com.dzkj.common.Angle;
import com.dzkj.common.BaseFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 10:47
 * @description 测量成果处理类(新计算方法-自由网平差+Helmert分量方差定权)
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class SurveyResultProcessNewFree {


    //region 平差计算

    /**
     * 完整的平差处理过程
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 精度 【数组长度1】
     */
    public static void adjust(List<Point3d> ptList, List<SurveyStation> stations, double[] m02) {

        int n = 0; //观测方程数
        int m = 0; //必要观测数m(测点坐标：3x未知测点数u；全站仪0刻度线方位角：测站数；球气差改正数：1)
        for(SurveyStation st : stations) {
            n += st.getSurveyLines().size() * 3;
        }

        //未知点数
        int u = ptList.size();
        m = u * 3 + stations.size() + 1;

        double[][] arrayB = new double[n][m];
        double[] arrayL = new double[n];
        double[][] arrayP = new double[n][n];
        double[][] arrayGt = new double[4][m];
        double[] arrayX0 = new double[m]; //未知数初值

        calcParams(ptList, stations, arrayX0, arrayB, arrayL, arrayGt);

        // 首次定权
        setPArray1(stations, arrayP);

        double[] arrayVx = new double[m];
        double[] arrayV = new double[n];
        double[][] arrayDxx = new double[m][n];
        double[][] arrayDll = new double[n][n];

        //平差计算
        adjustCalc(arrayB, arrayL, arrayP, arrayGt, arrayVx, arrayV, arrayDxx, arrayDll, m02);

        //赋值坐标平差值及精度
        for (int i = 0; i < ptList.size(); i++) {
            ptList.get(i).setX(arrayX0[3 * i] + arrayVx[3 * i]);
            ptList.get(i).setY(arrayX0[3 * i + 1] + arrayVx[3 * i + 1]);
            ptList.get(i).setZ(arrayX0[3 * i + 2] + arrayVx[3 * i + 2]);
            ptList.get(i).setMx(Math.sqrt(arrayDxx[3 * i][3 * i]));
            ptList.get(i).setMx(Double.isNaN(ptList.get(i).getMx()) ? 0.0 : ptList.get(i).getMx());
            ptList.get(i).setMy(Math.sqrt(arrayDxx[3 * i + 1][3 * i + 1]));
            ptList.get(i).setMy(Double.isNaN(ptList.get(i).getMy()) ? 0.0 : ptList.get(i).getMy());
            ptList.get(i).setMz(Math.sqrt(arrayDxx[3 * i + 2][3 * i + 2]));
            ptList.get(i).setMz(Double.isNaN(ptList.get(i).getMz()) ? 0.0 : ptList.get(i).getMz());
        }
        //赋值观测值平差值及精度
        int rIdx = 0;
        for(SurveyStation st : stations) {
            for(SurveyLine theLine : st.getSurveyLines()) {
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
    }

    /**
     * 计算系数阵中的参数
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param arrayX0 测点坐标初始值矩阵
     * @param arrayB 观测方程系数矩阵
     * @param arrayL 观测方程闭合差矩阵
     * @param arrayGt 附加条件矩阵
     */
    private static void calcParams(List<Point3d> ptList, List<SurveyStation> stations, double[] arrayX0,
                                   double[][] arrayB, double[] arrayL, double[][] arrayGt) {
        //未知点个数
        int u = ptList.size();
        //附加矩阵G前3行赋值
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < u * 3; j++) {
                arrayGt[i][j] = (i == j ? 1 : 0);
            }
        }

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

                double a3 = (deltX * deltZ) / (deltS * deltS * deltD);
                double b3 = (deltY * deltZ) / (deltS * deltS * deltD);
                double c3 = deltD / (deltS * deltS);
                double d3 = deltD / (2 * 6371000.0);
                double l3 = theLine.getVa0() - Math.acos(deltZ / deltS);

                //赋值系数矩阵B和L中的值
                //起点系数
                int cIdx = 0;
                for (int k = 0; k < ptList.size(); k++) {
                    if (ptList.get(k).getId() == theLine.getPoint1().getId()) {
                        cIdx = k;
                    }
                }
                cIdx *= 3;
                arrayB[rIdx][cIdx] = -a1;
                arrayB[rIdx][cIdx + 1] = -b1;
                arrayB[rIdx][cIdx + 2] = -c1;
                arrayB[rIdx + 1][cIdx] = a2;
                arrayB[rIdx + 1][cIdx + 1] = -b2;
                arrayB[rIdx + 1][cIdx + 2] = 0;
                arrayB[rIdx + 2][cIdx] = -a3;
                arrayB[rIdx + 2][cIdx + 1] = -b3;
                arrayB[rIdx + 2][cIdx + 2] = c3;

                //附加矩阵赋值
                arrayGt[3][cIdx] = theLine.getPoint1().getY0();
                arrayGt[3][cIdx + 1] = -theLine.getPoint1().getX0();
                arrayGt[3][cIdx + 2] = 0;

                //未知数初始值
                arrayX0[cIdx] = theLine.getPoint1().getX0();
                arrayX0[cIdx + 1] = theLine.getPoint1().getY0();
                arrayX0[cIdx + 2] = theLine.getPoint1().getZ0();

                //终点系数
                cIdx = -1;
                for (int k = 0; k < ptList.size(); k++) {
                    if (ptList.get(k).getId() == theLine.getPoint2().getId()) {
                        cIdx = k;
                    }
                }
                cIdx *= 3;
                arrayB[rIdx][cIdx] = a1;
                arrayB[rIdx][cIdx + 1] = b1;
                arrayB[rIdx][cIdx + 2] = c1;
                arrayB[rIdx + 1][cIdx] = -a2;
                arrayB[rIdx + 1][cIdx + 1] = b2;
                arrayB[rIdx + 1][cIdx + 2] = 0;
                arrayB[rIdx + 2][cIdx] = a3;
                arrayB[rIdx + 2][cIdx + 1] = b3;
                arrayB[rIdx + 2][cIdx + 2] = -c3;

                //附加矩阵赋值
                arrayGt[3][cIdx] = theLine.getPoint2().getY0();
                arrayGt[3][cIdx + 1] = -theLine.getPoint2().getX0();
                arrayGt[3][cIdx + 2] = 0;

                //未知数初始值
                arrayX0[cIdx] = theLine.getPoint2().getX0();
                arrayX0[cIdx + 1] = theLine.getPoint2().getY0();
                arrayX0[cIdx + 2] = theLine.getPoint2().getZ0();

                //w系数（测站0刻度线方位角初始值,放到所有测点未知数之后）
                arrayB[rIdx + 1][u * 3 + i] = -1;
                //k系数(球气差未知数，全网1个，放到w之后)
                arrayB[rIdx + 2][u * 3 + stations.size()] = -d3;

                //附加矩阵赋值
                arrayGt[3][u * 3 + i] = 1;

                //闭合差系数
                arrayL[rIdx] = l1;
                arrayL[rIdx + 1] = l2;
                arrayL[rIdx + 2] = l3;

                //矩阵行数加3
                rIdx += 3;
            }

            //附加矩阵赋值
            arrayGt[3][u * 3 + stations.size()] = 1;
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
     * @param arrayGt 附加条件矩阵
     * @param arrayVx 坐标改正数矩阵
     * @param arrayV 观测值改正数矩阵
     * @param arrayDxx 坐标改正数方差阵
     * @param arrayDll 观测值平差值方差阵
     * @param m02 验后单位权方差
     */
    private static void adjustCalc(double[][] arrayB, double[] arrayL, double[][] arrayP, double[][] arrayGt,
                                   double[] arrayVx, double[] arrayV, double[][] arrayDxx, double[][] arrayDll, double[] m02) {
        //平差计算+Helmert方差分量估计定权
        double ee = 1.0e-10; //退出循环最小差值
        Double ms2 = 2.0;
        Double ma2 = 1.0;
        Double mb2 = 1.0;
        double[] mValues = {2.0, 1.0, 1.0};
        int maxCalcNum = 99; //最大迭代计算次数
        int calcNum = 0; //当前迭代计算次数

        double deltm1 = Math.abs(1.0 - ms2 / ma2);
        double deltm2 = Math.abs(1.0 - ms2 / mb2);
        double deltm3 = Math.abs(1.0 - ma2 / mb2);

        Matrix nbb0 = null;
        Matrix nbbInv = null;
        Matrix rX = null;
        Matrix v = null;
        Matrix[] matrices = new Matrix[4];
        //
        while ((deltm1 > ee || deltm2 > ee || deltm3 > ee) && calcNum < maxCalcNum) {
//            adjustCalcOne(arrayB, arrayL, arrayP, arrayGt, nbb0, nbbInv, rX, v, ms2, ma2, mb2);
            adjustCalcOne(arrayB, arrayL, arrayP, arrayGt, matrices, mValues);
            //赋值
            ms2 = mValues[0];
            ma2 = mValues[1];
            mb2 = mValues[2];
            setPArray2(arrayP, ms2, ma2, mb2);
            deltm1 = Math.abs(ms2 - ma2);
            deltm2 = Math.abs(ms2 - mb2);
            deltm3 = Math.abs(ma2 - mb2);
            calcNum++;
        }
        //赋值
        nbb0 = matrices[0];
        nbbInv = matrices[1];
        rX = matrices[2];
        v = matrices[3];
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
        Matrix qxx = nbbInv.times(nbb0).times(nbbInv);
//        arrayDxx = (qxx.times(m02[0])).getArrayCopy();
        BaseFunction.copyArray2(arrayDxx, qxx.times(m02[0]).getArray());
        //求观测值平差值协因数阵
        Matrix b = new Matrix(arrayB);
        Matrix qll = b.times(nbbInv).times(b.transpose());
//        arrayDll = (qll.times(m02[0])).getArrayCopy();
        BaseFunction.copyArray2(arrayDll, qll.times(m02[0]).getArray());
    }

    /**
     * 平差计算-单次
     * @param arrayB 观测方程系数矩阵
     * @param arrayL 观测方程闭合差矩阵
     * @param arrayP 权阵
     * @param arrayGt 附加条件矩阵
     * @param matrices 下四个矩阵
     * @param nbb0 初始法方程矩阵
     * @param nbbInv 含附加条件的法方程矩阵
     * @param vx 坐标改正数矩阵
     * @param v 观测值改正数矩阵
     * @param mValues 下三个参数
     * @param ms2 斜距方差
     * @param ma2 水平角方差
     * @param mb2 垂直角方差
     */
    private static void adjustCalcOne(double[][] arrayB, double[] arrayL, double[][] arrayP, double[][] arrayGt,
                                      Matrix[] matrices, double[] mValues) {
        Matrix b = new Matrix(arrayB);
        Matrix l = new Matrix(arrayL, 1).transpose();
        Matrix p = new Matrix(arrayP).transpose();
        Matrix gt = new Matrix(arrayGt);
        Matrix gGt = gt.transpose().times(gt);
        Matrix nbb0 = b.transpose().times(p).times(b);
        Matrix nbb = nbb0.plus(gGt);
        Matrix w = b.transpose().times(p).times(l);
        Matrix nbbInv = nbb.inverse().transpose();
        Matrix vx = nbb.inverse().times(w);
        Matrix v = b.times(vx).minus(l);
        matrices[0] = nbb0;
        matrices[1] = nbbInv;
        matrices[2] = vx;
        matrices[3] = v;

        //获取单项改正数数组
        int n = arrayL.length;
        int k = n / 3;
        double[] arrayV = v.transpose().getArray()[0];
        double[] arrayVs = new double[k];
        double[] arrayVa = new double[k];
        double[] arrayVb = new double[k];
        for (int i = 0; i < k; i++)
        {
            arrayVs[i] = arrayV[3 * i];
            arrayVa[i] = arrayV[3 * i + 1];
            arrayVb[i] = arrayV[3 * i + 2];
        } //end for
        //单项单项权阵
        double[][] arrayPs = new double[k][k];
        double[][] arrayPa = new double[k][k];
        double[][] arrayPb = new double[k][k];
        for (int i = 0; i < k; i++)
        {
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
        double ms2 = (vs.transpose().times(ps).times(vs)).transpose().getArray()[0][0] / k;
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
    private static void setPArray2(double[][] arrayP, Double ms2, Double ma2, Double mb2) {
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

    //region 坐标参数转换到首次坐标基准下

    /**
     * 把第i次测量点的结果转换到初始测量点的坐标系下(不成功返回长度为0的列表)
     * @param surveyPtsI 第i次测量点列表(至少要有2个固定点)
     * @return 转换后的测量点列表(不成功返回长度为0的列表)
     */
    public static List<Point3d> calcTransPoints(List<Point3d> surveyPtsI)
    {
        //获取固定点列表
        List<Point3d> fixedPtsI = surveyPtsI.stream().filter(Point3d::isAsFixed).collect(Collectors.toList());
        List<Point3d> fixedPts0 = new ArrayList<>();
        for (Point3d pt : fixedPtsI) {
            Point3d pt0 = new Point3d(pt.getId(), pt.getName(), pt.isAsFixed(), pt.getX0(), pt.getY0(), pt.getZ0());
            pt0.setX(pt0.getX0());
            pt0.setY(pt0.getY0());
            pt0.setZ(pt0.getZ0());
            fixedPts0.add(pt0);
        }

        //计算转换参数(坐标转换法需要循环判定固定点是否稳定，直至全部稳定或只有1个点为止)
        List<Double> transParams = new ArrayList<>();
        boolean isOk = calcTransParams(fixedPts0, fixedPtsI, transParams);
        while (!isOk) {
            isOk = calcTransParams(fixedPts0, fixedPtsI, transParams);
        }

        //获得转换结果
        int num = fixedPtsI.size();
        //只有一个固定点
        if (num == 1) {
            return new ArrayList<>();
        }
        //有2个及以上固定点
        else {
            return calcTransPoints(surveyPtsI, transParams);
        }
    }

    /**
     * 个及以上固定点，坐标转换到第1期坐标参考系下
     * @param surveyPtsI surveyPtsI
     * @param transParams transParams
     */
    private static List<Point3d> calcTransPoints(List<Point3d> surveyPtsI, List<Double> transParams)
    {
        Double x = transParams.get(0);
        Double y = transParams.get(1);
        Double z = transParams.get(2);
        Double dx = transParams.get(3);
        Double dy = transParams.get(4);
        Double dz = transParams.get(5);
        Double k = transParams.get(6);
        //转换到第1次测量坐标系下
        List<Point3d> newPtsI0 = new ArrayList<>();
        for (Point3d pt : surveyPtsI) {
            Point3d newPt = new Point3d();
            newPt.setName(pt.getName());
            newPt.setAsFixed(pt.isAsFixed());
            newPt.setX0(pt.getX0());
            newPt.setY0(pt.getY0());
            newPt.setZ0(pt.getZ0());
            newPt.setX(x + (1+k) * pt.getX() + dz * pt.getY() - dy * pt.getZ());
            newPt.setY(y - dz * pt.getX() + (1+k) * pt.getY() + dx * pt.getZ());
            newPt.setZ(z + dy * pt.getX() - dx * pt.getY() + (1+k) * pt.getZ());
            newPt.setMx(pt.getMx());
            newPt.setMy(pt.getMy());
            newPt.setMz(pt.getMz());
            newPtsI0.add(newPt);
        }

        return newPtsI0;
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数
     * 固定点超过2点用7参数法;2点用5参数法,此时φx=0，φy=0;
     * @param fixedPtsI0 固定点第1次测量值列表
     * @param fixedPtsI 固定点第i次测量值列表
     * @param transParams 转换参数列表
     */
    private static boolean calcTransParams(List<Point3d> fixedPtsI0, List<Point3d> fixedPtsI, List<Double> transParams) {
        // 获得转换参数
        transParams.clear();
        List<Double> theParams = calcTransParams(fixedPtsI0, fixedPtsI);
        int num = fixedPtsI.size();
        //只有一个固定点
        if (num == 1) {
            transParams.addAll(theParams);
            return true;
        } else {//固定点超过1个
            //获得转换参数
            Point3d unStableFixedPt = findUnStableFixedPoint(theParams, fixedPtsI0, fixedPtsI);
            //固定点都是稳定的
            if (unStableFixedPt == null) {
                transParams.addAll(theParams);
                return true;
            } else {//有不稳定固定点，移除后重新计算转换参数，然后重新判定
                int idx = -1;
                for (int i = 0; i < fixedPtsI.size(); i++) {
                    if (Objects.equals(fixedPtsI.get(i).getName(), unStableFixedPt.getName())){
                        idx = i;
                    }
                }
                if (idx != -1) {
                    fixedPtsI.remove(idx);
                    fixedPtsI0.remove(idx);
                }
                return false;
            }
        }
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数
     * 固定点超过2点用7参数法;2点用5参数法,此时φx=0，φy=0;
     * @param fixedPtsI0 固定点第1次测量值列表
     * @param fixedPtsI 固定点第i次测量值列表
     */
    private static List<Double> calcTransParams(List<Point3d> fixedPtsI0, List<Point3d> fixedPtsI)
    {
        int num = fixedPtsI0.size();
        if (num == 2) {
            return calcTransParams5(fixedPtsI0, fixedPtsI);
        }
        return calcTransParams7(fixedPtsI0, fixedPtsI);
    }


    /**
     * 找到不稳定固定点
     * @param transParams 转换参数
     * @param fixedPts0 fixedPts0
     * @param fixedPtsI fixedPtsI
     * @return
     */
    private static Point3d findUnStableFixedPoint(List<Double> transParams, List<Point3d> fixedPts0, List<Point3d> fixedPtsI)
    {
        //转换到第1次测量坐标系下
        List<Point3d> newFixedPtsI0 = calcTransPoints(fixedPtsI, transParams);

        //判定固定点是否稳定,找出不稳定点
        List<Point3d> unStablePts = new ArrayList<>();
        List<Double> deltSList = new ArrayList<>();
        for (int i = 0; i < newFixedPtsI0.size(); i++)
        {
            Point3d pt0 = fixedPts0.get(i);
            Point3d newPt = newFixedPtsI0.get(i);
            double deltX = newPt.getX() - pt0.getX();
            double deltY = newPt.getY() - pt0.getY();
            double deltZ = newPt.getZ() - pt0.getZ();
            double deltS = Math.sqrt(deltX * deltX + deltY * deltY + deltZ * deltZ);
            //固定取点位中误差1mm作为测点是否移动的阈值 --> 修改为 10mm (24.07.28) --> 修改为 50mm (24.11.22)
            if (deltS < 2 * Math.sqrt(2) * 0.05) {
                continue;
            }
            unStablePts.add(newPt);
            deltSList.add(deltS);
        }

        if (unStablePts.size() == 0) {
            return null;
        }

        //找到偏距最大点
        double maxS = 0.0;
        int pos = 0;
        for (int i = 0; i < deltSList.size(); i++) {
            if (maxS > deltSList.get(i)) {
                continue;
            }
            maxS = deltSList.get(i);
            pos = i;
        }
        return unStablePts.get(pos);
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数(5参数法,此时φx=0，φy=0)；
     * @param fixedPtsI0 固定点第1次测量值列表
     * @param fixedPtsI 固定点第i次测量值列表
     * @return
     */
    private static List<Double> calcTransParams5(List<Point3d> fixedPtsI0, List<Point3d> fixedPtsI)
    {
        int num = fixedPtsI0.size();
        double[][] arrayB = new double[num * 3][ 5];
        double[] arrayL = new double[num * 3];
        for (int i = 0; i < num; i++)
        {
            arrayB[i * 3][0] = 1;
            arrayB[i * 3][1] = 0;
            arrayB[i * 3][2] = 0;
            arrayB[i * 3][3] = fixedPtsI.get(i).getY();
            arrayB[i * 3][4] = fixedPtsI.get(i).getX();

            arrayB[i * 3 + 1][0] = 0;
            arrayB[i * 3 + 1][1] = 1;
            arrayB[i * 3 + 1][2] = 0;
            arrayB[i * 3 + 1][3] = -fixedPtsI.get(i).getX();
            arrayB[i * 3 + 1][4] = fixedPtsI.get(i).getY();

            arrayB[i * 3 + 2][0] = 0;
            arrayB[i * 3 + 2][1] = 0;
            arrayB[i * 3 + 2][2] = 1;
            arrayB[i * 3 + 2][3] = 0;
            arrayB[i * 3 + 2][4] = fixedPtsI.get(i).getZ();

            arrayL[i * 3] = fixedPtsI0.get(i).getX() - fixedPtsI.get(i).getX();
            arrayL[i * 3 + 1] = fixedPtsI0.get(i).getY() - fixedPtsI.get(i).getY();
            arrayL[i * 3 + 2] = fixedPtsI0.get(i).getZ() - fixedPtsI.get(i).getZ();
        }


        Matrix b = new Matrix(arrayB);
        Matrix l = new Matrix(arrayL, 1).transpose();
        Matrix nbb = b.transpose().times(b);
        Matrix bl = b.transpose().times(l);
        Matrix rX = nbb.lu().solve(bl);

        double[] params5 = rX.transpose().getArray()[0];
        List<Double> params7 = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            if (i <= 2){
                params7.add(i, params5[i]);
            }else if (i >= 5){
                params7.add(i, params5[i-2]);
            }else {
                params7.add(i, 0.0);
            }
        }
        return params7;
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数(7参数法)；
     * @param fixedPtsI0 固定点第1次测量值列表
     * @param fixedPtsI 固定点第i次测量值列表
     */
    private static List<Double> calcTransParams7(List<Point3d> fixedPtsI0, List<Point3d> fixedPtsI)
    {
        int num = fixedPtsI0.size();
        double[][] arrayB = new double[num * 3][7];
        double[] arrayL = new double[num * 3];
        for (int i = 0; i < num; i++) {
            arrayB[i * 3][0] = 1;
            arrayB[i * 3][1] = 0;
            arrayB[i * 3][2] = 0;
            arrayB[i * 3][3] = 0;
            arrayB[i * 3][4] = -fixedPtsI.get(i).getZ();
            arrayB[i * 3][5] = fixedPtsI.get(i).getY();
            arrayB[i * 3][6] = fixedPtsI.get(i).getX();

            arrayB[i * 3 + 1][0] = 0;
            arrayB[i * 3 + 1][1] = 1;
            arrayB[i * 3 + 1][2] = 0;
            arrayB[i * 3 + 1][3] = fixedPtsI.get(i).getZ();
            arrayB[i * 3 + 1][4] = 0;
            arrayB[i * 3 + 1][5] = -fixedPtsI.get(i).getX();
            arrayB[i * 3 + 1][6] = fixedPtsI.get(i).getY();

            arrayB[i * 3 + 2][0] = 0;
            arrayB[i * 3 + 2][1] = 0;
            arrayB[i * 3 + 2][2] = 1;
            arrayB[i * 3 + 2][3] = -fixedPtsI.get(i).getY();
            arrayB[i * 3 + 2][4] = fixedPtsI.get(i).getX();
            arrayB[i * 3 + 2][5] = 0;
            arrayB[i * 3 + 2][6] = fixedPtsI.get(i).getZ();

            arrayL[i * 3] = fixedPtsI0.get(i).getX() - fixedPtsI.get(i).getX();
            arrayL[i * 3 + 1] = fixedPtsI0.get(i).getY() - fixedPtsI.get(i).getY();
            arrayL[i * 3 + 2] = fixedPtsI0.get(i).getZ() - fixedPtsI.get(i).getZ();
        }

        Matrix b = new Matrix(arrayB);
        Matrix l = new Matrix(arrayL, 1).transpose();
        Matrix nbb = b.transpose().times(b);
        Matrix bl = b.transpose().times(l);
        Matrix rX = nbb.lu().solve(bl);
        double[] doubles = rX.transpose().getArray()[0];
        List<Double> params7 = new ArrayList<>();
        for (double v : doubles) {
            params7.add(v);
        }
        return params7;
    }
    //endregion

    /**
     * 显示平差成果
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 单位权方差
     */
    public static String showResult(List<Point3d> ptList, List<SurveyStation> stations, double m02)
    {
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
        info += "秩亏自由网平差(Helmert方差分量估计定权)";
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
        info += totalVnum - ((unknownNum + knownNum) * 3+stations.size()+1);
        sb.append(info).append("\r\n");
        mainString = "验后单位权中误差(sec):";
        info = BaseFunction.getStringByLen(mainString, strLen / 2, fillChar, strLen / 2 - mainString.length());
        String m0Str = String.format("%.2f",Angle.rad2Deg(Math.sqrt(m02), false) * 3600);
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
            info += BaseFunction.getStringByLen(strMy, 8, fillChar, 8 - strMy.length());
            info += BaseFunction.getStringByLen(strMx, 8, fillChar, 8 - strMx.length());
            info += BaseFunction.getStringByLen(strMz, 8, fillChar, 8 - strMz.length());
            info += BaseFunction.getStringByLen(strMp, 8, fillChar, 8 - strMp.length());
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
