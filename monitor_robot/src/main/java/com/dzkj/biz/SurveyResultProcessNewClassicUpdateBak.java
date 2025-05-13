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
public class SurveyResultProcessNewClassicUpdateBak {

    //region 未知点概略坐标
    public static boolean calcPtXYZ0(List<SurveyStation> stations) {
        //获取含有未知点的测站列表
        List<SurveyStation> unKnownSts = stations;
        boolean isOk = true;
        while (isOk) {
            //从有未知点的测站列表中查找至少包含2个已知坐标点的测站列表
            //（经过上面计算，原来没有已知的测站列表，可能就会有坐标已知点）
            List<SurveyStation> knownSts = unKnownSts.stream().filter(st -> getKnownPtsCount(st) >= 2).collect(Collectors.toList());
            //获取含有未知点的测站列表
            unKnownSts = unKnownSts.stream().filter(st -> {
                boolean isExist = knownSts.contains(st);
                int count = 0;
                if (isExist) {
                    count = getUnKnownPtsCount(st);
                }
                return count > 0;
            }).collect(Collectors.toList());
            for (SurveyStation st : knownSts) {
                calcPtXYZ0Once(st);
            }
            //循环条件：有至少包含2个已知坐标点的测站列表 且 有含有未知点的测站列表
            isOk = !knownSts.isEmpty() && !unKnownSts.isEmpty();
        }

        //以所有未知点都已计算，无未知坐标（值为9999999999）点的测站数为是否为0，作为是否计算成功的判据
        return stations.stream().noneMatch(st -> getUnKnownPtsCount(st) > 0);
    }

    /**
     * 获得测站中已知点个数
     * @param st st
     * @return int
     */
    private static int getKnownPtsCount(SurveyStation st) {
        double ee = 0.01;
        List<Point3d> knownPts = new ArrayList<>();
        if (Math.abs(st.getStation().getX0() - 9999999999.0) > ee) {
            knownPts.add(st.getStation());
        }
        for (SurveyLine line : st.getSurveyLines()) {
            if (Math.abs(line.getPoint2().getX0() - 9999999999.0) > ee) {
                knownPts.add(line.getPoint2());
            }
        }
        return knownPts.size();
    }

    /**
     * 获得测站中未知点个数
     * @param st st
     * @return int
     */
    private static int getUnKnownPtsCount(SurveyStation st) {
        double ee = 0.01;
        List<Point3d> unKnownPts = new ArrayList<>();
        if (Math.abs(st.getStation().getX0() - 9999999999.0) < ee) {
            unKnownPts.add(st.getStation());
        }
        for (SurveyLine line : st.getSurveyLines()) {
            if (Math.abs(line.getPoint2().getX0() - 9999999999.0) < ee) {
                unKnownPts.add(line.getPoint2());
            }
        }
        return unKnownPts.size();
    }

