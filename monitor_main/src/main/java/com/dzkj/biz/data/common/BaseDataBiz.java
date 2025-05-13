package com.dzkj.biz.data.common;

import com.dzkj.biz.base.Angle;
import com.dzkj.biz.base.BaseFunction;
import com.dzkj.biz.data.vo.PointDataXyzhVO;
import com.dzkj.biz.data.vo.PointDataZVO;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.project.ProMission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/3
 * @description 通用数据业务服务类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class BaseDataBiz {

    // region 非三维数据
    /**
     * 检查单项是否超限
     **/
    public static String checkValueOver(PointDataZVO dataZ, List<AlarmItem> items, String type, AtomicInteger count, StringBuilder sb) {
        // 过滤数据
        List<AlarmItem> totalList = items.stream().filter(item -> item.getAlarmType() == 1 && type.equals(item.getResultItemType()))
                .collect(Collectors.toList());
        double value = getCompareValue(dataZ, type);
        if (totalList.size()>0){
            String originInfo = "";
            int level = 0;
            String levelInfo = "";
            boolean abs = false;
            for (AlarmItem item : totalList) {
                double tempValue = item.getAbsValue() ? Math.abs(value) : value;
                String thresholdStr = item.getAlarmThreshold().replace("(","").replace("]","");
                String[] thSplit = thresholdStr.split(",");
                // 阈值比较
                boolean b1 = "1".equals(item.getAlarmLevel()) || "2".equals(item.getAlarmLevel());
                if(b1 && tempValue>Double.parseDouble(thSplit[0]) && tempValue<=Double.parseDouble(thSplit[1])){
                    if ("1".equals(item.getAlarmLevel()) && level < 1){
                        level = 1;
                        levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                        abs = item.getAbsValue();
                    }
                    if ("2".equals(item.getAlarmLevel()) && level < 2){
                        level = 2;
                        levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                        abs = item.getAbsValue();
                    }
                }
                if ("3".equals(item.getAlarmLevel())){
                    if ("∞".equals(thSplit[1]) && tempValue > Double.parseDouble(thSplit[0]) && level<3){
                        level = 3;
                        levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                        abs = item.getAbsValue();
                    }
                    if ("-∞".equals(thSplit[1]) && tempValue <= Double.parseDouble(thSplit[0]) && level<3){
                        level = 3;
                        levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                        abs = item.getAbsValue();
                    }
                }
            }
            if (level > 0){
                count.getAndIncrement();
                sb.append(type).append(",");
                originInfo = dataZ.getName() + "," + items.get(0).getResultType() + "," + type
                        + ";" + levelInfo + ";" + abs;
            }
            return originInfo;
        }else {
            return "";
        }
    }

    /**
     * 检查双项是否超限
     **/
    public static List<String> checkValueOver2(PointDataZVO dataZ, List<AlarmItem> items, List<String> nameList, AtomicInteger count, StringBuilder sb) {
        List<String> list = new ArrayList<>();
        for (String name : nameList) {
            int level = 0;
            String levelInfo = "";
            boolean abs = false;
            List<AlarmItem> totalList = items.stream().filter(item -> name.equals(item.getResultItemType()))
                    .collect(Collectors.toList());
            if (totalList.size()>0){
                for (AlarmItem item : totalList) {
                    String[] split = item.getResultItemType().split("-");
                    String[] totalS = item.getAlarmThreshold().split("-");
                    String thresholdStr = totalS[0].replace("(", "").replace("]", "");
                    String thresholdStrOther = totalS[1].replace("(", "").replace("]", "");
                    String[] thSplit = thresholdStr.split(",");
                    String[] thSplitOther = thresholdStrOther.split(",");
                    String type = split[0];
                    String otherType = split[1];
                    double value = getCompareValue(dataZ, type);
                    double valueOther = getCompareValue(dataZ, otherType);
                    value = item.getAbsValue() ? Math.abs(value) : value;
                    valueOther = item.getAbsValue() ? Math.abs(valueOther) : valueOther;
                    // 阈值比较
                    boolean b1 = "1".equals(item.getAlarmLevel()) || "2".equals(item.getAlarmLevel());
                    if(b1 && value>Double.parseDouble(thSplit[0]) && value<=Double.parseDouble(thSplit[1])
                            && valueOther>Double.parseDouble(thSplitOther[0]) && valueOther<=Double.parseDouble(thSplitOther[1])){
                        if ("1".equals(item.getAlarmLevel()) && level < 1){
                            level = 1;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                        if ("2".equals(item.getAlarmLevel()) && level < 2){
                            level = 2;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                    }
                    if ("3".equals(item.getAlarmLevel())){
                        if ("∞".equals(thSplit[1]) && "∞".equals(thSplitOther[1]) && level<3
                                && value>Double.parseDouble(thSplit[0]) && valueOther>Double.parseDouble(thSplitOther[0])){
                            level = 3;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                        if ("-∞".equals(thSplit[1]) && "-∞".equals(thSplitOther[1]) && level<3
                                && value<=Double.parseDouble(thSplit[0]) && valueOther<=Double.parseDouble(thSplitOther[0])){
                            level = 3;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                        if ("∞".equals(thSplit[1]) && "-∞".equals(thSplitOther[1]) && level<3
                                && value>Double.parseDouble(thSplit[0]) && valueOther<=Double.parseDouble(thSplitOther[0])){
                            level = 3;
                            String thStr = "(" + thSplit[0] + "," + thSplit[1] + "]～("
                                    + thSplitOther[1] + "," + thSplitOther[0] + "]";
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + thStr;
                            abs = item.getAbsValue();
                        }
                        if ("-∞".equals(thSplit[1]) && "∞".equals(thSplitOther[1]) && level<3
                                && value<=Double.parseDouble(thSplit[0]) && valueOther>Double.parseDouble(thSplitOther[0])){
                            level = 3;
                            String thStr = "(" + thSplit[1] + "," + thSplit[0] + "]～("
                                    + thSplitOther[0] + "," + thSplitOther[1] + "]";
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + thStr;
                            abs = item.getAbsValue();
                        }
                    }
                }
                if (level > 0){
                    count.getAndIncrement();
                    sb.append(name).append(",");
                    String originInfo = dataZ.getName() + "," + items.get(0).getResultType() + "," + name
                            + ";" + levelInfo + ";" + abs;
                    list.add(originInfo);
                }
            }
        }
        return list;
    }

    /**
     * 设置初始值
     */
    public static void setFirstData(PointDataZVO dataZ, String type) {
        dataZ.setRecycleNum(1);
        if("水平位移(XY->S)".equals(type) || "支撑轴力".equals(type)){
            dataZ.setZ00(dataZ.getZ());
        }else {
            dataZ.setZ00(0.0);
        }
        dataZ.setZ(dataZ.getZ() - dataZ.getZ00());
        dataZ.setZ0(dataZ.getZ());
        dataZ.setGetTime0(dataZ.getGetTime());
        dataZ.setZPrev(dataZ.getZ());
        dataZ.setPrevGetTime(dataZ.getGetTime());
        dataZ.setDeltTime(0.0);
        dataZ.setDeltZ(0.0);
        dataZ.setVDeltZ(0.0);
        dataZ.setTotalZ(0.0);
        dataZ.setTotalZPrev(0.0);
    }

    /**
     * 设置数据值
     */
    public static void setNewData(PointDataZVO dataZ, PointDataZ lastData, ProMission mission, String type) {
        dataZ.setRecycleNum(lastData.getRecycleNum()+1);
        dataZ.setZ00(lastData.getZ00());
        dataZ.setZ0(lastData.getZ0());
        dataZ.setZ(dataZ.getZ() - dataZ.getZ00());
        dataZ.setTotalZPrev(lastData.getTotalZ());
        dataZ.setGetTime0(lastData.getGetTime0());
        dataZ.setZPrev(lastData.getZ());
        dataZ.setPrevGetTime(lastData.getGetTime());
        double deltTime = getDeltTime(mission, dataZ.getGetTime(), lastData.getGetTime());
        dataZ.setDeltTime(deltTime);
        if ("正常".equals(dataZ.getStatus()))
        {
            double deltZ = (dataZ.getZ() - lastData.getZ()) * mission.getRatio();
            double deltZNew = Math.round(deltZ * 100000000.0)/100000000.0;
            dataZ.setDeltZ(deltZNew);
            dataZ.setTotalZ(lastData.getTotalZ() + deltZNew);
            if (deltTime > 0){
                double vDeltZ = deltZNew/deltTime;
                double vDeltZNew = Math.round(vDeltZ * 100000000.0)/100000000.0;
                dataZ.setVDeltZ(vDeltZNew);
            }
        }
        else
        {
            dataZ.setDeltZ(0.0);
            dataZ.setTotalZ(lastData.getTotalZ());
            dataZ.setVDeltZ(0.0);
        }
    }
    // endregion

    // region 私有方法
    /**
     * 获取验证数据
     */
    private static double getCompareValue(PointDataZVO dataZ, String type) {
        switch (type)
        {
            case "累计变化量":
                return dataZ.getTotalZ() == null ? 0.0 : dataZ.getTotalZ();
            case "单次变化量":
                return dataZ.getDeltZ() == null ? 0.0 : dataZ.getDeltZ();
            case "日变化速率":
                return dataZ.getVDeltZ() == null ? 0.0 : dataZ.getVDeltZ();
            case "测量值":
                return dataZ.getZ() == null ? 0.0 : dataZ.getZ();
            default:
                return 0.0;
        }
    }
    // endregion

    // region 三维数据
    /**
     * 三维单项报警检查
     */
    public static List<String> checkValueOverXyz(PointDataXyzhVO dataXyzh, List<AlarmItem> items, String type, AtomicInteger count, StringBuilder sb, List<String> list) {
        // 过滤数据
        List<AlarmItem> totalList = items.stream().filter(item -> item.getAlarmType() == 1 && type.equals(item.getResultItemType()))
                .collect(Collectors.toList());
        List<String> alarmType = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            List<AlarmItem> filterList = filterAlarmItem(i, totalList);
            double value = getCompareValue2(dataXyzh, type, i);
            if (filterList.size()>0){
                String originInfo;
                int level = 0;
                String levelInfo = "";
                boolean abs = false;
                for (AlarmItem item : filterList) {
                    double tempValue = item.getAbsValue() ? Math.abs(value) : value;
                    String thresholdStr = item.getAlarmThreshold().replace("(","").replace("]","");
                    String[] thSplit = thresholdStr.split(",");
                    // 阈值比较
                    boolean b1 = "1".equals(item.getAlarmLevel()) || "2".equals(item.getAlarmLevel());
                    if(b1 && tempValue>Double.parseDouble(thSplit[0]) && tempValue<=Double.parseDouble(thSplit[1])){
                        if ("1".equals(item.getAlarmLevel()) && level < 1){
                            level = 1;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                        if ("2".equals(item.getAlarmLevel()) && level < 2){
                            level = 2;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                    }
                    if ("3".equals(item.getAlarmLevel())){
                        if ("∞".equals(thSplit[1]) && tempValue > Double.parseDouble(thSplit[0]) && level<3){
                            level = 3;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                        if ("-∞".equals(thSplit[1]) && tempValue <= Double.parseDouble(thSplit[0]) && level<3){
                            level = 3;
                            levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                            abs = item.getAbsValue();
                        }
                    }
                }
                if (level > 0){
                    count.getAndIncrement();
                    String qz = getDetailType(i);
                    sb.append(qz).append(type).append(",");
                    originInfo = dataXyzh.getName() + "," + filterList.get(0).getResultType() + "," + type
                            + ";" + levelInfo + ";" + abs  + ";" + value;
                    list.add(originInfo);
                    if ( i < 3) {
                        alarmType.add(i + "");
                    }
                }
            }
        }
        return alarmType;
    }

    /**
     * 三维双项报警检查
     */
    public static List<String> checkValueOverXyz2(PointDataXyzhVO dataXyzh, List<AlarmItem> items, List<String> nameList, AtomicInteger count, StringBuilder sb, List<String> list) {
        for (String name : nameList) {
            List<AlarmItem> totalList = items.stream().filter(item -> name.equals(item.getResultItemType()))
                    .collect(Collectors.toList());
            String[] split = name.split("-");
            String type = split[0];
            String otherType = split[1];
            for (int i = 0; i < 6; i++) {
                int level = 0;
                String levelInfo = "";
                boolean abs = false;
                List<AlarmItem> filterList = filterAlarmItem(i, totalList);
                if (filterList.size() > 0) {
                    for (AlarmItem item : filterList) {
                        String[] totalS = item.getAlarmThreshold().split("-");
                        String thresholdStr = totalS[0].replace("(", "").replace("]", "");
                        String thresholdStrOther = totalS[1].replace("(", "").replace("]", "");
                        String[] thSplit = thresholdStr.split(",");
                        String[] thSplitOther = thresholdStrOther.split(",");
                        double value = getCompareValue2(dataXyzh, type, i);
                        double valueOther = getCompareValue2(dataXyzh, otherType, i);
                        value = item.getAbsValue() ? Math.abs(value) : value;
                        valueOther = item.getAbsValue() ? Math.abs(valueOther) : valueOther;
                        // 阈值比较
                        if(("1".equals(item.getAlarmLevel()) || "2".equals(item.getAlarmLevel()))
                                && value>Double.parseDouble(thSplit[0]) && value<=Double.parseDouble(thSplit[1])
                                && valueOther>Double.parseDouble(thSplitOther[0]) && valueOther<=Double.parseDouble(thSplitOther[1])){
                            if ("1".equals(item.getAlarmLevel()) && level < 1){
                                level = 1;
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                                abs = item.getAbsValue();
                            }
                            if ("2".equals(item.getAlarmLevel()) && level < 2){
                                level = 2;
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                                abs = item.getAbsValue();
                            }
                        }
                        if ("3".equals(item.getAlarmLevel())){
                            if ("∞".equals(thSplit[1]) && "∞".equals(thSplitOther[1]) && level<3
                                    && value>Double.parseDouble(thSplit[0]) && valueOther>Double.parseDouble(thSplitOther[0])){
                                level = 3;
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                                abs = item.getAbsValue();
                            }
                            if ("-∞".equals(thSplit[1]) && "-∞".equals(thSplitOther[1]) && level<3
                                    && value<=Double.parseDouble(thSplit[0]) && valueOther<=Double.parseDouble(thSplitOther[0])){
                                level = 3;
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + item.getAlarmThreshold();
                                abs = item.getAbsValue();
                            }
                            if ("∞".equals(thSplit[1]) && "-∞".equals(thSplitOther[1]) && level<3
                                    && value>Double.parseDouble(thSplit[0]) && valueOther<=Double.parseDouble(thSplitOther[0])){
                                level = 3;
                                String thStr = "(" + thSplit[0] + "," + thSplit[1] + "]～("
                                        + thSplitOther[1] + "," + thSplitOther[0] + "]";
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + thStr;
                                abs = item.getAbsValue();
                            }
                            if ("-∞".equals(thSplit[1]) && "∞".equals(thSplitOther[1]) && level<3
                                    && value<=Double.parseDouble(thSplit[0]) && valueOther>Double.parseDouble(thSplitOther[0])){
                                level = 3;
                                String thStr = "(" + thSplit[1] + "," + thSplit[0] + "]～("
                                        + thSplitOther[0] + "," + thSplitOther[1] + "]";
                                levelInfo = item.getAlarmInfo() + ";" + item.getAlarmLevel() + ";" + thStr;
                                abs = item.getAbsValue();
                            }
                        }
                    }
                    if (level > 0){
                        count.getAndIncrement();
                        sb.append(name).append(",");
                        String originInfo = dataXyzh.getName() + "," + filterList.get(0).getResultType() + "," + name
                                + ";" + levelInfo + ";" + abs;
                        list.add(originInfo);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 设置初始值
     **/
    public static void setFirstData(PointDataXyzhVO dataXyzh, List<Point> points, boolean auto) {
        dataXyzh.setRecycleNum(1);
        dataXyzh.setX0(dataXyzh.getX());
        dataXyzh.setY0(dataXyzh.getY());
        dataXyzh.setZ0(dataXyzh.getZ());
        // 设置P S T
        setFirstPsAndT(dataXyzh, points);
        dataXyzh.setP0(dataXyzh.getP());
        dataXyzh.setS0(dataXyzh.getS());
        dataXyzh.setT0(dataXyzh.getT());
        dataXyzh.setXPrev(dataXyzh.getX());
        dataXyzh.setYPrev(dataXyzh.getY());
        dataXyzh.setZPrev(dataXyzh.getZ());
        dataXyzh.setPPrev(dataXyzh.getP());
        dataXyzh.setSPrev(dataXyzh.getS());
        dataXyzh.setTPrev(dataXyzh.getT());
        dataXyzh.setGetTime0(dataXyzh.getGetTime());
        dataXyzh.setGetTimePrev(dataXyzh.getGetTime());
        dataXyzh.setDeltX(0.0);
        dataXyzh.setVDeltX(0.0);
        dataXyzh.setTotalX(0.0);
        dataXyzh.setTotalXPrev(0.0);
        dataXyzh.setDeltY(0.0);
        dataXyzh.setVDeltY(0.0);
        dataXyzh.setTotalY(0.0);
        dataXyzh.setTotalYPrev(0.0);
        dataXyzh.setDeltZ(0.0);
        dataXyzh.setVDeltZ(0.0);
        dataXyzh.setTotalZ(0.0);
        dataXyzh.setTotalZPrev(0.0);
        dataXyzh.setDeltP(0.0);
        dataXyzh.setVDeltP(0.0);
        dataXyzh.setTotalP(0.0);
        dataXyzh.setTotalPPrev(0.0);
        dataXyzh.setDeltS(0.0);
        dataXyzh.setVDeltS(0.0);
        dataXyzh.setTotalS(0.0);
        dataXyzh.setTotalSPrev(0.0);
        dataXyzh.setDeltT(0.0);
        dataXyzh.setVDeltT(0.0);
        dataXyzh.setTotalT(0.0);
        dataXyzh.setTotalTPrev(0.0);
        if (!auto){
            dataXyzh.setHa(0.0);
            dataXyzh.setVa(0.0);
            dataXyzh.setSd(0.0);
        }
    }

    /**
     * 设置非初始值
     **/
    public static void setNewData(PointDataXyzhVO dataXyzh, PointDataXyzh lastData, ProMission mission, List<Point> points, boolean auto) {
        dataXyzh.setRecycleNum(lastData.getRecycleNum()+1);
        dataXyzh.setS00(lastData.getS00());
        dataXyzh.setT00(lastData.getT00());
        dataXyzh.setX0(lastData.getX0());
        dataXyzh.setY0(lastData.getY0());
        dataXyzh.setZ0(lastData.getZ0());
        dataXyzh.setP0(lastData.getP0());
        dataXyzh.setS0(lastData.getS0());
        dataXyzh.setT0(lastData.getT0());
        // 设置P S T
        setNewPsAndT(dataXyzh, points);
        dataXyzh.setXPrev(lastData.getX());
        dataXyzh.setYPrev(lastData.getY());
        dataXyzh.setZPrev(lastData.getZ());
        dataXyzh.setPPrev(lastData.getP());
        dataXyzh.setSPrev(lastData.getS());
        dataXyzh.setTPrev(lastData.getT());
        dataXyzh.setGetTime0(lastData.getGetTime0());
        dataXyzh.setGetTimePrev(lastData.getGetTime());
        // 设置时间间隔
        double deltTime = getDeltTime(mission, dataXyzh.getGetTime(), lastData.getGetTime());
        dataXyzh.setDeltTime(Math.round(deltTime * 100000000.0) / 100000000.0);
        if ("正常".equals(dataXyzh.getStatus()))
        {
            dataXyzh.setDeltX((dataXyzh.getX()-dataXyzh.getXPrev()) * mission.getRatio());
            dataXyzh.setDeltY((dataXyzh.getY()-dataXyzh.getYPrev()) * mission.getRatio());
            dataXyzh.setDeltZ((dataXyzh.getZ()-dataXyzh.getZPrev()) * mission.getRatio());
            dataXyzh.setDeltP((dataXyzh.getP()-dataXyzh.getPPrev()) * mission.getRatio());
            dataXyzh.setDeltS((dataXyzh.getS()-dataXyzh.getSPrev()) * mission.getRatio());
            dataXyzh.setDeltT((dataXyzh.getT()-dataXyzh.getTPrev()) * mission.getRatio());
            Double time = dataXyzh.getDeltTime();
            if (time > 0){
                dataXyzh.setVDeltX(Math.round(dataXyzh.getDeltX()/time * 100000000.0) / 100000000.0);
                dataXyzh.setVDeltY(Math.round(dataXyzh.getDeltY()/time * 100000000.0) / 100000000.0);
                dataXyzh.setVDeltZ(Math.round(dataXyzh.getDeltZ()/time * 100000000.0) / 100000000.0);
                dataXyzh.setVDeltP(Math.round(dataXyzh.getDeltP()/time * 100000000.0) / 100000000.0);
                dataXyzh.setVDeltS(Math.round(dataXyzh.getDeltS()/time * 100000000.0) / 100000000.0);
                dataXyzh.setVDeltT(Math.round(dataXyzh.getDeltT()/time * 100000000.0) / 100000000.0);
            }else {
                dataXyzh.setVDeltX(0.0);
                dataXyzh.setVDeltY(0.0);
                dataXyzh.setVDeltZ(0.0);
                dataXyzh.setVDeltP(0.0);
                dataXyzh.setVDeltS(0.0);
                dataXyzh.setVDeltT(0.0);
            }
        }
        else
        {
            dataXyzh.setDeltX(0.0);
            dataXyzh.setDeltY(0.0);
            dataXyzh.setDeltZ(0.0);
            dataXyzh.setDeltP(0.0);
            dataXyzh.setDeltS(0.0);
            dataXyzh.setDeltT(0.0);
            dataXyzh.setVDeltX(0.0);
            dataXyzh.setVDeltY(0.0);
            dataXyzh.setVDeltZ(0.0);
            dataXyzh.setVDeltP(0.0);
            dataXyzh.setVDeltS(0.0);
            dataXyzh.setVDeltT(0.0);
        }
        dataXyzh.setTotalX(lastData.getTotalX()+dataXyzh.getDeltX());
        dataXyzh.setTotalY(lastData.getTotalY()+dataXyzh.getDeltY());
        dataXyzh.setTotalZ(lastData.getTotalZ()+dataXyzh.getDeltZ());
        double totalZSqrt = Math.sqrt(dataXyzh.getTotalX() * dataXyzh.getTotalX() + dataXyzh.getTotalY() * dataXyzh.getTotalY());
        dataXyzh.setTotalP(Math.round(totalZSqrt * 100000000.0) / 100000000.0);
        dataXyzh.setTotalS(lastData.getTotalS()+dataXyzh.getDeltS());
        dataXyzh.setTotalT(lastData.getTotalT()+dataXyzh.getDeltT());
        dataXyzh.setTotalXPrev(lastData.getTotalX());
        dataXyzh.setTotalYPrev(lastData.getTotalY());
        dataXyzh.setTotalZPrev(lastData.getTotalZ());
        dataXyzh.setTotalPPrev(lastData.getTotalP());
        dataXyzh.setTotalSPrev(lastData.getTotalS());
        dataXyzh.setTotalTPrev(lastData.getTotalT());
        if (!auto){
            dataXyzh.setHa(0.0);
            dataXyzh.setVa(0.0);
            dataXyzh.setSd(0.0);
        }
    }
    // endregion

    // region 公用方法
    /**
     * 获取时间间隔天数
     */
    public static double getDeltTime(ProMission mission, Date currentDate, Date lastDate) {
        double deltTime = 0.0;
        if (currentDate == null || lastDate == null) {
            return deltTime;
        }
        double dayNum = (currentDate.getTime() - lastDate.getTime()) * 1.0 / (1000 * 60 * 60 * 24);
        if (mission!=null && "1".equals(mission.getCalculateType()))
        {
            deltTime = dayNum;
        }
        else if (mission!=null && "2".equals(mission.getCalculateType()))
        {
            deltTime = Math.ceil(dayNum);
        }
        else if (mission!=null && "3".equals(mission.getCalculateType()))
        {
            deltTime = dayNum < 1.0 ? dayNum : Math.ceil(dayNum);
        }else {
            deltTime = Math.ceil(dayNum);
        }
        return Math.round(deltTime * 100)*1.0 / 100;
    }

    /**
     * 坐标旋转变换 获取观测值
     **/
    public static List<Double> coordTransfer(double x, double y, double alpha){
        // 度分表转弧度
        double rad = Angle.dms2rad(alpha);
        List<Double> list = new ArrayList<>();
        list.add(0, x);
        list.add(1, y);
        BaseFunction.coordTransfer(rad, list);
        return list;
    }
    // endregion

    // region 私有方法
    /**
     * 获取三维细项分类
     */
    private static String getDetailType(int i) {
        switch (i)
        {
            case 0:
                return "X";
            case 1:
                return "Y";
            case 2:
                return "Z";
            case 3:
                return "P";
            case 4:
                return "S";
            case 5:
                return "T";
            default:
                return "";
        }
    }

    /**
     * 过滤结果类型
     **/
    private static List<AlarmItem> filterAlarmItem(int i, List<AlarmItem> totalList) {
        switch (i)
        {
            case 0:
                return totalList.stream().filter(item -> "X坐标".equals(item.getResultType())).collect(Collectors.toList());
            case 1:
                return totalList.stream().filter(item -> "Y坐标".equals(item.getResultType())).collect(Collectors.toList());
            case 2:
                return totalList.stream().filter(item -> "Z坐标".equals(item.getResultType())).collect(Collectors.toList());
            case 3:
                return totalList.stream().filter(item -> "平面位移(P)".equals(item.getResultType())).collect(Collectors.toList());
            case 4:
                return totalList.stream().filter(item -> "平行断面位移(S)".equals(item.getResultType())).collect(Collectors.toList());
            case 5:
                return totalList.stream().filter(item -> "垂直断面位移(T)".equals(item.getResultType())).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 过滤单项指定类型报警项
     **/
    public static List<AlarmItem> filterAlarmItem2(int i, List<AlarmItem> totalList) {
        switch (i)
        {
            case 0:
                return totalList.stream().filter(item -> "X坐标".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            case 1:
                return totalList.stream().filter(item -> "Y坐标".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            case 2:
                return totalList.stream().filter(item -> "Z坐标".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            case 3:
                return totalList.stream().filter(item -> "平面位移(P)".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            case 4:
                return totalList.stream().filter(item -> "平行断面位移(S)".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            case 5:
                return totalList.stream().filter(item -> "垂直断面位移(T)".equals(item.getResultType()) && 1==item.getAlarmType()).collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 获取验证数值
     */
    private static double getCompareValue2(PointDataXyzhVO dataXyzh, String type, int index) {
        switch (type)
        {
            case "累计变化量":
                switch (index)
                {
                    case 0:
                        return dataXyzh.getTotalX() == null ? 0.0 : dataXyzh.getTotalX();
                    case 1:
                        return dataXyzh.getTotalY() == null ? 0.0 : dataXyzh.getTotalY();
                    case 2:
                        return dataXyzh.getTotalZ() == null ? 0.0 : dataXyzh.getTotalZ();
                    case 3:
                        return dataXyzh.getTotalP() == null ? 0.0 : dataXyzh.getTotalP();
                    case 4:
                        return dataXyzh.getTotalS() == null ? 0.0 : dataXyzh.getTotalS();
                    case 5:
                        return dataXyzh.getTotalT() == null ? 0.0 : dataXyzh.getTotalT();
                    default:
                        return 0.0;
                }
            case "单次变化量":
                switch (index)
                {
                    case 0:
                        return dataXyzh.getDeltX() == null ? 0.0 : dataXyzh.getDeltX();
                    case 1:
                        return dataXyzh.getDeltY() == null ? 0.0 : dataXyzh.getDeltY();
                    case 2:
                        return dataXyzh.getDeltZ() == null ? 0.0 : dataXyzh.getDeltZ();
                    case 3:
                        return dataXyzh.getDeltP() == null ? 0.0 : dataXyzh.getDeltP();
                    case 4:
                        return dataXyzh.getDeltS() == null ? 0.0 : dataXyzh.getDeltS();
                    case 5:
                        return dataXyzh.getDeltT() == null ? 0.0 : dataXyzh.getDeltT();
                    default:
                        return 0.0;
                }
            case "日变化速率":
                switch (index)
                {
                    case 0:
                        return dataXyzh.getVDeltX() == null ? 0.0 : dataXyzh.getVDeltX();
                    case 1:
                        return dataXyzh.getVDeltY() == null ? 0.0 : dataXyzh.getVDeltY();
                    case 2:
                        return dataXyzh.getVDeltZ() == null ? 0.0 : dataXyzh.getVDeltZ();
                    case 3:
                        return dataXyzh.getVDeltP() == null ? 0.0 : dataXyzh.getVDeltP();
                    case 4:
                        return dataXyzh.getVDeltS() == null ? 0.0 : dataXyzh.getVDeltS();
                    case 5:
                        return dataXyzh.getVDeltT() == null ? 0.0 : dataXyzh.getVDeltT();
                    default:
                        return 0.0;
                }
            case "测量值":
                switch (index)
                {
                    case 0:
                        return dataXyzh.getX() == null ? 0.0 : dataXyzh.getX();
                    case 1:
                        return dataXyzh.getY() == null ? 0.0 : dataXyzh.getY();
                    case 2:
                        return dataXyzh.getZ() == null ? 0.0 : dataXyzh.getZ();
                    case 3:
                        return dataXyzh.getP() == null ? 0.0 : dataXyzh.getP();
                    case 4:
                        return dataXyzh.getS() == null ? 0.0 : dataXyzh.getS();
                    case 5:
                        return dataXyzh.getT() == null ? 0.0 : dataXyzh.getT();
                    default:
                        return 0.0;
                }
            default:
                return 0.0;
        }
    }

    /**
     * 计算S和T值
     **/
    private static void setFirstPsAndT(PointDataXyzhVO dataXyzh, List<Point> points) {
        double xValue = dataXyzh.getX() == null ? 0.0 : dataXyzh.getX();
        double yValue = dataXyzh.getY() == null ? 0.0 : dataXyzh.getY();
        double sqrt = Math.sqrt(xValue * xValue + yValue * yValue);
        dataXyzh.setP(Math.round(sqrt * 100000000.0) / 100000000.0);
        Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataXyzh.getPid())).findAny();
        double azimuth = optional.map(Point::getAzimuth).orElse(0.0);
        List<Double> doubleXy = coordTransfer(xValue, yValue, azimuth);
        double destX = doubleXy.get(0);
        double destY = doubleXy.get(1);
        dataXyzh.setS00(Math.round(destX * 100000000.0) / 100000000.0);
        dataXyzh.setT00(Math.round(destY * 100000000.0) / 100000000.0);
        dataXyzh.setS(0.0);
        dataXyzh.setT(0.0);
    }

    /**
     * 计算S和T值
     **/
    private static void setNewPsAndT(PointDataXyzhVO dataXyzh, List<Point> points) {
        double xValue = dataXyzh.getX() == null ? 0.0 : dataXyzh.getX();
        double yValue = dataXyzh.getY() == null ? 0.0 : dataXyzh.getY();
        double sqrt = Math.sqrt(xValue * xValue + yValue * yValue);
        dataXyzh.setP(Math.round(sqrt * 100000000.0) / 100000000.0);
        Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataXyzh.getPid())).findAny();
        double azimuth = optional.map(Point::getAzimuth).orElse(0.0);
        List<Double> doubleXy = coordTransfer(xValue, yValue, azimuth);
        double destX = doubleXy.get(0);
        double destY = doubleXy.get(1);
        dataXyzh.setS(Math.round((destX - dataXyzh.getS00()) * 100000000.0) / 100000000.0);
        dataXyzh.setT(Math.round((destY - dataXyzh.getT00()) * 100000000.0) / 100000000.0);
    }
    // endregion

}
