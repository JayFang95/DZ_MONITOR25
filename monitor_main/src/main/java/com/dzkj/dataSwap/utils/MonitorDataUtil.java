package com.dzkj.dataSwap.utils;

import com.dzkj.common.Angle;
import com.dzkj.common.util.DateUtil;
import com.dzkj.dataSwap.bean.monitor_data.OriData;
import com.dzkj.dataSwap.bean.process_data.ProcessData;
import com.dzkj.dataSwap.vo.OriOffsetInfoVO;
import com.dzkj.entity.survey.RobotSurveyData;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 17:18
 * @description 采集原始数据操作工具类
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class MonitorDataUtil {

    /**
     * 从测量记录数据中获取原始位移信息和过程数据集合
     * @param rawData 测量数据字符串
     * @param oriOffsetInfoList 原始位移信息
     * @param uploadProcessDataList 过程数据集合
     */
    public static void getOriOffsetInfoListAndProcessDataList(String rawData
            , List<String> oriOffsetInfoList, List<String> uploadProcessDataList){
        List<String> filterList = Arrays.stream(rawData.split(";"))
                .filter(item -> item.contains("Measure,")).collect(Collectors.toList());
        int seq = 1;
        for (String data : filterList) {
            //拼接原始数据
            String[] dataStrs = data.split(",");
            String pCode = dataStrs[1];
            int ha = (int) (Double.parseDouble(dataStrs[5]) * 180 * 360000 / Math.PI);
            int va = (int) (Double.parseDouble(dataStrs[6]) * 180 * 360000 / Math.PI);
            int sd = (int) (Double.parseDouble(dataStrs[7]) * 100000);
            int loop = Integer.parseInt(dataStrs[8]);
            int dir = Integer.parseInt(dataStrs[9]);
            String result = dataStrs[10];
            String timeStr = dataStrs[11];
            String oriOffsetInfo = pCode + "," + ha + "," + va + "," + sd + "," + loop + "," + dir;

            //拼接过程数据
            if ("0".equals(result))
            {
                result += "," + oriOffsetInfo;
            }
            String uploadProcessData = seq + "," + timeStr + "," + result;
            uploadProcessDataList.add(uploadProcessData);
            seq++;

            //拼接原始数据
            oriOffsetInfo += "," + timeStr;
            oriOffsetInfoList.add(oriOffsetInfo);
        }
    }

    /**
     * 从测量记录数据中获取页面展示原始位移信息
     *
     * @param rawData        测量数据字符串
     * @param oriOffsetInfos oriOffsetInfos
     */
    public static void getShowOriOffsetInfos(String rawData, List<OriOffsetInfoVO> oriOffsetInfos){
        List<String> filterList = Arrays.stream(rawData.split(";"))
                .filter(item -> item.contains("Measure,")).collect(Collectors.toList());
        for (String data : filterList) {
            OriOffsetInfoVO infoVO = new OriOffsetInfoVO();
            //拼接原始数据
            String[] dataStrs = data.split(",");
            infoVO.setPtCode(dataStrs[1]);
            infoVO.setHa(new Angle(Double.parseDouble(dataStrs[5]), 1, false).showDmmss(2));
            infoVO.setVa(new Angle(Double.parseDouble(dataStrs[6]), 1, false).showDmmss(2));
            infoVO.setSd(Double.parseDouble(dataStrs[7]));
            infoVO.setLoop(Integer.parseInt(dataStrs[8]));
            infoVO.setDir(Integer.parseInt(dataStrs[9]));

            oriOffsetInfos.add(infoVO);
        }
    }

    /**
     * 获取过程数据集合
     *
     * @param rawData         获取过程数据集合
     * @param processDataList processDataList
     */
    public static void getProcessDataList(String rawData, String projectCode, List<ProcessData> processDataList){
        List<String> filterList = Arrays.stream(rawData.split(";"))
                .filter(item -> item.contains("Measure,")).collect(Collectors.toList());
        int seq = processDataList.size() + 1;
        for (String data : filterList) {
            ProcessData processData = new ProcessData();
            processData.setProjectCode(projectCode);
            //获取原始数据
            String[] dataStrs = data.split(",");
            processData.setPCode(dataStrs[1]);
            String result = dataStrs[10];
            //测量成功时添加原始数据
            if ("0".equals(result)){
                processData.setHAngle((int) (Double.parseDouble(dataStrs[5]) * 180 * 360000 / Math.PI));
                processData.setVAngle((int) (Double.parseDouble(dataStrs[6]) * 180 * 360000 / Math.PI));
                processData.setDistance((int) (Double.parseDouble(dataStrs[7]) * 100000));
                processData.setLoop(Integer.parseInt(dataStrs[8]));
                processData.setDir(Integer.parseInt(dataStrs[9]));
            }
            processData.setResult(Integer.parseInt(result));
            processData.setDataTime(DateUtil.getDate(dataStrs[11], DateUtil.yyyy_MM_dd_HH_mm_ss_EN));
            processData.setSeq(seq);

            processDataList.add(processData);
            seq++;
        }
    }

    /**
     * 从测量记录数据中获取测点上报原始数据信息
     *
     * @param rawData 测量数据字符串
     */
    public static void getUploadMonitorDataOriDataList(RobotSurveyData surveyData, List<OriData> oriOffsetInfos){
        List<String> filterList = Arrays.stream(surveyData.getRawData().split(";"))
                .filter(item -> item.contains("Measure,")).collect(Collectors.toList());
        for (String data : filterList) {
            OriData info = new OriData();
            //拼接原始数据
            String[] dataStrs = data.split(",");
            info.setPCode(dataStrs[1]);
            info.setHAngle((int) (Double.parseDouble(dataStrs[5]) * 180 * 360000 / Math.PI));
            info.setVAngle((int) (Double.parseDouble(dataStrs[6]) * 180 * 360000 / Math.PI));
            info.setDistance((int) (Double.parseDouble(dataStrs[7]) * 100000));
            info.setLoop(Integer.parseInt(dataStrs[8]));
            info.setDir(Integer.parseInt(dataStrs[9]));
            info.setDataTime(DateUtil.getDate(dataStrs[11], DateUtil.yyyy_MM_dd_HH_mm_ss_EN));

            oriOffsetInfos.add(info);
        }
    }

}