    /**
     * 计算单测站未知点概略坐标
     * @param st st
     */
    private static void calcPtXYZ0Once(SurveyStation st) {
        double ee = 0.01;
        //获取终点坐标初始值已知的测线集合
        List<SurveyLine> lines = st.getSurveyLines().stream().filter(line -> Math.abs(line.getPoint2().getX0() - 9999999999.0) > ee)
                .collect(Collectors.toList());
        //若测站点未知，先计算测站点坐标
        if (Math.abs(st.getStation().getX0() - 9999999999.0) < ee) {
            //计算测站点概略坐标
            List<Double> x0List = new ArrayList<>();
            List<Double> y0List = new ArrayList<>();
            List<Double> z0List = new ArrayList<>();

            for (int i = 0; i < lines.size() - 1 ; i++) {
                SurveyLine l1 = lines.get(i);
                for (int j = i + 1; j < lines.size(); j++) {
                    SurveyLine l2 = lines.get(j);
                    double[] values = new double[3];
                    double minBeta = Math.PI * 20.0 / 180;
                    boolean isOk = calcResection(l1, l2, st.getHi(), minBeta, values);
                    if (!isOk) continue;//无计算结果，下一循环
                    x0List.add(values[0]);
                    y0List.add(values[1]);
                    z0List.add(values[2]);
                }
                if(x0List.isEmpty()) return;//无计算结果，返回
            }
            st.getStation().setX0(x0List.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            st.getStation().setY0(y0List.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            st.getStation().setZ0(z0List.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        }

        //修正测边方位角初始值
        SurveyLine l01 = lines.get(0);
        double a0 = BaseFunction.alphaAb(l01.getPoint1().getX0(), l01.getPoint1().getY0()
                , l01.getPoint2().getX0(), l01.getPoint2().getY0());
        double delta0 = a0 - l01.getHa0();
        for (SurveyLine line : st.getSurveyLines()) {
            line.setHa0(line.getHa0() + delta0);
            if (line.getHa0() < 0) {
                line.setHa0(line.getHa0() + 2 * Math.PI);
            }
            if (line.getHa0() >= 2 * Math.PI) {
                line.setHa0(line.getHa0() - 2 * Math.PI);
            }
        }

        //计算测站未知点坐标
        for (SurveyLine line : st.getSurveyLines()) {
            if (Math.abs(line.getPoint2().getX0() - 9999999999.0) > ee) continue;
            double hd = line.getSd0() * Math.sin(line.getVa0());
            line.getPoint2().setX0(line.getPoint1().getX0() + hd * Math.cos(line.getHa0()));
            line.getPoint2().setY0(line.getPoint1().getY0() + hd * Math.sin(line.getHa0()));
            line.getPoint2().setZ0(line.getPoint1().getZ0() + line.getSd0() * Math.cos(line.getVa0()) + (st.getHi() - line.getHt()));
        }
    }

    /**
     * 三角形后方交会计算交点坐标
     * @param l1 l1
     * @param l2 l2
     * @param hi hi
     * @param minBeta 最新夹角
     * @param values x y z 值数组
     */
    private static boolean calcResection(SurveyLine l1, SurveyLine l2, double hi, double minBeta, double[] values) {
        //夹角1-St-2
        double delHa0 = l2.getHa0() - l1.getHa0();
        //delHa0若为负值，转正值
        if (delHa0 < 0) {
            delHa0 += 2*Math.PI;
        }
        //通过夹角1-St-2是否超过180度判断测站点St是在1-2连线的左边还是右边：不超-右；超-左
        //测站St在1-2连线右边，fh=-1，此时1-2-st顺时针;左边，fh=1，此时1-2-st逆时针
        int fh = delHa0 < Math.PI ? -1 : 1;
        //超高PI的夹角，转为反向内角
        delHa0 = delHa0>Math.PI ? 2 * Math.PI - delHa0 : delHa0;
        //夹角不能小于指定夹角,否则不计算
        if(delHa0 < minBeta && delHa0 > Math.PI - minBeta) return false;

        //分别取顶点夹角+S2、顶点夹角+S1计算测站概略坐标，后取平均作为结果
        double x1 = l1.getPoint2().getX0();
        double y1 = l1.getPoint2().getY0();
        double x2 = l2.getPoint2().getX0();
        double y2 = l2.getPoint2().getY0();
        double deltX = x2 - x1;
        double deltY = y2 - y1;
        double s0 = Math.sqrt(deltX * deltX + deltY * deltY);//1-2平距

        //角度取舍标识：12：保留平均值；1：保留第一次计算值；2：保留第二次计算值；0：无效结果
        int selFlag = 0;
        //第1次
        double s1 = l1.getSd0() * Math.sin(l1.getVa0());
        double s2 = l2.getSd0() * Math.sin(l2.getVa0());
        double b1 = Math.asin(s2 / s0 * Math.sin(delHa0));
        double b2 = Math.PI - delHa0 - b1;
        if(Math.abs(b1) > minBeta){
            selFlag = Math.abs(b2) > minBeta ? 12 : 2;
        } else {
            selFlag = Math.abs(b2) > minBeta ? 1 : 0;
        }
        if (selFlag == 0) return false;

        double cotb1 = 1 / Math.tan(b1);
        double cotb2 = 1 / Math.tan(b2);
        double resX1 = (x2 * cotb1 + x1 * cotb2 + fh * deltY) / (cotb1 + cotb2);
        double resY1 = (y2 * cotb1 + y1 * cotb2 - fh * deltX) / (cotb1 + cotb2);

        //第2次
        b2 = Math.asin(s1 / s0 * Math.sin(delHa0));
        b1 = Math.PI - delHa0 - b2;
        cotb1 = 1.0 / Math.tan(b1);
        cotb2 = 1.0 / Math.tan(b2);
        double resX2 = (x2 * cotb1 + x1 * cotb2 + fh * deltY) / (cotb1 + cotb2);
        double resY2 = (y2 * cotb1 + y1 * cotb2 - fh * deltX) / (cotb1 + cotb2);

        //平均
        values[0] = (resX1 + resX2) / 2;
        values[1] = (resY1 + resY2) / 2;
        values[2] = l1.getPoint2().getZ0() - l1.getSd0() * Math.cos(l1.getVa0()) - (hi - l1.getHt());
        return true;
    }

    /**
     * 后方交会计算未知点坐标
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param fh 1-2-pt顺时针：-1；逆时针：+1
     * @param s1 pt-1平距
     * @param s2 pt-2平距
     * @param values 测站坐标 x y
     */
    private static void resectionBack(double x1, double y1, double x2, double y2, int fh, double s1,double s2, double[] values) {
        double deltX = x2 - x1;
        double deltY = y2 -y1;
        double s0 = Math.sqrt(deltX*deltX + deltY*deltY);//1-2平距
        double b1 = Math.acos((s0 * s0 + s1 * s1 - s2 * s2) / (2.0 * s0 * s1));//2-1-st夹角
        double b2 = Math.acos((s0 * s0 + s2 * s2 -s1 * s1) / (2.0 * s0 * s2));//1-2-st夹角
        double cotb1 = 1.0 / Math.tan(b1);
        double cotb2 = 1.0 / Math.tan(b2);

        values[0] = (x2 * cotb1 + x1 * cotb2 + fh * deltY) / (cotb1 + cotb2);
        values[1] = (y2 * cotb1 + y1 * cotb2 - fh * deltX) / (cotb1 + cotb2);
    }

    /**
     * 三角形后方交会计算交点坐标
     * @param l1 l1
     * @param l2 l2
     * @param hi hi
     * @param values x,y,z值
     */
    private static void calcResection1(SurveyLine l1, SurveyLine l2, double hi, double[] values) {
        double x1 = l1.getPoint2().getX0();
        double y1 = l1.getPoint2().getY0();
        double x2 = l2.getPoint2().getX0();
        double y2 = l2.getPoint2().getY0();

        //夹角1-St-2
        double deltHa0 = l2.getHa0() - l1.getHa0();
        //delHa0若为负值，转正值
        if (deltHa0 < 0) {
            deltHa0+=2*Math.PI;
        }
        //通过夹角1-St-2是否超过180度判断测站点St是在1-2连线的左边还是右边：不超-右；超-左
        //测站St在1-2连线右边，fh=-1，此时1-2-st顺时针;左边，fh=1，此时1-2-st逆时针
        int fh = deltHa0 < Math.PI ? -1 : 1;
        double s1 = l1.getSd0() * Math.sin(l1.getVa0());
        double s2 = l2.getSd0() * Math.sin(l2.getVa0());
        resectionBack(x1, y1, x2, y2, fh, s1, s2, values);
        values[2] = l1.getPoint2().getZ0() - l1.getSd0() * Math.cos(l1.getVa0()) - (hi - l1.getHt());
    }

    /**
     * 三角形后方交会计算交点坐标
     * @param l1 l1
     * @param l2 l2
     * @param hi hi
     * @param values x,y,z值
     */
    private static void calcResection2(SurveyLine l1, SurveyLine l2, double hi, double[] values) {
        double x1 = l1.getPoint2().getX0();
        double y1 = l1.getPoint2().getY0();
        double x2 = l2.getPoint2().getX0();
        double y2 = l2.getPoint2().getY0();

        //夹角1-St-2
        double deltHa0 = l2.getHa0() - l1.getHa0();
        //delHa0若为负值，转正值
        if (deltHa0 < 0) {
            deltHa0 += 2 * Math.PI;
        }
        //通过夹角1-St-2是否超过180度判断测站点St是在1-2连线的左边还是右边：不超-右；超-左
        //测站St在1-2连线右边，fh=-1，此时1-2-st顺时针;左边，fh=1，此时1-2-st逆时针
        int fh = deltHa0 < Math.PI ? -1 : 1;
        double s1 = l1.getSd0() * Math.sin(l1.getVa0());
        double s2 = l2.getSd0() * Math.sin(l2.getVa0());
        resectionBack(x1, y1, x2, y2, fh, s1, s2, values);
        double[] values2 = new double[]{0.0, 0.0};
        double ss1 = BaseFunction.distAb(x1, y1, x2, y2);
        resectionBack(x1, y1, values[0], values[1], -fh, ss1, s2, values2);
        values[2] = l1.getPoint2().getZ0() - l1.getSd0() * Math.cos(l1.getVa0()) - (hi - l1.getHt());

        double ss = BaseFunction.distAb(values2[0], values2[1], x1, y1);
        double newX0 = 0.0;
        double newY0 = 0.0;
        double newX1 =s1;
        double newY1 =0.0;
        double[] values3 = new double[]{0.0, 0.0};
        double s0=BaseFunction.distAb(x1, y1, x2, y2);
        resectionBack(newX1, newY1, newX0, newY0, -fh, s0, s2, values3);

        double deltX = x2 - x1;
        double deltY = y2 - y1;
        double newDeltX = values3[0] - newX1;
        double newDeltY = values3[1] - newY1;
        //缩放因子m
        double m=Math.sqrt(newDeltX*newDeltX + newDeltY*newDeltY)/Math.sqrt(deltX*deltX + deltY*deltY);
        //旋转角度
        double theta=Math.atan2(deltX*newDeltY-deltY*newDeltX,deltX*newDeltX+deltY*newDeltY);
        double theta1 = Math.atan2(newDeltY, newDeltX) - Math.atan2(deltY, deltX);
        double a = m * Math.cos(theta);
        double b = m * Math.sin(theta);

        //平移参数c,d
        double c = values3[0] - (a*x2-b*y2);
        double d = values3[1] - (b*x2+a*y2);

        double aa=a/(a*a+b*b);
        double bb=b/(a*a+b*b);

        double x0 = aa * (newX0 - c) - bb * (newY0 - d);
        double y0 = bb * (newX0 - c) + aa * (newY0 - d);

        double sss = BaseFunction.distAb(x0, y0, x1, y1);
    }
    //endregion

    //region 平差计算
    /**
     * 完整的平差处理过程
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 精度 [长度1]
     */
    public static boolean adjust(List<Point3d> ptList, List<SurveyStation> stations, double[] m02) {
        boolean isOk = ptList.size() > 2 && !stations.isEmpty();
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
            for (int j = 0; j < w0List.size() - 1; j++) {
                for (int k = j+1; k < w0List.size(); k++) {
                    double deltW0 = w0List.get(j) - w0List.get(k);
                    if (Math.abs(deltW0) < Math.PI) {
                        continue;
                    }
                    //插值绝对值超过60秒时，较小者增加2PI
                    if (deltW0 < 0){
                        w0List.set(j, w0List.get(j) + 2 * Math.PI);
                    }else {
                        w0List.set(k, w0List.get(k) + 2 * Math.PI);
                    }
                }
            }
            //获取w0,并归化到[0,2PI]范围
            w0 = w0List.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            if (w0 > 2 * Math.PI) {
                w0 -= 2 * Math.PI;
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
                        theLine.getPoint2().getX0(), theLine.getPoint2().getY0());

                double a1 = deltX / deltS;
                double b1 = deltY / deltS;
                double c1 = deltZ / deltS;
                double l1 = (deltS - theLine.getSd0());

                double a2 = deltY / (deltD * deltD);
                double b2 = deltX / (deltD * deltD);
                double deltL = BaseFunction.alphaAb(theLine.getPoint1().getX0(), theLine.getPoint1().getY0(),
                        theLine.getPoint2().getX0(), theLine.getPoint2().getY0()) - theLine.getHa0();
                if (deltL < 0) {
                    deltL += 2 * Math.PI;
                }

                double l2 = deltL - w0;
                //处理0度左右与359度左右等价偏差问题:闭合差值绝对值超180度时，若闭合差为负，+2Pi；为正，-2Pi;
                if (Math.abs(l2) > Math.PI) {
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
        Matrix nbbInv = null;
        Matrix rX = null;
        Matrix v = null;
        Matrix[] matrices = new Matrix[3];
        adjustCalcOne(arrayB, arrayL, arrayP,  matrices);
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
     * param mValues 下三个数值
     *  ms2 斜距方差
     *  ma2 水平角方差
     *  mb2 垂直角方差
     */
    private static void adjustCalcOne(double[][] arrayB, double[] arrayL, double[][] arrayP, Matrix[] matrices) {
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
    }
    //endregion

    /**
     * 显示平差成果
     * @param isAdjustOk 平差是否成功
     * @param errorInfo 平差错误信息
     * @param ptList 测点列表
     * @param stations 测站列表
     * @param m02 单位权方差
     */
    public static String showResult(boolean isAdjustOk, String errorInfo, List<Point3d> ptList, List<SurveyStation> stations, double m02) {
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
        if (!isAdjustOk)
        {
            mainString = errorInfo;
            leftFillLen = (strLen - mainString.length()) / 2;
            info = BaseFunction.getStringByLen(mainString, strLen, fillChar, leftFillLen);
            sb.append(info).append("\r\n");
            sb.append(line).append("\r\n");
            return sb.toString();
        }

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
        info += "经典约束网平差";
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
            }
        }

        sb.append(line).append("\r\n");
        sb.append("\r\n");

        return sb.toString();
    }

}
