package com.dzkj.biz;

import Jama.Matrix;
import com.dzkj.bean.SurveyCalcResult;
import com.dzkj.bean.SurveyPoint;
import com.dzkj.bean.SurveyResult;
import com.dzkj.common.Angle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 测量成果处理类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class SurveyResultProcess {

    //region 测量结果坐标转换相关方法
    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数
     * 固定点超过2点用7参数法;2点用5参数法,此时φx=0，φy=0;1点用固定测站差分法
     * <param name="fixedPtsI0">固定点第1次测量值列表</param>
     * <param name="fixedPtsI">固定点第i次测量值列表</param>
     * <param name="transParams">转换参数列表</param>
     **/
    private static boolean calcTransParams(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI, List<Double> transParams) {
        // 获得转换参数
        transParams.clear();
        List<Double> theParams = calcTransParams(fixedPtsI0, fixedPtsI);
        int num = fixedPtsI.size();
        //只有一个固定点
        if (num == 1) {
            transParams.addAll(theParams);
            return true;
        }
        //固定点超过1个
        else {
            //获得转换参数
            SurveyPoint unStableFixedPt = findUnStableFixedPoint(theParams, fixedPtsI0, fixedPtsI);
            //固定点都是稳定的
            if (unStableFixedPt == null) {
                transParams.addAll(theParams);
                return true;
            }
            //有不稳定固定点，移除后重新计算转换参数，然后重新判定
            else {
                Optional<SurveyPoint> optional = fixedPtsI.stream().filter(it -> it.getName().equals(unStableFixedPt.getName())).findFirst();
                if (optional.isPresent()){
                    int idx = fixedPtsI.indexOf(optional.get());
                    fixedPtsI.remove(idx);
                    fixedPtsI0.remove(idx);
                    return false;
                }
                return true;
            }
        }
    }

    /** <summary>
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数
     * 固定点超过2点用7参数法;2点用5参数法,此时φx=0，φy=0;1点用固定测站差分法
     * </summary>
     * <param name="fixedPtsI0">固定点第1次测量值列表</param>
     * <param name="fixedPtsI">固定点第i次测量值列表</param>
     **/
    private static List<Double> calcTransParams(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI) {
        int num = fixedPtsI0.size();
        switch (num) {
            case 1:
                return calcTransParams1(fixedPtsI0, fixedPtsI);
            case 2:
                return calcTransParams5(fixedPtsI0, fixedPtsI);
            default:
                return calcTransParams7(fixedPtsI0, fixedPtsI);
        }
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数(测站固定，差分计算)
     * 参数顺序:方位角改正deltL,斜距改正deltS,球气改正deltC
     * </summary>
     * <param name="fixedPtsI0">固定点第1次测量值列表</param>
     * <param name="fixedPtsI">固定点第i次测量值列表</param>
     **/
    private static List<Double> calcTransParams1(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI) {
        int n = fixedPtsI.size();
        //方位角改正deltL
        double s = 0.0;
        for (int i = 0; i < n; i++) {
            s += fixedPtsI0.get(i).getHa() - fixedPtsI.get(i).getHa();
        }
        double deltL = s / n;

        //斜距改正deltS
        s = 0.0;
        for (int i = 0; i < n; i++) {
            s += (fixedPtsI0.get(i).getSd() - fixedPtsI.get(i).getSd()) / fixedPtsI.get(i).getSd();
        }
        double deltS = s / n;

        //球气改正deltC
        s = 0.0;
        for (int i = 0; i < n; i++) {
            double h0 = fixedPtsI0.get(i).getSd() * Math.sin(fixedPtsI0.get(i).getVa());
            double hi = fixedPtsI.get(i).getSd() * Math.sin(fixedPtsI.get(i).getVa());
            double di = fixedPtsI.get(i).getSd() * Math.cos(fixedPtsI.get(i).getVa());
            s += (h0 - hi) / (di * di);
        }
        double deltC = s / n;

        //返回参数
        ArrayList<Double> params1 = new ArrayList<>();
        params1.add(deltL);
        params1.add(deltS);
        params1.add(deltC);
        return params1;
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数(5参数法,此时φx=0，φy=0)；
     * <param name="fixedPtsI0">固定点第1次测量值列表</param>
     * <param name="fixedPtsI">固定点第i次测量值列表</param>
     **/
    public static List<Double> calcTransParams5(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI) {
        int num = fixedPtsI0.size();
        double[][] arrayB = new double[num * 3][5];
        double[] arrayL = new double[num * 3];
        for (int i = 0; i < num; i++) {
            arrayB[i*3][0] = 1;
            arrayB[i*3][1] = 0;
            arrayB[i*3][2] = 0;
            arrayB[i*3][3] = fixedPtsI.get(i).getY();
            arrayB[i*3][4] = fixedPtsI.get(i).getX();

            arrayB[i*3+1][0] = 0;
            arrayB[i*3+1][1] = 1;
            arrayB[i*3+1][2] = 0;
            arrayB[i*3+1][3] = -fixedPtsI.get(i).getX();
            arrayB[i*3+1][4] = fixedPtsI.get(i).getY();

            arrayB[i*3+2][0] = 0;
            arrayB[i*3+2][1] = 0;
            arrayB[i*3+2][2] = 1;
            arrayB[i*3+2][3] = 0;
            arrayB[i*3+2][4] = fixedPtsI.get(i).getZ();

            setArrayLvalue(fixedPtsI0, fixedPtsI, arrayL, i);
        }

        // 方程转换
        Matrix rx = getRxMatrix(arrayB, arrayL);
        double[] params5 = rx.transpose().getArray()[0];
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

    private static Matrix getRxMatrix(double[][] arrayB, double[] arrayL) {
        Matrix b = new Matrix(arrayB);
        Matrix l = new Matrix(arrayL, 1).transpose();
        Matrix nbb = b.transpose().times(b);
        Matrix bl = b.transpose().times(l);
        return nbb.lu().solve(bl);
    }

    private static void setArrayLvalue(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI, double[] arrayL, int i) {
        arrayL[i*3] = fixedPtsI0.get(i).getX() - fixedPtsI.get(i).getX();
        arrayL[i*3 + 1] = fixedPtsI0.get(i).getY() - fixedPtsI.get(i).getY();
        arrayL[i*3 + 2] = fixedPtsI0.get(i).getZ() - fixedPtsI.get(i).getZ();
    }

    /**
     * 计算第i次观测值转换到第1次测量值坐标系统下的转换参数(7参数法)；
     * <param name="fixedPtsI0">固定点第1次测量值列表</param>
     * <param name="fixedPtsI">固定点第i次测量值列表</param>
     **/
    private static List<Double> calcTransParams7(List<SurveyPoint> fixedPtsI0, List<SurveyPoint> fixedPtsI) {
        int num = fixedPtsI0.size();
        double[][] arrayB = new double[num * 3][7];
        double[] arrayL = new double[num * 3];
        for (int i = 0; i < num; i++) {
            arrayB[i*3][0] = 1;
            arrayB[i*3][1] = 0;
            arrayB[i*3][2] = 0;
            arrayB[i*3][3] = 0;
            arrayB[i*3][4] = -fixedPtsI.get(i).getZ();
            arrayB[i*3][5] = fixedPtsI.get(i).getY();
            arrayB[i*3][6] = fixedPtsI.get(i).getX();

            arrayB[i*3+1][0] = 0;
            arrayB[i*3+1][1] = 1;
            arrayB[i*3+1][2] = 0;
            arrayB[i*3+1][3] = fixedPtsI.get(i).getZ();
            arrayB[i*3+1][4] = 0;
            arrayB[i*3+1][5] = -fixedPtsI.get(i).getX();
            arrayB[i*3+1][6] = fixedPtsI.get(i).getY();

            arrayB[i*3+2][0] = 0;
            arrayB[i*3+2][1] = 0;
            arrayB[i*3+2][2] = 1;
            arrayB[i*3+2][3] = -fixedPtsI.get(i).getY();
            arrayB[i*3+2][4] = fixedPtsI.get(i).getX();
            arrayB[i*3+2][5] = 0;
            arrayB[i*3+2][6] = fixedPtsI.get(i).getZ();

            setArrayLvalue(fixedPtsI0, fixedPtsI, arrayL, i);
        } //end for

        // 方程转换
        Matrix rx = getRxMatrix(arrayB, arrayL);
        double[] doubles = rx.transpose().getArray()[0];
        List<Double> params7 = new ArrayList<>();
        for (double v : doubles) {
            params7.add(v);
        }
        return params7;
    }

    /**
     * 找到不稳定固定点
     * <param name="transParams">转换参数</param>
     * <param name="fixedPts0"></param>
     * <param name="fixedPtsI"></param>
     **/
    private static SurveyPoint findUnStableFixedPoint(List<Double> transParams, List<SurveyPoint> fixedPts0, List<SurveyPoint> fixedPtsI) {
        //转换到第1次测量坐标系下
        List<SurveyPoint> newFixedPtsI0 = calcTransPoints(fixedPtsI, transParams);

        //判定固定点是否稳定,找出不稳定点
        List<SurveyPoint> unStablePts = new ArrayList<>();
        List<Double> deltSList = new ArrayList<>();
        for (int i = 0; i < newFixedPtsI0.size(); i++) {
            SurveyPoint pt0 = fixedPts0.get(i);
            SurveyPoint newPt = newFixedPtsI0.get(i);
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
     * 只有1个固定点，测站稳定，差分转换
     * </summary>
     * <param name="surveyPtsI">第i次测量结果</param>
     * <param name="transParams">转换参数列表</param>
     * <param name="station">测站点</param>
     * <returns></returns>
     **/
    private static List<SurveyPoint> calcTransPoints0(List<SurveyPoint> surveyPtsI, List<Double> transParams, SurveyPoint station) {
        //方位角、斜距及球气差改正
        double deltL = transParams.get(0);
        double deltS = transParams.get(1);
        double deltC = transParams.get(2);

        List<SurveyPoint> transPts = new ArrayList<>();
        for (SurveyPoint pt : surveyPtsI) {
            SurveyPoint transPt = new SurveyPoint();
            double adjHa = pt.getHa() + deltL;
            double adjSd = pt.getSd() + pt.getSd() * deltS;
            double adjH = adjSd * Math.sin(pt.getVa()) + deltC * Math.pow(adjSd * Math.cos(pt.getVa()), 2);
            double adjHd = Math.sqrt(adjSd * adjSd - adjH * adjH);

            transPt.setId(pt.getId());
            transPt.setName(pt.getName());
            transPt.setRecycleNum(pt.getRecycleNum());
            transPt.setAsFixed(pt.isAsFixed());
            transPt.setMp(pt.getMp());

            transPt.setX(station.getX() + adjHd * Math.cos(adjHa));
            transPt.setY(station.getY() + adjHd * Math.sin(adjHa));
            transPt.setZ(station.getZ() + adjH);

            transPt.setHa(pt.getHa());
            transPt.setVa(pt.getVa());
            transPt.setSd(pt.getSd());

            transPts.add(transPt);
        }
        //把测站点也加进来,放在首位--20240227
        transPts.add(0, station);
        return transPts;
    }

    /**
     * 2个及以上固定点，坐标转换到第1期坐标参考系下
     * <param name="surveyPtsI"></param>
     * <param name="transParams"></param>
     * <returns></returns>
     **/
    private static List<SurveyPoint> calcTransPoints(List<SurveyPoint> surveyPtsI, List<Double> transParams) {
        double x = transParams.get(0);
        double y = transParams.get(1);
        double z = transParams.get(2);
        double dx = transParams.get(3);
        double dy = transParams.get(4);
        double dz = transParams.get(5);
        double k = transParams.get(6);
        //转换到第1次测量坐标系下
        List<SurveyPoint> newPtsI0 = new ArrayList<>();
        for (SurveyPoint pt : surveyPtsI) {
            SurveyPoint newPt = new SurveyPoint();

            newPt.setId(pt.getId());
            newPt.setName(pt.getName());
            newPt.setRecycleNum(pt.getRecycleNum());
            newPt.setAsFixed(pt.isAsFixed());
            newPt.setMp(pt.getMp());

            newPt.setX(x + (1+k) * pt.getX() + dz * pt.getY() - dy * pt.getZ());
            newPt.setY(y -  dz * pt.getX() + (1+k) * pt.getY() + dx * pt.getZ());
            newPt.setZ(z + dy * pt.getX() - dx * pt.getY() + (1+k) * pt.getZ());

            newPt.setHa(pt.getHa());
            newPt.setVa(pt.getVa());
            newPt.setSd(pt.getSd());

            newPtsI0.add(newPt);
        }

        return newPtsI0;
    }
    //endregion

    /**
     * 把第i次测量点的结果转换到第1次测量点的坐标系下(不成功返回长度为0的列表)
     * <param name="surveyPts0">第1次测量点列表(至少要有1个固定点)</param>
     * <param name="surveyPtsI">第i次测量点列表(至少要有1个固定点)</param>
     * <param name="msg">返回消息对象</param>
     * <param name="station">测站点</param>
     * <returns>转换后的测量点列表(不成功返回长度为0的列表)</returns>
     **/
    public static List<SurveyPoint> calcTransPoints(List<SurveyPoint> surveyPts0, List<SurveyPoint> surveyPtsI, SurveyPoint station) {
        //获取固定点列表
        List<SurveyPoint> fixedPts0 = surveyPts0.stream().filter(SurveyPoint::isAsFixed).collect(Collectors.toList());
        List<SurveyPoint> fixedPtsI = surveyPtsI.stream().filter(SurveyPoint::isAsFixed).collect(Collectors.toList());
        //第I次测量结果中的固定点数，可能由于无法观测的原因和第1次观测结果中的固定点不一致
        //过滤第1次测量结果中的固定点，使之与第I次保持一致
        List<SurveyPoint> fixedPtsI0 = new ArrayList<>();
        for (SurveyPoint pt : fixedPtsI) {
            for (SurveyPoint pt0 : fixedPts0) {
                if (!Objects.equals(pt.getName(), pt0.getName())) {
                    continue;
                }
                fixedPtsI0.add(pt0);
                break;
            }
        }

        //计算转换参数(坐标转换法需要循环判定固定点是否稳定，直至全部稳定或只有1个点为止)
        List<Double> transParams = new ArrayList<>();
        boolean isOk = calcTransParams(fixedPtsI0, fixedPtsI, transParams);
        while (!isOk) {
            isOk = calcTransParams(fixedPtsI0, fixedPtsI, transParams);
        }

        //获得转换结果
        int num = fixedPtsI.size();
        //只有一个固定点
        if (num == 1) {
            //测站不稳定，转换失败
            if (station == null) {
                return new ArrayList<>();
            } else {
                return calcTransPoints0(surveyPtsI, transParams, station);
            }
        }
        //有2个及以上固定点
        else {
            //把测站点也加进来,放在首位--20240227
            surveyPtsI.add(0, station);
            return calcTransPoints(surveyPtsI, transParams);
        }
    }

    /**
     * 计算单次测量结果
     * <param name="surveyResult">测量结果对象</param>
     * <param name="results">当次测量结果列表</param>
     **/
    public static void calcSurveyResultOnce(SurveyResult surveyResult, List<String> results) {
        //完善测量结果对象数据
        surveyResult.setHa(Double.parseDouble(results.get(0)));
        surveyResult.setVa(Double.parseDouble(results.get(1)));
        surveyResult.setT(Double.parseDouble(results.get(3)));
        surveyResult.setL(Double.parseDouble(results.get(4)));
        surveyResult.setSd(Double.parseDouble(results.get(6)));
        surveyResult.setX(Double.parseDouble(results.get(9)));
        surveyResult.setY(Double.parseDouble(results.get(8)));
        surveyResult.setZ(Double.parseDouble(results.get(10)));
        //首次测量
        if (surveyResult.isFirst()) {
            surveyResult.setX0(surveyResult.getX());
            surveyResult.setY0(surveyResult.getY());
            surveyResult.setZ0(surveyResult.getZ());
        }
    }

    /**
     * 计算测回内测量结果
     * <param name="surveyCalcResults">测量计算成果列表(已经过滤掉测量不成功的数据，留下有盘左盘右测量结果的数据)</param>
     * <param name="surveyResults">原始测量结果</param>
     * <param name="chIndex">测回序号</param>
     **/
    public static void calcSurveyResultCh(List<SurveyCalcResult> surveyCalcResults,
                                          List<SurveyResult> surveyResults, int chIndex) {
        //对应测回测量结果
        List<SurveyResult> chResults = surveyResults.stream().filter(it -> it.getChIndex() == chIndex).collect(Collectors.toList());
        //在测回结果中正镜测量且有效的结果
        List<SurveyResult> chResults1 = chResults.stream().filter(it -> it.isFace1() && it.isSuccess()).collect(Collectors.toList());
        //获取测量计算结果列表
        //起始点方向盘左测2次，盘右也测2次，用于计算半测回归零差.需要一个标识记录最后一次盘右测量情况
        boolean isLast = false;
        for (SurveyResult r1 : chResults1) {
            //在盘右结果中查找对应测点数据
            List<SurveyResult> results = chResults.stream().filter(it -> !it.isFace1() && it.isSuccess() && it.getId()==r1.getId()).collect(Collectors.toList());
            SurveyResult r2;
            switch (results.size()) {
                //无测量值
                case 0:
                    continue;
                    //只有1次盘右测量值(极端情形:归零测量盘左2次正常，盘右有1次正常，用正常的盘右测量值参与归零差计算)
                case 1:
                    r2 = results.get(0);
                    break;
                //有2次盘右测量值，必然是最后的归零测量
                default:
                {
                    //最后1次
                    if (isLast)
                    {
                        r2 = results.get(1);
                    }
                    else
                    {
                        isLast = true;
                        r2 = results.get(0);
                    }
                    break;
                }
            }

            SurveyCalcResult newResult = new SurveyCalcResult();
            newResult.setId(r1.getId());
            newResult.setChIndex(chIndex);
            newResult.setPtName(r1.getPtName());
            newResult.setAsFixed(r1.isAsFixed());
            newResult.setHa1(r1.getHa());
            newResult.setHa2(r2.getHa());
            newResult.setVa1(r1.getVa());
            newResult.setVa2(r2.getVa());
            newResult.setSd1(r1.getSd());
            newResult.setSd2(r2.getSd());
            newResult.setX((r1.getX() + r2.getX())/2);
            newResult.setY((r1.getY() + r2.getY())/2);
            newResult.setZ((r1.getZ() + r2.getZ())/2);

            surveyCalcResults.add(newResult);
        } //end for

        //计算2C、I角和Ha、Va、Sd均值
        for (SurveyCalcResult result : surveyCalcResults) {
            //计算2C值
            double tempV = result.getHa2() - Math.PI;
            tempV = tempV > 0 ? tempV : tempV + Math.PI * 2;
            result.setD2C(result.getHa1() - tempV);
            //处理角度0与359差值问题(D2C是一个很小值，不会超过1度。若差值超过PI，则遇到了角度0与359差值问题)
            result.setD2C(Angle.correct0_359(result.getD2C()));
            //计算Ha平均值
            result.setHaAve((result.getHa1() + tempV)/2);
            //计算i值
            result.setDi((result.getVa1() + result.getVa2() - Math.PI * 2)/2);
            //计算Va平均值
            result.setVaAve((result.getVa1() - result.getVa2() + Math.PI * 2)/2);
            //计算SD平均值
            result.setSdAve((result.getSd1() + result.getSd2())/2);
        }
    }

    /**
     * 补充完整测回间测量计算结果
     * <param name="surveyCalcResults"></param>
     **/
    public static void calcSurveyResultAll(List<SurveyCalcResult> surveyCalcResults) {
        //获取pIds列表(pid唯一)
        List<Long> pIds = surveyCalcResults.stream().map(SurveyCalcResult::getId).distinct().collect(Collectors.toList());
        for (Long pId : pIds) {
            //计算测回间平均ha,va,sd
            List<SurveyCalcResult> calcResults = surveyCalcResults.stream().filter(it -> it.getId() == pId).collect(Collectors.toList());
            int n = calcResults.size();
            double ha = 0.0;
            double va = 0.0;
            double sd = 0.0;
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            for (SurveyCalcResult result : calcResults) {
                ha +=result.getHaAve();
                va += result.getVaAve();
                sd += result.getSdAve();
                x += result.getX();
                y += result.getY();
                z += result.getZ();
            }

            ha = ha / n;
            va = va / n;
            sd = sd / n;
            x = x / n;
            y = y / n;
            z = z / n;
            //赋值
            for (SurveyCalcResult result : calcResults) {
                result.setHa(ha);
                result.setVa(va);
                result.setSd(sd);
                result.setX(x);
                result.setY(y);
                result.setZ(z);
            }
        }
    }

}
