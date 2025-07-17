package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
import com.dzkj.biz.data.IDataBiz;
import com.dzkj.biz.data.common.BaseDataBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.biz.project.vo.ProMissionVO;
import com.dzkj.biz.vo.AppMsgVO;
import com.dzkj.biz.vo.TextCardVO;
import com.dzkj.biz.vo.TextVO;
import com.dzkj.biz.wx.vo.ActivityMsgVO;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.util.*;
import com.dzkj.entity.alarm_setting.AlarmDistribute;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.data.*;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.param_set.SensorZl;
import com.dzkj.entity.param_set.TypeZl;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.entity.survey.RobotSurveyControl;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.entity.system.Company;
import com.dzkj.entity.system.Groups;
import com.dzkj.entity.system.MonitorType;
import com.dzkj.entity.system.User;
import com.dzkj.robot.survey.SurveyBiz;
import com.dzkj.service.alarm_setting.IAlarmDistributeService;
import com.dzkj.service.alarm_setting.IAlarmInfoCorrectService;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.data.*;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.param_set.ISensorZlService;
import com.dzkj.service.param_set.ITypeZlService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.survey.IRobotSurveyControlService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IGroupService;
import com.dzkj.service.system.IMonitorTypeService;
import com.dzkj.service.system.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
@Slf4j
public class DataBizImpl implements IDataBiz {

    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private ISensorZlService sensorZlService;
    @Autowired
    private ITypeZlService typeZlService;
    @Autowired
    private IAlarmItemService alarmItemService;
    @Autowired
    private IJcInfoService jcInfoService;
    @Autowired
    private IPointDataZService pointDataZService;
    @Autowired
    private IPointDataZRealService dataZRealService;
    @Autowired
    private IPointDataZlService pointDataZlService;
    @Autowired
    private IPointDataXyzService pointDataXyzService;
    @Autowired
    private IPointDataXyzhService pointDataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IAlarmDistributeService distributeService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IMonitorTypeService monitorTypeService;
    @Autowired
    private WeiXinUtil weiXinUtil;
    @Autowired
    private QwUtil qwUtil;
    @Autowired
    private IRobotSurveyControlService surveyControlService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IPushTaskOtherService pushTaskOtherService;
    @Autowired
    private IPointDataXyzhCorrectService dataXyzhCorrectService;
    @Autowired
    private IAlarmInfoCorrectService infoCorrectService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // region 数据上传
    @Override
    public ResponseUtil upload(UploadData uploadData, MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null){
            return ResponseUtil.failure(500, "文件不存在");
        }
        if (!(filename.endsWith(".xlsx") || filename.endsWith(".xls"))){
            return ResponseUtil.failure(500, "上传文件格式错误:请选择excel表格");
        }
        // 验证导入表格命名是否正确
        String info = checkFilename(filename, uploadData);
        if(StringUtils.isNotEmpty(info)){
            return ResponseUtil.failure(500, info);
        }
        try {
            InputStream inputStream = file.getInputStream();
            Workbook wb;
            if (filename.endsWith(".xlsx")){
                wb = new XSSFWorkbook(inputStream);
            }else {
                wb = new HSSFWorkbook(inputStream);
            }
            // 表名称验证
            if (!uploadData.getMissionName().equals(wb.getSheetAt(0).getSheetName()))
            {
                return ResponseUtil.failure(500, "导入表格的表格名称和当前监测任务名不一致");
            }
            Sheet sheet = wb.getSheetAt(0);
            // 读取表格数据
            switch (uploadData.getType()){
                case MissionTypeConst.MANUAL_PATROL:
                    return getXsData(sheet, uploadData);
                case MissionTypeConst.SX_H_OFFSET:
                case MissionTypeConst.SP_HD_OFFSET:
                case MissionTypeConst.QX_OFFSET:
                    return getSxData(sheet, uploadData);
                case MissionTypeConst.SP_XY_OFFSET:
                    return getSpxyData(sheet, uploadData);
                case MissionTypeConst.SP_DEEP_OFFSET:
                    return getSpfcData(sheet, uploadData);
                case MissionTypeConst.ZC_FORCE:
                    return getZlData(sheet, uploadData);
                case MissionTypeConst.HAND_XYZ_OFFSET:
                case MissionTypeConst.AUTO_XYZ_OFFSET:
                    return getXyzData(sheet, uploadData);
                default:
                    return ResponseUtil.failure(500, "监测任务类型不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.failure(500, "读取excel数据异常");
        }
    }

    /**
     *  获取支撑轴力表格数据
     **/
    private ResponseUtil getZlData(Sheet sheet, UploadData uploadData) {
        try {
            List<Point> points = pointService.queryByMissionId(uploadData.getMissionId());
            UploadDataCb dataCb = new UploadDataCb();
            dataCb.setType(uploadData.getType());
            List<PointDataZVO> dataZList = new ArrayList<>();
            List<PointDataZlVO> dataZlList = new ArrayList<>();
            List<TablePtData> tablePtList = new ArrayList<>();
            List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
            // 读取数据 : PID  测点名 传感器编号 位置 观测值(Hz) 温度(℃)	测点状态	传感器状态	时间	备注
            boolean checkTime = false;
            for (int i = 2; i <= sheet.getLastRowNum() ; i++) {
                PointDataZVO dataZ = new PointDataZVO();
                String pid = ExcelUtil.getStringValue(sheet, i, 0);
                String name = ExcelUtil.getStringValue(sheet, i, 1);
                String jxgCode = ExcelUtil.getStringValue(sheet, i, 2);
                String local = ExcelUtil.getStringValue(sheet, i, 3);
                String z = ExcelUtil.getStringValue(sheet, i, 4);
                String temp = ExcelUtil.getStringValue(sheet, i, 5);
                String ptStatus = ExcelUtil.getStringValue(sheet, i, 6);
                String sensorStatus = ExcelUtil.getStringValue(sheet, i, 7);
                DateCell dateCell = ExcelUtil.getDateValue(sheet, i, 8);
                String note = ExcelUtil.getStringValue(sheet, i, 9);
                // 表格数据验证
                if (StringUtils.isEmpty(pid) && StringUtils.isEmpty(name) && StringUtils.isEmpty(jxgCode) && StringUtils.isEmpty(local) &&
                        StringUtils.isEmpty(z) && StringUtils.isEmpty(temp) && StringUtils.isEmpty(ptStatus)  && StringUtils.isEmpty(sensorStatus)
                        && dateCell.getGetTime()==null && StringUtils.isEmpty(note)){
                    break;
                }
                // 表格列数据格式检查
                String checkInfo = checkZlData(dataZ, pid, name, jxgCode, local, z, temp, ptStatus, sensorStatus, dateCell, note, i, tablePtList);
                if (StringUtils.isNotEmpty(checkInfo)){
                    return ResponseUtil.failure(500, checkInfo);
                }
                // 观测时间有效性验证
                if(!checkTime && dataZ.getGetTime()!=null){
                    CheckTimeVO check = checkPtTime(dataZ, uploadData);
                    checkTime = check.isChecked();
                    if (StringUtils.isNotEmpty(check.getCheckInfo())){
                        return ResponseUtil.failure(500, check.getCheckInfo());
                    }
                }
                // 添加表格数据
                TablePtData ptData = new TablePtData();
                ptData.setPid(dataZ.getPid()).setName(name).setZ(dataZ.getZ()).setJxgCode(jxgCode)
                        .setTemp(temp).setLocation(local).setSensorStatus(sensorStatus)
                        .setStatus(dataZ.getStatus()).setGetTime(dataZ.getGetTime()).setNote(dataZ.getNote());
                tablePtList.add(ptData);
                // ZL数据
                PointDataZlVO dataZl = new PointDataZlVO();
                dataZl.setPid(ptData.getPid()).setName(ptData.getName()).setPointStatus(ptData.getStatus())
                        .setSensorStatus(ptData.getSensorStatus()).setJxgCode(jxgCode).setLocation(local).setNote(note)
                        .setGetTime(ptData.getGetTime()).setF(dataZ.getZ());
                if (StringUtils.isNotEmpty(temp)){
                    dataZl.setTemp(Double.parseDouble(temp));
                }
                dataZlList.add(dataZl);
                dataZList.add(dataZ);
            }
            if (dataZList.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            // 相同PID校验
            List<Long> pidList = tablePtList.stream().map(TablePtData::getPid).distinct().collect(Collectors.toList());
            for (Long pid : pidList) {
                List<TablePtData> collect = tablePtList.stream().filter(item -> item.getPid().equals(pid)).collect(Collectors.toList());
                long count = collect.stream().filter(item -> "正常".equals(item.getStatus())).count();
                //②e.“测点状态”不是“停测”时，传感器状态至少有一个是“正常”；
                if("停测".equals(collect.get(0).getStatus()) && count==0){
                    return ResponseUtil.failure(500, "测点状态不是停测时，至少一个传感器状态为正常");
                }
                long count2 = collect.stream().filter(item -> item.getZ() == null).count();
                long count3 = collect.stream().filter(item -> item.getZ() != null).count();
                //⑤b.相同PID的观测值都是空，测点状态“停测”，传感器状态 空，观测时间必须是空，备注不能是空；
                if(count2 == collect.size()){
                    for (TablePtData data : collect) {
                        if (!"停测".equals(data.getStatus()) || StringUtils.isNotEmpty(data.getSensorStatus())
                                || data.getGetTime()!=null || StringUtils.isEmpty(data.getNote())){
                            return ResponseUtil.failure(500, "相同PID的观测值都是空，测点状态只能是停测，传感器状态必须是空，观测时间必须是空，备注不能是空");
                        }
                    }
                }
                //⑤b如果相同PID有观测值非空，则测点状态不能是“停测”，传感器状态必须是“破坏”，观测时间必须是空
                if (count2>0 && count3>0) {
                    for (TablePtData data : collect) {
                        if ("停测".equals(data.getStatus()) || !"破坏".equals(data.getSensorStatus()) || data.getGetTime()!=null){
                            return ResponseUtil.failure(500, "相同PID有观测值非空，则测点状态不能是停测，传感器状态必须是破坏，观测时间必须是空");
                        }
                    }
                }
            }
            // 测点有效性验证
            String result = checkZlDb(dataZlList, points, uploadData);
            if (StringUtils.isNotEmpty(result)){
                return ResponseUtil.failure(500, result);
            }
            // 计算轴力值
            List<PointDataZVO> dataZResult = calculateZ(uploadData, dataZlList, alarmInfoList, points);
            // 中间数据计算
            dataZResult.forEach(item -> calculateData(item, uploadData, alarmInfoList));
            dataCb.setPtDataZ(dataZResult);
            dataCb.setPtDataZl(dataZlList);
            dataCb.setAlarmInfoList(alarmInfoList);
            dataCb.setTableList(tablePtList);
            return ResponseUtil.success(dataCb);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel表格出现异常");
        }
    }

    /**
     * 支撑轴力数据与数据库验证
     */
    private String checkZlDb(List<PointDataZlVO> dataZlList, List<Point> points, UploadData uploadData) {
        for (PointDataZlVO dataZl : dataZlList) {
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataZl.getPid())).findAny();
            //⑦PID正确性检查 与系统已存在的PID比对，检查PID是否有误；
            if (!optional.isPresent()){
                return "PID: "+dataZl.getPid()+" 不存在";
            }else {
                Point point = optional.get();
                //⑧PID对应测点名与系统中同名PID挂接的测点名比对，检查测点名是否有误；
                if (!point.getName().equals(dataZl.getName())){
                    return "PID: "+dataZl.getPid()+" 对应测点深度: "+dataZl.getName()+" 和系统不一致";
                }
                //⑨PID对应测点状态 如果系统中的“测点停测 = true”，则从表格获取的“测点状态”必须是“停测”；
                // 如果“测点停测 = false”，则从表格获取的“测点状态”不能是“停测”；
                if ((point.getStop() && !"停测".equals(dataZl.getPointStatus()))
                        || (!point.getStop() && "停测".equals(dataZl.getPointStatus())))
                {
                    return "PID: "+dataZl.getPid()+" 对应测点状态和系统状态不一致";
                }
                //⑩传感器编号唯一性检查 在同一工程项目内唯一；
                List<SensorZl> zls = sensorZlService.getListByMissionId(uploadData.getMissionId());
                Optional<SensorZl> sensorZl = zls.stream().filter(item -> item.getJxgCode().equals(dataZl.getJxgCode())).findAny();
                if (!sensorZl.isPresent()){
                    return "传感器编号 ["+dataZl.getJxgCode()+"] 不存在";
                }
                //⑪传感器编号对应传感器状态正确性检查
                //“破坏 = true”时，则从表格获取的“传感器状态”必须是“破坏”；
                //如果“破坏 = false”，则从表格获取的“传感器状态”必须是“正常”；
                SensorZl sensor = sensorZl.get();
                if ((sensor.getBroken() && !"破坏".equals(dataZl.getSensorStatus()))
                        || (!sensor.getBroken() && !"正常".equals(dataZl.getSensorStatus()))){
                    return "编号: "+dataZl.getJxgCode()+" 传感器状态和系统不一致";
                }
            }
        }
        return null;
    }

    /**
     * 计算轴力值
    **/
    private List<PointDataZVO> calculateZ(UploadData uploadData, List<PointDataZlVO> dataZlList, List<AlarmInfoVO> alarmInfoList, List<Point> points) {
        ArrayList<PointDataZVO> list = new ArrayList<>();
        List<SensorZl> sensors = sensorZlService.getListByMissionId(uploadData.getMissionId());
        List<Long> idList = dataZlList.stream().map(PointDataZlVO::getPid).distinct().collect(Collectors.toList());
        for (Long pid : idList) {
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(pid)).findAny();
            List<PointDataZlVO> filterList = dataZlList.stream().filter(item -> item.getPid().equals(pid)).collect(Collectors.toList());
            // 获取dataZ数据
            PointDataZVO dataZ = new PointDataZVO();
            dataZ.setPid(pid)
                    .setName(filterList.get(0).getName())
                    .setStatus(filterList.get(0).getPointStatus())
                    .setNote(filterList.get(0).getNote());
            optional.ifPresent(point -> dataZ.setStop(point.getStop()));
            Optional<PointDataZlVO> any = filterList.stream().filter(item -> item.getGetTime() != null).findAny();
            any.ifPresent(pointDataZlVO -> dataZ.setGetTime(pointDataZlVO.getGetTime()));
            if (optional.isPresent() && optional.get().getStop()){
                list.add(dataZ);
                continue;
            }
            // 计算观测值
            List<Double> zList = new ArrayList<>();
            for (PointDataZlVO data : filterList) {
                if ("破坏".equals(data.getSensorStatus()) || "停测".equals(data.getSensorStatus())){continue;}
                Optional<SensorZl> sensorZl = sensors.stream().filter(tem -> tem.getJxgCode().equals(data.getJxgCode())).findFirst();
                Long typeId = sensorZl.isPresent() ? sensorZl.get().getTypeZlId() : 0;
                TypeZl typeZl = typeZlService.getById(typeId);
                if (typeZl == null) {continue;}
                double f = data.getF() == null ? 0.0 : data.getF();
                double k = sensorZl.isPresent() ? sensorZl.get().getCalibration() : 1;
                if ("钢支撑轴力".equals(typeZl.getType())){
                    //z=-0.206K * F * [(r/2)²-(r/2-th)²]π
                    double z = -0.206 * k * f
                            * (Math.pow(typeZl.getR()*1.0 / 2, 2) - Math.pow(typeZl.getR()*1.0 / 2 - typeZl.getTh(), 2))
                            * Math.PI;
                    zList.add(Math.round(z * 100000000.0) / 100000000.0);
                }else if ("混凝土支撑轴力".equals(typeZl.getType())){
                    //z=-1000K * F * ACEC_ASES /(A1 * ES)
                    double z = -1000 * k * f
                            * typeZl.getAcecAses() / ((double)typeZl.getA() * typeZl.getEs());
                    zList.add(Math.round(z * 100000000.0) / 100000000.0);
                }else {
                    //z=-1000K * F
                    double z = -1000 * k * f;
                    zList.add(Math.round(z * 100000000.0) / 100000000.0);
                }
            }
            if (zList.size() > 0){
                double tempZ = zList.stream().mapToDouble(z -> z).sum();
                dataZ.setZ(Math.round(tempZ / zList.size() * 100000000.0) / 100000000.0);
                list.add(dataZ);
            }
        }
        return list;
    }

    /**
     * 支撑轴力表格数据验证
     */
    private String checkZlData(PointDataZVO dataZ, String pid, String name, String jxgCode, String local, String z,
                               String temp, String ptStatus, String sensorStatus, DateCell dateCell, String note, int i,
                               List<TablePtData> tablePtList) {
        List<String> statusList = Arrays.asList("正常", "停测", "破坏重埋", "基准点修正");
        List<String> statusList2 = Arrays.asList("正常", "破坏");
        //①非空检查 “PID”、“测点名”、“传感器编号”、“位置”、“测点状态”5列数值非空；
        if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(name) || StringUtils.isEmpty(jxgCode)
                || StringUtils.isEmpty(local) || StringUtils.isEmpty(ptStatus)){
            return "表格第" + (i+1) + "行数据错误: PID、测点名、传感器编号、位置、测点状态列不为空";
        }
        try {
            pid = pid.replace(".0","");
            dataZ.setPid(Long.valueOf(pid));
        }catch (Exception e){
            return "表格第" + (i+1) + "行数据错误: PID只能是整数数值";
        }
        //②测点状态有效性检查和传感器状态有效性检查
        //a.“测点状态”列的值只能是“正常,停测,破坏重埋和基准点修正”4项中的内容；
        if(!statusList.contains(ptStatus)){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能是[正常,停测,破坏重埋和基准点修正]";
        }
        //b.“传感器状态”列只能是“正常，破坏”2项中的内容；
        if(StringUtils.isNotEmpty(sensorStatus) && !statusList2.contains(sensorStatus)){
            return "表格第" + (i+1) + "行数据错误: 传感器状态只能是[正常,破坏]";
        }
        //c.相同PID的“测点状态”必须相同；
        List<TablePtData> dataList = tablePtList.stream().filter(item -> item.getPid().equals(dataZ.getPid())).collect(Collectors.toList());
        if (dataList.size()>0 && !dataList.get(0).getStatus().equals(ptStatus)){
            return "表格第" + (i+1) + "行数据错误: 相同PID的测点状态必须相同";
        }
        //d.“测点状态”为“停测”时，传感器状态必须是空；
        if("停测".equals(ptStatus) && StringUtils.isNotEmpty(sensorStatus)){
            return "表格第" + (i+1) + "行数据错误: 测点状态为停测时传感器状态必须为空";
        }
        // ③观测时间 列的非空值是日期时间格式；
        if (StringUtils.isNotEmpty(dateCell.getInfo())){
            return dateCell.getInfo();
        }
        dataZ.setGetTime(dateCell.getGetTime());
        // ④观测值 列的值除空值外，都是数值；
        if (StringUtils.isNotEmpty(z)){
            try {
                dataZ.setZ(Double.valueOf(z));
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列只能是数值格式";
            }
            // ⑤观测值、测点状态、传感器状态、观测时间和备注等5列内容逻辑有效性检查
            // a.“观测值”非空，测点状态不能是“停测”，传感器状态必须是“正常”，必须有观测时间；
            if ("停测".equals(ptStatus) || !"正常".equals(sensorStatus) || dateCell.getGetTime()==null){
                return "表格第" + (i+1) + "行数据错误: 观测值非空时，测点状态不能为停测，传感器状态必须是正常，观测时间不能为空";
            }
        }
        // ⑥PID有效性检查
        // a.PID相同的点名相同；
        if (dataList.size()>0 && !dataList.get(0).getName().equals(name)){
            return "表格第" + (i+1) + "行数据错误: PID相同的点名相同";
        }
        // b.点名相同的PID相同；
        List<TablePtData> dataList2 = tablePtList.stream().filter(item -> item.getName().equals(name)).collect(Collectors.toList());
        if (dataList2.size()>0 && !dataList2.get(0).getPid().equals(dataZ.getPid())){
            return "表格第" + (i+1) + "行数据错误: 点名相同的PID相同";
        }
        // ⑩传感器编号唯一性检查
        long count = tablePtList.stream().filter(item -> item.getJxgCode().equals(jxgCode)).count();
        if(count > 0 ){
            return "表格第" + (i+1) + "行数据错误: 传感器编号["+jxgCode+"]工程内不唯一";
        }
        // 温度格式验证
        if (StringUtils.isNotEmpty(temp)){
            try {
                Double.parseDouble(temp);
            }catch (Exception e){
                return "表格第" + (i+1) + "行数据错误: 温度列非空时只能是数值";
            }
        }
        dataZ.setName(name).setStatus(ptStatus).setNote(note);
        return null;
    }

    /**
     *  获取水平分层表格数据
     **/
    private ResponseUtil getSpfcData(Sheet sheet, UploadData uploadData) {
        try {
            List<Point> points = pointService.queryByMissionId(uploadData.getMissionId());
            UploadDataCb dataCb = new UploadDataCb();
            dataCb.setType(uploadData.getType());
            List<PointDataZVO> dataZList = new ArrayList<>();
            List<TablePtData> tablePtList = new ArrayList<>();
            List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
            // 读取数据 : PID 	测点名	深度(m)	观测值(m)	测点状态	观测时间	备注
            boolean checkTime = false;
            for (int i = 2; i <= sheet.getLastRowNum() ; i++) {
                PointDataZVO dataZ = new PointDataZVO();
                String pid = ExcelUtil.getStringValue(sheet, i, 0);
                String name = ExcelUtil.getStringValue(sheet, i, 1);
                String depth = ExcelUtil.getStringValue(sheet, i, 2);
                String z = ExcelUtil.getStringValue(sheet, i, 3);
                String status = ExcelUtil.getStringValue(sheet, i, 4);
                DateCell dateCell = ExcelUtil.getDateValue(sheet, i, 5);
                String note = ExcelUtil.getStringValue(sheet, i, 6);
                // 表格数据验证
                if (StringUtils.isEmpty(pid) && StringUtils.isEmpty(name) && StringUtils.isEmpty(depth) && StringUtils.isEmpty(z) &&
                        StringUtils.isEmpty(status) && StringUtils.isEmpty(note) && dateCell.getGetTime()==null){
                    break;
                }
                // 表格列数据格式检查
                String checkInfo = checkFcData(dataZ, pid, name, depth, z, status, dateCell, note, i, tablePtList, points);
                if (StringUtils.isNotEmpty(checkInfo)){
                    return ResponseUtil.failure(500, checkInfo);
                }
                // 观测时间有效性验证
                if(!checkTime && dataZ.getGetTime()!=null){
                    CheckTimeVO check = checkPtTime(dataZ, uploadData);
                    checkTime = check.isChecked();
                    if (StringUtils.isNotEmpty(check.getCheckInfo())){
                        return ResponseUtil.failure(500, check.getCheckInfo());
                    }
                }
                // 添加表格数据
                TablePtData ptData = new TablePtData();
                ptData.setPid(dataZ.getPid()).setName(name).setPtName(depth).setZ(dataZ.getZ())
                        .setStatus(dataZ.getStatus()).setGetTime(dataZ.getGetTime()).setNote(dataZ.getNote());
                tablePtList.add(ptData);
                dataZList.add(dataZ);
            }
            if (dataZList.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            // 测点有效性验证
            String result = checkFcDb(dataZList, points);
            if (StringUtils.isNotEmpty(result)){
                return ResponseUtil.failure(500, result);
            }
            // 中间数据计算
            dataZList.forEach(item -> calculateData(item, uploadData, alarmInfoList));
            dataCb.setPtDataZ(dataZList);
            dataCb.setAlarmInfoList(alarmInfoList);
            dataCb.setTableList(tablePtList);
            return ResponseUtil.success(dataCb);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel表格出现异常");
        }
    }

    /**
     * 获取水平位移(分层)数据与数据库验证
     **/
    private String checkFcDb(List<PointDataZVO> dataZList, List<Point> points) {
        for (PointDataZVO dataZ : dataZList) {
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataZ.getPid())).findAny();
            //⑦PID正确性检查 与系统已存在的PID比对，检查PID是否有误；
            if (!optional.isPresent()){
                return "PID: "+dataZ.getPid()+" 不存在";
            }else {
                //⑧PID对应测点名、深度值正确性检查 与系统中同名PID挂接的测点名、深度值比对，检查测点名、深度值是否有误；
                Point point = optional.get();
                String depthValue = point.getName().contains(".") ? point.getName() : point.getName() + ".0";
                if (!dataZ.getName().equals(depthValue)){
                    return "表格数据错误: PID["+dataZ.getPid()+"]对应深度值和系统不一致";
                }
                if (!dataZ.getGpName().equals(point.getPtGroupName())){
                    return "表格数据错误: PID["+dataZ.getPid()+"]对应测点名和系统不一致";
                }
                dataZ.setStop(false);
            }
        }
        return null;
    }

    /**
     * 水平分层数据列验证
     */
    private String checkFcData(PointDataZVO dataZ, String pid, String name, String depth, String z, String status, DateCell dateCell, String note, int i, List<TablePtData> ptDataList, List<Point> points) {
        // ①非空检查 “PID”、“测点名”、“深度”、“观测值”、“测点状态”和“观测时间”6列数值非空；
        if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(name) || StringUtils.isEmpty(depth) || StringUtils.isEmpty(z)
                || StringUtils.isEmpty(status) || dateCell==null){
            return "表格第" + (i+1) + "行数据错误: 除备注外所有列不为空";
        }
        try {
            pid = pid.replace(".0","");
            dataZ.setPid(Long.valueOf(pid));
        }catch (Exception e){
            return "表格第" + (i+1) + "行数据错误: PID只能是整数数值";
        }
        dataZ.setGpName(name);
        // ②测点状态列的值只能是“正常”；
        if(!"正常".equals(status)){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能[正常]";
        }
        // ③观测时间列的非空值是日期时间格式；
        if (StringUtils.isNotEmpty(dateCell.getInfo())){
            return dateCell.getInfo();
        }
        dataZ.setGetTime(dateCell.getGetTime());
        //④深度值、观测值 列的值都是数值；
        try {
            Double.valueOf(depth);
            dataZ.setName(depth);
        }catch (Exception e){
            return  "表格第" + (i+1) + "行数据错误: 深度值列只能是数值格式";
        }
        try {
            dataZ.setZ(Double.valueOf(z));
        }catch (Exception e){
            return  "表格第" + (i+1) + "行数据错误: 观测值列只能是数值格式";
        }
        //⑤测点名 列的值可以不相同，但相同的测点名所挂接的“深度”值，必须唯一(即深度值不同)；
        List<TablePtData> collect2 = ptDataList.stream().filter(item ->
                item.getName().equals(dataZ.getName()) && item.getPtName().equals(dataZ.getZ() + "")).collect(Collectors.toList());
        if (collect2.size() > 0){
            return  "表格第" + (i+1) + "行数据错误: 相同测点深度值必须唯一";
        }
        //⑥PID唯一性检查 “PID”列数值全局唯一
        List<TablePtData> collect = ptDataList.stream().filter(item -> item.getPid().equals(dataZ.getPid())).collect(Collectors.toList());
        if(collect.size() > 0 ){
            return "表格第" + (i+1) + "行数据错误: PID值表格内重复";
        }
        dataZ.setStatus(status);
        dataZ.setNote(note);
        return null;
    }

    /**
     *  获取三维位移(XYZ)表格数据
     **/
    private ResponseUtil getXyzData(Sheet sheet, UploadData uploadData) {
        try {
            List<Point> points = pointService.queryByMissionId(uploadData.getMissionId());
            UploadDataCb dataCb = new UploadDataCb();
            dataCb.setType(uploadData.getType());
            List<PointDataXyzhVO> dataXyzhList = new ArrayList<>();
            List<PointDataZVO> dataZList = new ArrayList<>();
            List<TablePtData> tablePtList = new ArrayList<>();
            List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
            // 读取数据 :PID	测点名	x  y  z	 测点状态	观测时间	备注
            boolean checkTime = false;
            int dataIndex = MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType()) ? 3 : 2;
            List<Long> stConfigPidList = getUpdateStationPidList(uploadData);
            if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType()) && stConfigPidList.size() == 0){
                return ResponseUtil.failure(500, "当前测站未配置任何测点信息");
            }
            List<Long> pidList = new ArrayList<>();
            for (int i = dataIndex; i <= sheet.getLastRowNum() ; i++) {
                PointDataZVO dataZ = new PointDataZVO();
                String pid = ExcelUtil.getStringValue(sheet, i, 0);
                String name = ExcelUtil.getStringValue(sheet, i, 1);
                String x = ExcelUtil.getStringValue(sheet, i, 2);
                String y = ExcelUtil.getStringValue(sheet, i, 3);
                String z = ExcelUtil.getStringValue(sheet, i, 4);
                String status = ExcelUtil.getStringValue(sheet, i, 5);
                DateCell dateCell = ExcelUtil.getDateValue(sheet, i, 6);
                String note = ExcelUtil.getStringValue(sheet, i, 7);
                // 表格数据验证
                if (StringUtils.isEmpty(pid) && StringUtils.isEmpty(name) && StringUtils.isEmpty(x) && StringUtils.isEmpty(y)
                        && StringUtils.isEmpty(z) && StringUtils.isEmpty(status) && StringUtils.isEmpty(note) && dateCell.getGetTime()==null){
                    break;
                }
                // 表格列数据格式检查
                String checkInfo = checkSxData(dataZ, pid, name,x, y, z, status, dateCell, note, i, pidList, 2, points);
                if (StringUtils.isNotEmpty(checkInfo)){
                    return ResponseUtil.failure(500, checkInfo);
                }
                // 观测时间有效性验证
                if(!checkTime && dataZ.getGetTime()!=null){
                    CheckTimeVO check = checkPtTime2(dataZ, uploadData);
                    checkTime = check.isChecked();
                    if (StringUtils.isNotEmpty(check.getCheckInfo())){
                        return ResponseUtil.failure(500, check.getCheckInfo());
                    }
                }
                // ⑩全站仪自动监测(XYZ)测点PID是否在对应测点配置内检查
                if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType())
                        && !stConfigPidList.contains(dataZ.getPid()))
                {
                    return ResponseUtil.failure(500, "表第" + (i + 1) + "行数据错误: 测站未配置导入测点信息");
                }
                // 添加表格数据
                TablePtData ptData = DzBeanUtils.propertiesCopy(dataZ, TablePtData.class);
                PointDataXyzhVO dataXyzh = DzBeanUtils.propertiesCopy(dataZ, PointDataXyzhVO.class);
                if (StringUtils.isEmpty(x)){
                    ptData.setX(null);
                    dataXyzh.setX(0.0);
                }else {
                    ptData.setX(Double.parseDouble(x));
                    dataXyzh.setX(Double.parseDouble(x));
                }
                if (StringUtils.isEmpty(y)){
                    ptData.setY(null);
                    dataXyzh.setY(0.0);
                }else {
                    ptData.setY(Double.parseDouble(y));
                    dataXyzh.setY(Double.parseDouble(y));
                }
                if (StringUtils.isEmpty(z)){
                    ptData.setZ(null);
                    dataXyzh.setZ(0.0);
                }else {
                    ptData.setZ(Double.parseDouble(z));
                    dataXyzh.setZ(Double.parseDouble(z));
                }
                pidList.add(dataZ.getPid());
                tablePtList.add(ptData);
                dataXyzhList.add(dataXyzh);
                dataZList.add(dataZ);
            }
            if (dataXyzhList.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            // 测点有效性验证
            String result = checkSxDb(dataZList, points);
            if (StringUtils.isNotEmpty(result)){
                return ResponseUtil.failure(500, result);
            }
            // 中间数据计算
            dataXyzhList.forEach(item -> calculateDataXyz(item, uploadData, alarmInfoList, points));
            dataCb.setPtDataXyzh(dataXyzhList);
            dataCb.setAlarmInfoList(alarmInfoList);
            dataCb.setTableList(tablePtList);
            return ResponseUtil.success(dataCb);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel表格出现异常");
        }
    }

    /**
     * 获取测站包含的测点信息
    **/
    private List<Long> getUpdateStationPidList(UploadData uploadData) {
        List<Long> list = new ArrayList<>();
        if(!MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType())){
            return list;
        }
        String paramStr = uploadData.getParams();
        if (StringUtils.isEmpty(paramStr)) {
            return list;
        }
        String[] split = paramStr.split("\\|");
        if (split.length == 0) {
            return list;
        }
        String[] stSplit = split[0].split(";");
        if (stSplit.length == 1) {
            return list;
        }

        for (int i = 1; i < stSplit.length; i++)
        {
            if (StringUtils.isEmpty(stSplit[i])) {
                continue;
            }
            String[] pointSplit = stSplit[i].split(",");
            if (StringUtils.isNumeric(pointSplit[0])){
                list.add(Long.valueOf(pointSplit[0]));
            }
        }
        return list;
    }

    /**
     * 时间验证
     */
    private CheckTimeVO checkPtTime2(PointDataZVO dataZ, UploadData uploadData) {
        CheckTimeVO result = new CheckTimeVO();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String timeStr = format.format(dataZ.getGetTime());
        if (!timeStr.equals(uploadData.getDateTimeStr())){
            result.setChecked(false);
            result.setCheckInfo("表格第一个非空观测时间:"+timeStr+"和文件标题日期"+uploadData.getDateTimeStr()+"不一致");
            return result;
        }
        result.setChecked(true);
        // 时间滞后性验证
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataXyzh::getPid, dataZ.getPid())
                .ge(PointDataXyzh::getGetTime, dataZ.getGetTime());
        if (pointDataXyzhService.count(wrapper) > 0){
            result.setCheckInfo("监测时间滞后错误:PID["+dataZ.getPid() +"]存在["+format2.format(dataZ.getGetTime())+"]之后巡视记录");
        }
        return result;
    }

    /**
     * 计算三维位移中间数据
    **/
    private void calculateDataXyz(PointDataXyzhVO dataXyzh, UploadData uploadData, List<AlarmInfoVO> alarmInfoList, List<Point> points) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataXyzh::getPid, dataXyzh.getPid()).orderByDesc(PointDataXyzh::getGetTime);
        List<PointDataXyzh> list = pointDataXyzhService.list(wrapper);
        PointDataXyzh lastData  = null;
        if(list.size() > 0){
            lastData = list.get(0);
        }
        ProMission mission = missionService.findById(uploadData.getMissionId());
        if(lastData == null){
            // 设置初始值
            BaseDataBiz.setFirstData(dataXyzh, points, false);
        }else {
            // 设置初始值
            BaseDataBiz.setNewData(dataXyzh, lastData, mission, points, false);
        }
        if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType()))
        {
            List<RobotSurveyData> dataList = surveyDataService.getByMissionId(uploadData.getMissionId());
            dataXyzh.setRecycleNum(dataList.size() ==  0 ? 1 : dataList.get(0).getRecycleNum() + 1);
        }
        Optional<Point> point = points.stream().filter(item -> item.getId().equals(dataXyzh.getPid())).findAny();
        if (!point.isPresent()){
            return;
        }
        dataXyzh.setStop(point.get().getStop());
        if (dataXyzh.getStop()) { return; }
        // 验证测点是否超限，记录报警信息
        List<AlarmItem> alarmItems = alarmItemService.getByPid(dataXyzh.getPid());
        List<AlarmItem> items = alarmItems.stream().filter(item -> item.getMonitorType().equals(mission.getTypeName()))
                .collect(Collectors.toList());
        List<String> nameList = items.stream().filter(item -> item.getAlarmType() == 2).map(AlarmItem::getResultItemType)
                .distinct().collect(Collectors.toList());
        AtomicInteger atomicInteger = new AtomicInteger(0);
        StringBuilder sb = new StringBuilder();
        List<String> infoList = new ArrayList<>();
        BaseDataBiz.checkValueOverXyz(dataXyzh, items, "累计变化量", atomicInteger, sb, infoList);
        BaseDataBiz.checkValueOverXyz(dataXyzh, items, "单次变化量", atomicInteger, sb, infoList);
        BaseDataBiz.checkValueOverXyz(dataXyzh, items, "日变化速率", atomicInteger, sb, infoList);
        BaseDataBiz.checkValueOverXyz(dataXyzh, items, "测量值", atomicInteger, sb, infoList);
        BaseDataBiz.checkValueOverXyz2(dataXyzh, items, nameList, atomicInteger, sb, infoList);
        if(atomicInteger.get() > 0){
            dataXyzh.setOverLimit(true);
            dataXyzh.setOverLimitInfo(sb.substring(0, sb.toString().length()-1));
            // 生成报警信息对象
            if (infoList.size()>0){
                for (String checkInfo : infoList) {
                    alarmInfoList.add(getAlarmDataXyz(checkInfo, mission, dataXyzh));
                }
            }
        }else {
            dataXyzh.setOverLimit(false);
        }
    }

    /**
     * 获取报警对象
     */
    private AlarmInfoVO getAlarmDataXyz(String alarmInfoStr, ProMission mission, PointDataXyzhVO dataXyzh) {
        String[] split = alarmInfoStr.split(";");
        AlarmInfoVO info = new AlarmInfoVO();
        info.setProjectId(mission.getProjectId());
        info.setMissionId(mission.getId());
        info.setPtId(dataXyzh.getPid());
        info.setRecycleNum(dataXyzh.getRecycleNum());
        info.setAlarmOrigin(split[0]);
        info.setInfo(split[1]);
        info.setAlarmLevel(split[2]);
        info.setThreshold(split[3]);
        info.setAbs("true".equals(split[4]));
        info.setAlarmTime(new Date());
        info.setHandle(false);
        return info;
    }

    /**
     *  获取水平位移xy表格数据
     **/
    private ResponseUtil getSpxyData(Sheet sheet, UploadData uploadData) {
        try {
            List<Point> points = pointService.queryByMissionId(uploadData.getMissionId());
            UploadDataCb dataCb = new UploadDataCb();
            dataCb.setType(uploadData.getType());
            ArrayList<PointDataZVO> dataZList = new ArrayList<>();
            ArrayList<PointDataXyzVO> dataXyzList = new ArrayList<>();
            ArrayList<TablePtData> tablePtList = new ArrayList<>();
            ArrayList<AlarmInfoVO> alarmInfoList = new ArrayList<>();
            // 读取数据 :PID 	测点名	X(m)	Y(m)	测点状态	观测时间	备注
            boolean checkTime = false;
            ArrayList<Long> pidList = new ArrayList<>();
            for (int i = 2; i <= sheet.getLastRowNum() ; i++) {
                PointDataZVO dataZ = new PointDataZVO();
                String pid = ExcelUtil.getStringValue(sheet, i, 0);
                String name = ExcelUtil.getStringValue(sheet, i, 1);
                String x = ExcelUtil.getStringValue(sheet, i, 2);
                String y = ExcelUtil.getStringValue(sheet, i, 3);
                String status = ExcelUtil.getStringValue(sheet, i, 4);
                DateCell dateCell = ExcelUtil.getDateValue(sheet, i, 5);
                String note = ExcelUtil.getStringValue(sheet, i, 6);
                // 表格数据验证
                if (StringUtils.isEmpty(pid) && StringUtils.isEmpty(name) && StringUtils.isEmpty(x) && StringUtils.isEmpty(y)
                        && StringUtils.isEmpty(status) && StringUtils.isEmpty(note) && dateCell.getGetTime()==null){
                    break;
                }
                // 表格列数据格式检查
                String checkInfo = checkSxData(dataZ, pid, name,x, y, null, status, dateCell, note, i, pidList, 1, points);
                if (StringUtils.isNotEmpty(checkInfo)){
                    return ResponseUtil.failure(500, checkInfo);
                }
                // 观测时间有效性验证
                if(!checkTime && dataZ.getGetTime()!=null){
                    CheckTimeVO check = checkPtTime(dataZ, uploadData);
                    checkTime = check.isChecked();
                    if (StringUtils.isNotEmpty(check.getCheckInfo())){
                        return ResponseUtil.failure(500, check.getCheckInfo());
                    }
                }
                // 添加数据
                TablePtData ptData = DzBeanUtils.propertiesCopy(dataZ, TablePtData.class);
                PointDataXyzVO dataXyz = DzBeanUtils.propertiesCopy(dataZ, PointDataXyzVO.class);
                if (StringUtils.isEmpty(x)){
                    ptData.setX(null);
                    dataXyz.setX(null);
                }else {
                    ptData.setX(Double.parseDouble(x));
                    dataXyz.setX(Double.parseDouble(x));
                }
                if (StringUtils.isEmpty(y)){
                    ptData.setY(null);
                    dataXyz.setY(null);
                }else {
                    ptData.setY(Double.parseDouble(y));
                    dataXyz.setY(Double.parseDouble(y));
                }
                pidList.add(dataZ.getPid());
                tablePtList.add(ptData);
                dataXyzList.add(dataXyz);
                dataZList.add(dataZ);
            }
            if (dataZList.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            // 测点有效性验证
            String result = checkSxDb(dataZList, points);
            if (StringUtils.isNotEmpty(result)){
                return ResponseUtil.failure(500, result);
            }
            // 中间数据计算
            dataZList.forEach(item -> calculateData(item, uploadData, alarmInfoList));
            dataCb.setPtDataZ(dataZList);
            dataCb.setPtDataXyz(dataXyzList);
            dataCb.setAlarmInfoList(alarmInfoList);
            dataCb.setTableList(tablePtList);
            return ResponseUtil.success(dataCb);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel表格出现异常");
        }
    }

    /**
     *  获取竖向位移/水平位移(HD)/倾斜位移表格数据
     **/
    private ResponseUtil getSxData(Sheet sheet, UploadData uploadData) {
        try {
            List<Point> points = pointService.queryByMissionId(uploadData.getMissionId());
            UploadDataCb dataCb = new UploadDataCb();
            dataCb.setType(uploadData.getType());
            ArrayList<PointDataZVO> dataZList = new ArrayList<>();
            ArrayList<TablePtData> tablePtList = new ArrayList<>();
            ArrayList<AlarmInfoVO> alarmInfoList = new ArrayList<>();
            // 读取数据 :PID	测点名	观测值(m)	测点状态	观测时间	备注
            boolean checkTime = false;
            ArrayList<Long> pidList = new ArrayList<>();
            for (int i = 2; i <= sheet.getLastRowNum() ; i++) {
                PointDataZVO dataZ = new PointDataZVO();
                String pid = ExcelUtil.getStringValue(sheet, i, 0);
                String name = ExcelUtil.getStringValue(sheet, i, 1);
                String z = ExcelUtil.getStringValue(sheet, i, 2);
                String status = ExcelUtil.getStringValue(sheet, i, 3);
                DateCell dateCell = ExcelUtil.getDateValue(sheet, i, 4);
                String note = ExcelUtil.getStringValue(sheet, i, 5);
                // 表格数据验证
                if (StringUtils.isEmpty(pid) && StringUtils.isEmpty(name) && StringUtils.isEmpty(z) &&
                        StringUtils.isEmpty(status) && StringUtils.isEmpty(note) && dateCell.getGetTime()==null){
                    break;
                }
                // 表格列数据格式检查
                String checkInfo = checkSxData(dataZ, pid, name,null, null, z, status, dateCell, note, i, pidList, 0, points);
                if (StringUtils.isNotEmpty(checkInfo)){
                    return ResponseUtil.failure(500, checkInfo);
                }
                // 观测时间有效性验证
                if(!checkTime && dataZ.getGetTime()!=null){
                    CheckTimeVO check = checkPtTime(dataZ, uploadData);
                    checkTime = check.isChecked();
                    if (StringUtils.isNotEmpty(check.getCheckInfo())){
                        return ResponseUtil.failure(500, check.getCheckInfo());
                    }
                }
                // 添加数据
                pidList.add(dataZ.getPid());
                tablePtList.add(DzBeanUtils.propertiesCopy(dataZ, TablePtData.class));
                dataZList.add(dataZ);
            }
            if (dataZList.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            // 测点有效性验证
            String result = checkSxDb(dataZList, points);
            if (StringUtils.isNotEmpty(result)){
                return ResponseUtil.failure(500, result);
            }
            // 中间数据计算
            dataZList.forEach(item -> calculateData(item, uploadData, alarmInfoList));
            dataCb.setPtDataZ(dataZList);
            dataCb.setAlarmInfoList(alarmInfoList);
            dataCb.setTableList(tablePtList);
            return ResponseUtil.success(dataCb);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel表格出现异常");
        }
    }

    /**
     * 获取竖向位移/水平位移(HD)/倾斜位移数据与数据库验证
     **/
    private String checkSxDb(List<PointDataZVO> dataZList, List<Point> points) {
        for (PointDataZVO dataZ : dataZList) {
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataZ.getPid())).findAny();
            //⑦PID正确性检查 与系统已存在的PID比对，检查PID是否有误；
            if (!optional.isPresent()){
                return "PID: "+dataZ.getPid()+" 不存在";
            }else {
                //⑧PID对应测点名正确性检查 与系统中同名PID挂接的测点名比对，检查测点名是否有误；
                Point point = optional.get();
                if (!point.getName().equals(dataZ.getName())){
                    return "PID: "+dataZ.getPid()+" 对应测点名称: "+dataZ.getName()+" 和系统不一致";
                }
                //⑨PID对应测点状态正确性检查 与系统中同名PID挂接的测点状态比对，
                //如果系统中的“测点停测 = true”，则从表格获取的“测点状态”必须是“停测”；
                //如果“测点停测 = false”，则从表格获取的“测点状态”不能是“停测”；
                if ((!point.getStop() && "停测".equals(dataZ.getStatus())) || (point.getStop() && !"停测".equals(dataZ.getStatus())))
                {
                    return "PID: "+dataZ.getPid()+" 对应测点状态和系统不一致";
                }
                dataZ.setStop(point.getStop());
            }
        }
        return null;
    }

    /**
     * 计算表格中间数据
    **/
    private void calculateData(PointDataZVO dataZ, UploadData uploadData, List<AlarmInfoVO> alarmInfoList) {
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataZ::getPid, dataZ.getPid()).orderByDesc(PointDataZ::getGetTime);
        List<PointDataZ> list = pointDataZService.list(wrapper);
        PointDataZ lastData  = null;
        if(list.size() > 0){
            lastData = list.get(0);
        }
        ProMission mission = missionService.findById(uploadData.getMissionId());
        if(lastData == null){
            BaseDataBiz.setFirstData(dataZ, uploadData.getType());
        }else {
            BaseDataBiz.setNewData(dataZ, lastData, mission, uploadData.getType());
        }
        if (dataZ.getStop()) { return; }
        // 验证测点是否超限，记录报警信息
        getCheckInfo(dataZ, alarmInfoList, mission);
    }

    /**
     * 获取报警关联信息
     **/
    private void getCheckInfo(PointDataZVO dataZ, List<AlarmInfoVO> alarmInfoList, ProMission mission) {
        List<AlarmItem> alarmItems = alarmItemService.getByPid(dataZ.getPid());
        List<AlarmItem> items = alarmItems.stream().filter(item -> item.getMonitorType().equals(mission.getTypeName()))
                .collect(Collectors.toList());
        List<String> nameList = items.stream().filter(item -> item.getAlarmType() == 2).map(AlarmItem::getResultItemType)
                .distinct().collect(Collectors.toList());
        AtomicInteger atomicInteger = new AtomicInteger(0);
        StringBuilder sb = new StringBuilder();
        String totalZCheck = BaseDataBiz.checkValueOver(dataZ, items, "累计变化量", atomicInteger, sb);
        String deltZCheck = BaseDataBiz.checkValueOver(dataZ, items, "单次变化量", atomicInteger, sb);
        String vDeltZCheck = BaseDataBiz.checkValueOver(dataZ, items, "日变化速率", atomicInteger, sb);
        String zCheck = BaseDataBiz.checkValueOver(dataZ, items, "测量值", atomicInteger, sb);
        List<String> check2 = BaseDataBiz.checkValueOver2(dataZ, items, nameList, atomicInteger, sb);
        if(atomicInteger.get() > 0){
            dataZ.setOverLimit(true);
            dataZ.setOverLimitInfo(sb.substring(0, sb.toString().length()-1));
            // 生成报警信息对象
            if (StringUtils.isNotEmpty(totalZCheck)){
                alarmInfoList.add(getAlarmData(totalZCheck, mission, dataZ));
            }
            if (StringUtils.isNotEmpty(deltZCheck)){
                alarmInfoList.add(getAlarmData(deltZCheck, mission, dataZ));
            }
            if (StringUtils.isNotEmpty(vDeltZCheck)){
                alarmInfoList.add(getAlarmData(vDeltZCheck, mission, dataZ));
            }
            if (StringUtils.isNotEmpty(zCheck)){
                alarmInfoList.add(getAlarmData(zCheck, mission, dataZ));
            }
            if (check2.size()>0){
                for (String checkInfo : check2) {
                    alarmInfoList.add(getAlarmData(checkInfo, mission, dataZ));
                }
            }
        }else {
            dataZ.setOverLimit(false);
        }
    }

    /**
     * 获取报警信息对象
    **/
    private AlarmInfoVO getAlarmData(String alarmInfoStr, ProMission mission, PointDataZVO dataZ) {
        String[] split = alarmInfoStr.split(";");
        AlarmInfoVO info = new AlarmInfoVO();
        info.setProjectId(mission.getProjectId());
        info.setMissionId(mission.getId());
        info.setPtId(dataZ.getPid());
        info.setRecycleNum(dataZ.getRecycleNum());
        info.setAlarmOrigin(split[0]);
        info.setInfo(split[1]);
        info.setAlarmLevel(split[2]);
        info.setThreshold(split[3]);
        info.setAbs("true".equals(split[4]));
        info.setAlarmTime(new Date());
        info.setHandle(false);
        return info;
    }

    /**
     * 时间验证
    **/
    private CheckTimeVO checkPtTime(PointDataZVO dataZ, UploadData uploadData) {
        CheckTimeVO result = new CheckTimeVO();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String timeStr = format.format(dataZ.getGetTime());
        if (!timeStr.equals(uploadData.getDateTimeStr())){
            result.setChecked(false);
            result.setCheckInfo("表格第一个非空观测时间:"+timeStr+"和文件标题日期"+uploadData.getDateTimeStr()+"不一致");
            return result;
        }
        result.setChecked(true);
        // 时间滞后性验证
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataZ::getPid, dataZ.getPid())
                .ge(PointDataZ::getGetTime, dataZ.getGetTime());
        if (pointDataZService.count(wrapper) > 0){
            result.setCheckInfo("监测时间滞后错误:PID["+dataZ.getPid() +"]存在["+format2.format(dataZ.getGetTime())+"]之后记录");
        }
        return result;
    }
    private String checkPtTimeApp(PointDataZVO dataZ){
        // 时间滞后性验证
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataZ::getPid, dataZ.getPid())
                .ge(PointDataZ::getGetTime, dataZ.getGetTime());
        if (pointDataZService.count(wrapper) > 0){
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            return "监测时间滞后错误:PID["+dataZ.getPid() +"]存在["+format2.format(dataZ.getGetTime())+"]之后记录";
        }
        return null;
    }

    private String checkPtTimeApp2(PointDataZVO dataZ){
        // 时间滞后性验证
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointDataXyzh::getPid, dataZ.getPid())
                .ge(PointDataXyzh::getGetTime, dataZ.getGetTime());
        if (pointDataXyzhService.count(wrapper) > 0){
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            return "监测时间滞后错误:PID["+dataZ.getPid() +"]存在["+format2.format(dataZ.getGetTime())+"]之后记录";
        }
        return null;
    }

    /**
     * 竖向位移数据监测
     * isXy: 0：非xyz; 1-水平xy ;2: 三维xyz
    **/
    private String  checkSxData(PointDataZVO dataZ, String pid, String name, String x, String y, String z, String status, DateCell dateCell,
                               String note, int i, List<Long> pidList, int isXy, List<Point> points) {
        List<String> statusList = Arrays.asList("正常", "停测", "破坏重埋", "基准点修正");
        // ①非空检查 “PID”、“测点名”、“测点状态”3列数值非空；
        if (StringUtils.isEmpty(pid) || StringUtils.isEmpty(name) || StringUtils.isEmpty(status)){
            return "表格第" + (i+1) + "行数据错误: PID、测点名、测点状态三列不为空";
        }
        try {
            pid = pid.replace(".0","");
            dataZ.setPid(Long.valueOf(pid));
        }catch (Exception e){
            return "表格第" + (i+1) + "行数据错误: PID只能是整数数值";
        }
        //② 测点状态值只能是“正常,停测,破坏重埋和基准点修正”4项中的内容
        if(!statusList.contains(status)){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能是[正常,停测,破坏重埋和基准点修正]";
        }
        //③ 观测时间”列的非空值是日期时间格式；
        if (StringUtils.isNotEmpty(dateCell.getInfo())){
            return dateCell.getInfo();
        }
        dataZ.setGetTime(dateCell.getGetTime());
        //④ “观测值”列的值除空值外，都是数值；
        if (StringUtils.isNotEmpty(x)){
            try {
                Double.valueOf(x);
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        if (StringUtils.isNotEmpty(y)){
            try {
                Double.valueOf(y);
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        if (StringUtils.isNotEmpty(z)){
            try {
                dataZ.setZ(Double.valueOf(z));
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        // 计算Z值
        if (isXy==1 && StringUtils.isNotEmpty(x) && StringUtils.isNotEmpty(y)){
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataZ.getPid())).findAny();
            double azimuth = optional.map(Point::getAzimuth).orElse(0.0);
            z = BaseDataBiz.coordTransfer(Double.parseDouble(x), Double.parseDouble(y), azimuth).get(1) + "";
        }
        //⑤观测值、测点状态、观测时间和备注等4列内容逻辑有效性检查
        //a.“观测值”非空，测点状态不能是“停测”，必须有观测时间；
        //b.“观测值”空，测点状态只能是“停测”，观测时间必须是空，备注不能是空；
        if((isXy==1 && StringUtils.isEmpty(x) && StringUtils.isEmpty(y)) || (isXy==0 && StringUtils.isEmpty(z))
                || (isXy==2 && StringUtils.isEmpty(x) && StringUtils.isEmpty(y) && StringUtils.isEmpty(z))){
            if (!"停测".equals(status) || dateCell.getGetTime()!=null || StringUtils.isEmpty(note)){
                return  "表格第" + (i+1) + "行数据错误: 观测值列为空时状态只能是停测，观测时间为空，备注不为空";
            }
        }
        if((isXy==1 && StringUtils.isNotEmpty(x) && StringUtils.isNotEmpty(y)) || (isXy==0 && StringUtils.isNotEmpty(z))
                || (isXy==2 && StringUtils.isNotEmpty(x) && StringUtils.isNotEmpty(y) && StringUtils.isNotEmpty(z))){
            if ("停测".equals(status) || dateCell.getGetTime()==null){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时状态不能是停测，观测时间不为空";
            }
        }
        //⑥PID唯一性检查 “PID”列数值全局唯一
        if(pidList.size() > 0 && pidList.contains(dataZ.getPid())){
            return "表格第" + (i+1) + "行数据错误: PID值表格内重复";
        }
        if (StringUtils.isNotEmpty(z)){
            dataZ.setZ(Double.parseDouble(z));
        }else {
            dataZ.setZ(null);
        }
        dataZ.setName(name);
        dataZ.setStatus(status);
        dataZ.setNote(note);
        return null;
    }

    /**
     *  获取巡视表格数据
    **/
    private ResponseUtil getXsData(Sheet sheet, UploadData uploadData) {
        try {
            int rowNum = sheet.getLastRowNum();
            if (rowNum != 20){
                return ResponseUtil.failure(500, "导入表格行数不等于21");
            }
            Cell cell = sheet.getRow(1).getCell(1);
            Date date;
            try {
                date = cell.getDateCellValue();
            }catch (Exception e){
                return ResponseUtil.failure(500, "巡视日期格式错误");
            }
            if (date == null){
                return ResponseUtil.failure(500, "巡视日期格式错误或者为空");
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            if (!format.format(date).equals(uploadData.getDateTimeStr())){
                return ResponseUtil.failure(500, "巡视日期数值和文件标题日期不一致");
            }
            LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(JcInfo::getMissionId, uploadData.getMissionId())
                    .ge(JcInfo::getJcDate, date);
            if (jcInfoService.count(wrapper) > 0){
                return ResponseUtil.failure(500, "新增巡视任务观测时间必须晚于系统已有巡视任务");
            }
            JcInfoVO vo = new JcInfoVO();
            StringBuilder info= new StringBuilder();
            for (int i = 3; i < 21 ; i++) {
                String value = ExcelUtil.getStringValue(sheet, i, 2);
                String note = ExcelUtil.getStringValue(sheet, i, 3);
                if (StringUtils.isEmpty(value)){
                    return ResponseUtil.failure(500, "除备注外所有列不能为空");
                }else {
                    if (i != 3){
                        info.append("||").append(value);
                    }else {
                        info.append(value);
                    }
                }
                if (StringUtils.isEmpty(note)){
                    info.append("<$>").append("");
                }else {
                    info.append("<$>").append(note);
                }
            }
            vo.setProjectId(uploadData.getProjectId());
            vo.setMissionId(uploadData.getMissionId());
            vo.setJcDate(date);
            vo.setInfo(info.toString());
            return ResponseUtil.success(vo);
        }catch (Exception e){
            return ResponseUtil.failure(500, "读取excel数据发生错误");
        }
    }

    /**
     * 导入表格文件名称验证
    **/
    private String checkFilename(String filename, UploadData uploadData) {
        filename = filename.substring(0, filename.lastIndexOf('.'));
        String[] split = filename.split("_");
        boolean isAuto = MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType());
        int splitLen = 5 ;
        int timeIndex = 4 ;
        if(split.length < splitLen){
            if (isAuto){
                return "导入文件名[" + filename + "]称格式错误,正确格式：工程名_监测任务名_测站名_入库表格_监测日期.xlsx";
            }
            return "导入文件名[" + filename + "]称格式错误,正确格式：工程名_监测任务名_编组名_入库表格_监测日期.xlsx";
        }
        if (!uploadData.getProjectName().equals(split[0])){
            return "导入文件工程名称 [" + split[0] + "] 和当前工程 [" + uploadData.getProjectName() + "] 不一致";
        }
        if (!uploadData.getMissionName().equals(split[1])){
            return "导入文件监测任务名称 [" + split[1] + "] 和当前任务 [" + uploadData.getMissionName() + "] 不一致";
        }
        if(isAuto && !split[2].equals(uploadData.getStConfigName())){
            return "导入文件测站名称 [" + split[2] + "] 和当前测站 [" + uploadData.getStConfigName() + "] 不一致";
        }
        String dateTime = split[timeIndex].substring(0, 8);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            format.parse(dateTime);
            uploadData.setDateTimeStr(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "导入文件监测日期格式错误: " + split[timeIndex];
        }
        return null;
    }

    @Override
    public void download(Long missionId, Long secondId, HttpServletResponse response) {
        ProMissionVO missionVO = DzBeanUtils.propertiesCopy(missionService.findById(missionId), ProMissionVO.class);
        if (missionVO == null) {
            return;
        }
        DownloadData downloadData = new DownloadData();
        downloadData.setType(missionVO.getTypeName()).setValueUnit(missionVO.getValueUnit())
                .setMissionId(missionId).setMissionName(missionVO.getName())
                .setProjectId(missionVO.getProjectId()).setProjectName(missionVO.getProjectName());
        switch (downloadData.getType()){
            case MissionTypeConst.MANUAL_PATROL:
                exportXsExcel(downloadData, response);
                break;
            case MissionTypeConst.SX_H_OFFSET:
                exportSxExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.SP_XY_OFFSET:
                exportSpxyExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.SP_HD_OFFSET:
                exportSpHdExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.SP_DEEP_OFFSET:
                exportSpfcExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.QX_OFFSET:
                exportQxExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.ZC_FORCE:
                exportZlExcel(downloadData, response, secondId);
                break;
            case MissionTypeConst.HAND_XYZ_OFFSET:
            case MissionTypeConst.AUTO_XYZ_OFFSET:
                exportSwXyzExcel(downloadData, secondId, response);
                break;
            default:
                throw new RuntimeException("监测任务类型不存在");
        }
    }

    /**
     * 导出三维位移表格模板
     *
     * @description 导出三维位移表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportSwXyzExcel(DownloadData downloadData, Long secondId, HttpServletResponse response) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByMissionId(downloadData.getMissionId());
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            boolean isNoAuto = MissionTypeConst.HAND_XYZ_OFFSET.equals(downloadData.getType());
            String locationPattern = isNoAuto ? "static/手动三维位移_入库表.xlsx"
                    : "static/全站仪自动三维位移_入库表.xlsx";
            Resource[] resources = resolver.getResources(locationPattern);
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            //全站仪自动时设置测站名 并过滤测点信息
            if (!isNoAuto){
                points = setStNameAndGetStPoint(sheet, points, secondId, downloadData);
            }else {
                //获取对应手动三维编组测点
                points = points.stream().filter(item -> item.getPtGroupId().equals(secondId)).collect(Collectors.toList());
            }
            // 添加测点信息
            int rowIndex = isNoAuto ? 1 : 2;
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(rowIndex);
                Cell cell2 = row1.getCell(2);
                Cell cell3 = row1.getCell(3);
                Cell cell4 = row1.getCell(4);
                cell2.setCellValue("X("+downloadData.getValueUnit()+")");
                cell3.setCellValue("Y("+downloadData.getValueUnit()+")");
                cell4.setCellValue("Z("+downloadData.getValueUnit()+")");
            }
            // PID 	测点名	X(m)	Y(m)    Z(m)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getName());
                Cell status = rowPt.getCell(5);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(6);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 全站仪自动时设置测站名 并过滤测点信息
    **/

    private List<Point> setStNameAndGetStPoint(Sheet sheet, List<Point> points, Long surveyControlId, DownloadData downloadData) {
        RobotSurveyControl surveyControl = surveyControlService.getById(surveyControlId);
        if (surveyControl == null || StringUtils.isEmpty(surveyControl.getStationConfig())){
            return new ArrayList<>();
        }else {
            //设置测站名
            Row row = sheet.getRow(1);
            Cell cell = row.getCell(1);
            cell.setCellValue(surveyControl.getStationConfig());
            downloadData.setStConfigName(surveyControl.getStationConfig());
            //过滤测点
            String[] split = surveyControl.getParams().split("\\|");
            if (split.length == 0) {
                return new ArrayList<>();
            }
            String[] stSplit = split[0].split(";");
            if (stSplit.length <= 1) {
                return new ArrayList<>();
            }
            ArrayList<Long> pidList = new ArrayList<>();
            for (int i = 1; i < stSplit.length; i++) {
                if (StringUtils.isNotEmpty(stSplit[i])
                        && StringUtils.isNumeric(stSplit[i].split(",")[0])){
                    pidList.add(Long.valueOf(stSplit[i].split(",")[0]));
                }
            }
            return pidList.size() > 0
                    ? points.stream().filter(it -> pidList.contains(it.getId())).collect(Collectors.toList())
                    : new ArrayList<>();
        }
    }

    /**
     * 导出支撑轴力表格模板
     *
     * @description 导出支撑轴力表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportZlExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        List<Long> pidList = points.stream().map(Point::getId).collect(Collectors.toList());
        List<SensorZl> sensorZls = new ArrayList<>();
        if (pidList.size() > 0){
            LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SensorZl::getPointId, pidList).orderByDesc(SensorZl::getCreateTime);
            sensorZls = sensorZlService.list(wrapper);
        }
        // 获取轴力数据
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/支撑轴力_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(4);
                cell2.setCellValue("观测值");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID 	测点名	传感器编号	位置	观测值(Hz)	温度(℃)	测点状态	传感器状态	时间	备注
            for (Point point : points) {
                // 获取传感器列表
                List<SensorZl> list = sensorZls.stream().filter(item -> item.getPointId().equals(point.getId()))
                        .collect(Collectors.toList());
                if (list.size() > 0){
                    for (SensorZl zl : list) {
                        rowIndex++;
                        Row rowPt = sheet.getRow(rowIndex);
                        Cell pid = rowPt.getCell(0);
                        pid.setCellValue(point.getId());
                        Cell name = rowPt.getCell(1);
                        name.setCellValue(point.getName());
                        Cell code = rowPt.getCell(2);
                        code.setCellValue(zl.getJxgCode());
                        Cell location = rowPt.getCell(3);
                        location.setCellValue(zl.getLocation());
                        Cell status = rowPt.getCell(6);
                        status.setCellValue(point.getStop() ? "停测" : "正常");
                        Cell sensorStatus = rowPt.getCell(7);
                        sensorStatus.setCellValue(zl.getBroken() ? "破坏" : "正常");
                        Cell date = rowPt.getCell(8);
                        date.setCellValue(new Date());
                    }
                }else {
                    rowIndex++;
                    Row rowPt = sheet.getRow(rowIndex);
                    Cell pid = rowPt.getCell(0);
                    pid.setCellValue(point.getId());
                    Cell name = rowPt.getCell(1);
                    name.setCellValue(point.getName());
                    Cell status = rowPt.getCell(6);
                    status.setCellValue(point.getStop() ? "停测" : "正常");
                    Cell date = rowPt.getCell(8);
                    date.setCellValue(new Date());
                }
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出倾斜位移表格模板
     *
     * @description 导出倾斜位移表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportQxExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/倾斜位移_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(2);
                cell2.setCellValue("观测值("+downloadData.getValueUnit()+")");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID 	测点名	观测值(%)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getName());
                Cell status = rowPt.getCell(3);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(4);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出水平位移(分层)表格模板
     *
     * @description 导出水平位移(分层)表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportSpfcExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        points.sort(Comparator.comparing(Point::getId));
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/水平位移分层_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(3);
                cell2.setCellValue("观测值("+downloadData.getValueUnit()+")");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID 	测点名	深度(m)	观测值(m)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                String depthValue = point.getName().contains(".") ? point.getName() : point.getName() + ".0";
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getPtGroupName());
                Cell depth = rowPt.getCell(2);
                depth.setCellValue(Double.parseDouble(point.getName()));
                Cell status = rowPt.getCell(4);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(5);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出水平位移(HD)表格模板
     *
     * @description 导出水平位移(HD)表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportSpHdExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/水平位移HD_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(2);
                cell2.setCellValue("观测值("+downloadData.getValueUnit()+")");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID 	测点名	观测值(m)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getName());
                Cell status = rowPt.getCell(3);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(4);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出水平位移(XY->S)表格模板
     *
     * @description 导出水平位移(XY->S)表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportSpxyExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/水平位移XY_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(2);
                Cell cell3 = row1.getCell(3);
                cell2.setCellValue("X("+downloadData.getValueUnit()+")");
                cell3.setCellValue("Y("+downloadData.getValueUnit()+")");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID 	测点名	X(m)	Y(m)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getName());
                Cell status = rowPt.getCell(4);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(5);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出竖向位移表格模板
     *
     * @description 导出竖向位移表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
     **/
    private void exportSxExcel(DownloadData downloadData, HttpServletResponse response, Long groupId) {
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/竖向位移_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置测量值单位
            if(StringUtils.isNotEmpty(downloadData.getValueUnit())){
                Row row1 = sheet.getRow(1);
                Cell cell2 = row1.getCell(2);
                cell2.setCellValue("观测值("+downloadData.getValueUnit()+")");
            }
            // 添加测点信息
            int rowIndex = 1;
            // PID	测点名	观测值(m)	测点状态	观测时间	备注
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell pid = rowPt.getCell(0);
                pid.setCellValue(point.getId());
                Cell name = rowPt.getCell(1);
                name.setCellValue(point.getName());
                Cell status = rowPt.getCell(3);
                status.setCellValue(point.getStop() ? "停测" : "正常");
                Cell date = rowPt.getCell(4);
                date.setCellValue(new Date());
            }
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 导出巡视表格模板
     *
     * @description 导出巡视表格模板
     * XXX工程_监测任务名_入库表_yyyyMMdd.xlsx
     * @author jing.fang
    **/
    private void exportXsExcel(DownloadData downloadData, HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/现场巡视_入库表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            // 设置表名
            wb.setSheetName(0, downloadData.getMissionName());
            Sheet sheet = wb.getSheetAt(0);
            // 设置标题
            Row row = sheet.getRow(0);
            Cell cell0 = row.getCell(0);
            cell0.setCellValue(downloadData.getMissionName());
            // 设置日期
            Row row1 = sheet.getRow(1);
            Cell cell1 = row1.getCell(1);
            cell1.setCellValue(new Date());
            // 写入导出
            exportExcel(downloadData, response, wb);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("模板导出异常");
        }
    }

    /**
     * 写出模板表格
     *
     * @description 写出模板表格
     * @author jing.fang
     * @date 2022/4/1 14:02
    **/
    private void exportExcel(DownloadData downloadData, HttpServletResponse response, Workbook wb) throws IOException {
        try {
            // 设置表格名称
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String formatDate = format.format(new Date());
            String fileName = downloadData.getProjectName() + "_" + downloadData.getMissionName() + "_入库表_"
                    + formatDate + ".xlsx";
            if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(downloadData.getType())){
                fileName = downloadData.getProjectName() + "_" + downloadData.getMissionName()
                        + "_" + downloadData.getStConfigName()+ "_入库表_"
                        + formatDate + ".xlsx";
            }
            response.reset();
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1));
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseUtil saveToDb(UploadDataCb dataCb) {
        List<AlarmInfoVO> infoList = dataCb.getAlarmInfoList();
        List<PointDataZVO> dataZ = dataCb.getPtDataZ();
        List<PointDataXyzVO> dataXyz = dataCb.getPtDataXyz();
        List<PointDataZlVO> dataZl = dataCb.getPtDataZl();
        List<PointDataXyzhVO> dataXyzh = dataCb.getPtDataXyzh();
        int ptNum = 0;
        int alarmNum = 0;
        switch (dataCb.getType()){
            case MissionTypeConst.MANUAL_PATROL:
                JcInfoVO infoVO = dataCb.getInfoVO();
                if (infoVO == null) {
                    return ResponseUtil.failure(500, "未上传有效巡视数据");
                }
                jcInfoService.save(DzBeanUtils.propertiesCopy(infoVO, JcInfo.class));
                return ResponseUtil.success("巡视记录入库成功");
            case MissionTypeConst.SX_H_OFFSET:
            case MissionTypeConst.SP_HD_OFFSET:
            case MissionTypeConst.SP_DEEP_OFFSET:
            case MissionTypeConst.QX_OFFSET:
                // 数据库验证和数据计算
                if (dataZ!= null && dataZ.size() > 0){
                    pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZ.class));
                    dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZReal.class));
                }
                if (infoList!= null && infoList.size() > 0){
                    alarmInfoService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfo.class));
                    List<Long> list = infoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
                    alarmNum = list.size();
                }
                ptNum = dataZ!=null ? dataZ.size() : 0;
                break;
            case MissionTypeConst.SP_XY_OFFSET:
                // 数据库验证和数据计算
                if (dataXyz!= null && dataXyz.size() > 0){
                    pointDataXyzService.saveBatch(DzBeanUtils.listCopy(dataXyz, PointDataXyz.class));
                }
                if (dataZ!= null && dataZ.size() > 0){
                    pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZ.class));
                    dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZReal.class));
                }
                if (infoList!= null && infoList.size() > 0){
                    alarmInfoService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfo.class));
                    List<Long> list = infoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
                    alarmNum = list.size();
                }
                ptNum = dataZ!=null ? dataZ.size() : 0;
                break;
            case MissionTypeConst.ZC_FORCE:
                // 数据库验证和数据计算
                if (dataZl!= null && dataZl.size() > 0){
                    pointDataZlService.saveBatch(DzBeanUtils.listCopy(dataZl, PointDataZl.class));
                }
                if (dataZ!= null && dataZ.size() > 0){
                    pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZ.class));
                    dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZ, PointDataZReal.class));
                }
                if (infoList!= null && infoList.size() > 0){
                    alarmInfoService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfo.class));
                    List<Long> list = infoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
                    alarmNum = list.size();
                }
                ptNum = dataZ!=null ? dataZ.size() : 0;
                break;
            case MissionTypeConst.HAND_XYZ_OFFSET:
            case MissionTypeConst.AUTO_XYZ_OFFSET:
                // 数据库验证和数据计算
                if (dataXyzh!= null && dataXyzh.size() > 0){
                    pointDataXyzhService.saveBatch(DzBeanUtils.listCopy(dataXyzh, PointDataXyzh.class));
                    dataXyzhRealService.saveBatch(DzBeanUtils.listCopy(dataXyzh, PointDataXyzhReal.class));
                    if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(dataCb.getType()))
                    {
                        // 向robot_survey_data中插入数据
                        RobotSurveyData surveyData = new RobotSurveyData();
                        surveyData.setCreateId(dataCb.getUserId());
                        surveyData.setMissionId(dataCb.getMissionId());
                        surveyData.setRecycleNum(dataXyzh.get(0).getRecycleNum());
                        surveyData.setAuto(false);
                        surveyDataService.save(surveyData);
                    }
                }
                if (infoList!= null && infoList.size() > 0){
                    alarmInfoService.saveBatch(DzBeanUtils.listCopy(infoList, AlarmInfo.class));
                    List<Long> list = infoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
                    alarmNum = list.size();
                }
                ptNum = dataXyzh!=null ? dataXyzh.size() : 0;
                break;
            default:
                return ResponseUtil.failure(500, "监测任务类型不存在");
        }
        // 生成报警信息
        createAlarmMsg(infoList);
        // region 2024/11/20 数据推送处理
        if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(dataCb.getType()) && dataXyzh!= null && dataXyzh.size() > 0) {
            pushLastDataToCorrectDb(dataCb.getMissionId(), dataXyzh.get(0));
        }
        // endregion 2024/11/20 数据推送处理
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    /**
     * 推送上期数据到同步表
     * @param missionId missionId
     * @param dataXyzh dataXyzh
     */
    private void pushLastDataToCorrectDb(Long missionId, PointDataXyzhVO dataXyzh) {
        LambdaQueryWrapper<PushTaskOther> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskOther::getMissionId, missionId);
        List<PushTaskOther> list = pushTaskOtherService.list(wrapper);
        //当前任务存在推送任务且指定推送上期数据时执行数据同步
        if (list.size() > 0 && list.get(0).getPushCurrentData() == 0) {
            List<Long> ptIds = pointService.queryByMissionId(missionId).stream().map(Point::getId).collect(Collectors.toList());
            if (ptIds.size() > 0) {
                SurveyBiz.doPushLastDataList(dataXyzh.getRecycleNum(), ptIds, list.get(0),
                        DzBeanUtils.propertiesCopy(dataXyzh, PointDataXyzh.class),
                        pointDataXyzhService, dataXyzhCorrectService, alarmInfoService, infoCorrectService);
            }
        }
    }

    @Override
    public ResponseUtil uploadApp(int index, UploadDataApp uploadData) {
        List<DataApp> dataList = uploadData.getDataList();
        if (dataList == null || dataList.size() == 0){
            return ResponseUtil.failure(500, "上传数据不能为空");
        }
        try {
            List<Point> pointList = pointService.queryByMissionId(uploadData.getMissionId());
            switch (index) {
                //1: 竖向 2: 水平HD 3: 倾斜
                case 1:
                case 2:
                case 3:
                    return uploadSxData(uploadData, pointList);
                //4: 水平XY
                case 4:
                    return uploadSpXyData(uploadData, pointList);
                //5: 水平分层
                case 5:
                    return uploadSpFcData(uploadData, pointList);
                //6: 支撑轴力
                case 6:
                    return uploadZlData(uploadData, pointList);
                //7: 手动三维位移 8: 全站仪自动化三维
                case 7:
                case 8:
                    return uploadXyzData(uploadData, pointList);
                default:
                    return ResponseUtil.failure(500, "上传类型错误");

            }
        } catch (Exception e) {
            return ResponseUtil.failure(500, "上传出现异常");
        }
    }

    private ResponseUtil uploadXyzData(UploadDataApp uploadData, List<Point> pointList) {
        boolean checkTime = false;
        List<PointDataZVO> dataZList = new ArrayList<>();
        List<PointDataXyzhVO> dataXyzhList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        List<DataApp> dataList = uploadData.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            DataApp data = dataList.get(i);
            PointDataZVO dataZ = new PointDataZVO();
            // 表格列数据格式检查
            String checkInfo = checkSxDataApp(data, dataZ, i, pointList);
            if (StringUtils.isNotEmpty(checkInfo)){
                return ResponseUtil.failure(500, checkInfo);
            }
            // 观测时间有效性验证
            if(!checkTime && dataZ.getGetTime()!=null){
                String checkTimeInfo = checkPtTimeApp2(dataZ);
                checkTime = true;
                if (StringUtils.isNotEmpty(checkTimeInfo)){
                    return ResponseUtil.failure(500, checkTimeInfo);
                }
            }
            PointDataXyzhVO dataXyzh = DzBeanUtils.propertiesCopy(dataZ, PointDataXyzhVO.class);
            if (StringUtils.isEmpty(data.getX())){
                dataXyzh.setX(0.0);
            }else {
                dataXyzh.setX(Double.parseDouble(data.getX()));
            }
            if (StringUtils.isEmpty(data.getY())){
                dataXyzh.setY(0.0);
            }else {
                dataXyzh.setY(Double.parseDouble(data.getY()));
            }
            if (StringUtils.isEmpty(data.getZ())){
                dataXyzh.setZ(0.0);
            }else {
                dataXyzh.setZ(Double.parseDouble(data.getZ()));
            }
            dataXyzh.setAuto(dataList.get(0).isAuto());
            dataXyzhList.add(dataXyzh);
            dataZList.add(dataZ);
        }
        if (dataXyzhList.size() == 0){
            return ResponseUtil.failure(500, "导入表格中无有效数据行");
        }
        // 测点有效性验证
        String result = checkSxDb(dataZList, pointList);
        if (StringUtils.isNotEmpty(result)){
            return ResponseUtil.failure(500, result);
        }
        // 中间数据计算
        UploadData data = new UploadData();
        data.setMissionId(uploadData.getMissionId());
        data.setType(uploadData.getType());
        dataXyzhList.forEach(item -> calculateDataXyz(item, data, alarmInfoList, pointList));
        // 数据库验证和数据计算
        int ptNum = dataXyzhList.size();
        int alarmNum = 0;
        if (dataXyzhList.size() > 0){
            pointDataXyzhService.saveBatch(DzBeanUtils.listCopy(dataXyzhList, PointDataXyzh.class));
            dataXyzhRealService.saveBatch(DzBeanUtils.listCopy(dataXyzhList, PointDataXyzhReal.class));
            if (MissionTypeConst.AUTO_XYZ_OFFSET.equals(uploadData.getType()))
            {
                // 向robot_survey_data中插入数据
                RobotSurveyData surveyData = new RobotSurveyData();
                surveyData.setCreateId(uploadData.getUserId());
                surveyData.setMissionId(uploadData.getMissionId());
                surveyData.setRecycleNum(dataXyzhList.get(0).getRecycleNum());
                surveyData.setAuto(dataList.get(0).isAuto());
                surveyDataService.save(surveyData);
            }
        }
        if (alarmInfoList.size() > 0){
            alarmInfoService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfo.class));
            List<Long> list = alarmInfoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            alarmNum = list.size();
        }
        // 生成报警信息
        createAlarmMsg(alarmInfoList);
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    private ResponseUtil uploadZlData(UploadDataApp uploadData, List<Point> pointList) {
        boolean checkTime = false;
        List<PointDataZVO> dataZList = new ArrayList<>();
        List<PointDataZlVO> dataZlList = new ArrayList<>();
        List<TablePtData> tablePtList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        List<DataApp> dataList = uploadData.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            DataApp data = dataList.get(i);
            PointDataZVO dataZ = new PointDataZVO();
            // 表格列数据格式检查
            String checkInfo = checkZlDataApp(data, dataZ, i, tablePtList);
            if (StringUtils.isNotEmpty(checkInfo)){
                return ResponseUtil.failure(500, checkInfo);
            }
            // 观测时间有效性验证
            if(!checkTime && dataZ.getGetTime()!=null){
                String checkTimeInfo = checkPtTimeApp(dataZ);
                checkTime = true;
                if (StringUtils.isNotEmpty(checkTimeInfo)){
                    return ResponseUtil.failure(500, checkTimeInfo);
                }
            }
            // 添加表格数据
            TablePtData ptData = new TablePtData();
            ptData.setPid(dataZ.getPid()).setStatus(dataZ.getStatus());
            tablePtList.add(ptData);
            // ZL数据
            PointDataZlVO dataZl = new PointDataZlVO();
            dataZl.setPid(data.getPid()).setName(data.getName()).setPointStatus(data.getPointStatus())
                    .setSensorStatus(data.getSensorStatus()).setJxgCode(data.getJxgCode()).setLocation(data.getLocation())
                    .setNote(data.getNote()).setGetTime(data.getGetTime()).setF(dataZ.getZ());
            if (StringUtils.isNotEmpty(data.getTemp())){
                dataZl.setTemp(Double.parseDouble(data.getTemp()));
            }
            dataZlList.add(dataZl);
            dataZList.add(dataZ);
        }
        if (dataZList.size() == 0){
            return ResponseUtil.failure(500, "导入表格中无有效数据行");
        }
        // 相同PID校验
        List<Long> pidList = dataZList.stream().map(PointDataZVO::getPid).distinct().collect(Collectors.toList());
        for (Long pid : pidList) {
            List<DataApp> collect = dataList.stream().filter(item -> item.getPid().equals(pid)).collect(Collectors.toList());
            long count = collect.stream().filter(item -> "正常".equals(item.getPointStatus())).count();
            //②e.“测点状态”不是“停测”时，传感器状态至少有一个是“正常”；
            if("停测".equals(collect.get(0).getPointStatus()) && count==0){
                return ResponseUtil.failure(500, "测点状态不是停测时，至少一个传感器状态为正常");
            }
            long count2 = collect.stream().filter(item -> item.getZ() == null).count();
            long count3 = collect.stream().filter(item -> item.getZ() != null).count();
            //⑤b.相同PID的观测值都是空，测点状态“停测”，传感器状态 空，观测时间必须是空，备注不能是空；
            if(count2 == collect.size()){
                for (DataApp data : collect) {
                    if (!"停测".equals(data.getPointStatus()) || StringUtils.isNotEmpty(data.getSensorStatus())
                            || data.getGetTime()!=null || StringUtils.isEmpty(data.getNote())){
                        return ResponseUtil.failure(500, "相同PID的观测值都是空，测点状态只能是停测，传感器状态必须是空，观测时间必须是空，备注不能是空");
                    }
                }
            }
            //⑤b如果相同PID有观测值非空，则测点状态不能是“停测”，传感器状态必须是“破坏”，观测时间必须是空
            if (count2>0 && count3>0) {
                for (DataApp data : collect) {
                    if ("停测".equals(data.getPointStatus()) || !"破坏".equals(data.getSensorStatus()) || data.getGetTime()!=null){
                        return ResponseUtil.failure(500, "相同PID有观测值非空，则测点状态不能是停测，传感器状态必须是破坏，观测时间必须是空");
                    }
                }
            }
        }
        // 测点有效性验证
        UploadData data = new UploadData();
        data.setMissionId(uploadData.getMissionId());
        data.setType(uploadData.getType());
        String result = checkZlDb(dataZlList, pointList, data);
        if (StringUtils.isNotEmpty(result)){
            return ResponseUtil.failure(500, result);
        }
        // 计算轴力值
        List<PointDataZVO> dataZResult = calculateZ(data, dataZlList, alarmInfoList, pointList);
        // 中间数据计算
        dataZResult.forEach(item -> calculateData(item, data, alarmInfoList));
        int ptNum = dataZList.size();
        int alarmNum = 0;
        // 数据入库
        pointDataZlService.saveBatch(DzBeanUtils.listCopy(dataZlList, PointDataZl.class));
        pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZResult, PointDataZ.class));
        dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZResult, PointDataZReal.class));
        if (alarmInfoList.size() > 0){
            alarmInfoService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfo.class));
            List<Long> list = alarmInfoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            alarmNum = list.size();
        }
        // 生成报警信息
        createAlarmMsg(alarmInfoList);
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    private ResponseUtil uploadSpFcData(UploadDataApp uploadData, List<Point> pointList) {
        boolean checkTime = false;
        List<PointDataZVO> dataZList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        List<DataApp> dataList = uploadData.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            DataApp data = dataList.get(i);
            PointDataZVO dataZ = new PointDataZVO();
            // 表格列数据格式检查
            String checkInfo = checkFcDataApp(data, dataZ, i, pointList);
            if (StringUtils.isNotEmpty(checkInfo)){
                return ResponseUtil.failure(500, checkInfo);
            }
            // 观测时间有效性验证
            if(!checkTime && dataZ.getGetTime()!=null){
                String checkTimeInfo = checkPtTimeApp(dataZ);
                checkTime = true;
                if (StringUtils.isNotEmpty(checkTimeInfo)){
                    return ResponseUtil.failure(500, checkTimeInfo);
                }
            }
            dataZList.add(dataZ);
        }
        if (dataZList.size() == 0){
            return ResponseUtil.failure(500, "导入表格中无有效数据行");
        }
        // 测点有效性验证
        String result = checkFcDb(dataZList, pointList);
        if (StringUtils.isNotEmpty(result)){
            return ResponseUtil.failure(500, result);
        }
        // 中间数据计算
        UploadData data = new UploadData();
        data.setMissionId(uploadData.getMissionId());
        data.setType(uploadData.getType());
        dataZList.forEach(item -> calculateData(item, data, alarmInfoList));
        int ptNum = dataZList.size();
        int alarmNum = 0;
        // 数据入库
        if (dataZList.size() > 0){
            pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZ.class));
            dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZReal.class));
        }
        if (alarmInfoList.size() > 0){
            alarmInfoService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfo.class));
            List<Long> list = alarmInfoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            alarmNum = list.size();
        }
        // 生成报警信息
        createAlarmMsg(alarmInfoList);
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    private ResponseUtil uploadSpXyData(UploadDataApp uploadData, List<Point> pointList) {
        boolean checkTime = false;
        List<PointDataZVO> dataZList = new ArrayList<>();
        List<PointDataXyzVO> dataXyzList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        List<DataApp> dataList = uploadData.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            DataApp data = dataList.get(i);
            PointDataZVO dataZ = new PointDataZVO();
            // 表格列数据格式检查
            String checkInfo = checkSxDataApp(data, dataZ, i, pointList);
            if (StringUtils.isNotEmpty(checkInfo)){
                return ResponseUtil.failure(500, checkInfo);
            }
            // 观测时间有效性验证
            if(!checkTime && dataZ.getGetTime()!=null){
                String checkTimeInfo = checkPtTimeApp(dataZ);
                checkTime = true;
                if (StringUtils.isNotEmpty(checkTimeInfo)){
                    return ResponseUtil.failure(500, checkTimeInfo);
                }
            }
            // 添加数据
            PointDataXyzVO dataXyz = DzBeanUtils.propertiesCopy(dataZ, PointDataXyzVO.class);
            if (StringUtils.isEmpty(data.getX())){
                dataXyz.setX(null);
            }else {
                dataXyz.setX(Double.parseDouble(data.getX()));
            }
            if (StringUtils.isEmpty(data.getY())){
                dataXyz.setY(null);
            }else {
                dataXyz.setY(Double.parseDouble(data.getY()));
            }
            //  2024/9/12 新增保存 Z 值坐标(自动化采集)
            //  同一个点竖向位移坐标复用
            if (StringUtils.isEmpty(data.getZ())){
                dataXyz.setZ(null);
            }else {
                dataXyz.setZ(Double.parseDouble(data.getZ()));
            }
            dataXyzList.add(dataXyz);
            dataZList.add(dataZ);
        }
        if (dataZList.size() == 0){
            return ResponseUtil.failure(500, "导入表格中无有效数据行");
        }
        // 测点有效性验证
        String result = checkSxDb(dataZList, pointList);
        if (StringUtils.isNotEmpty(result)){
            return ResponseUtil.failure(500, result);
        }
        // 中间数据计算
        UploadData data = new UploadData();
        data.setMissionId(uploadData.getMissionId());
        data.setType(uploadData.getType());
        dataZList.forEach(item -> calculateData(item, data, alarmInfoList));
        int ptNum = dataZList.size();
        int alarmNum = 0;
        // 数据入库
        if (dataZList.size() > 0){
            pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZ.class));
            dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZReal.class));
        }
        // 数据库验证和数据计算
        if (dataXyzList.size() > 0){
            pointDataXyzService.saveBatch(DzBeanUtils.listCopy(dataXyzList, PointDataXyz.class));
        }
        if (alarmInfoList.size() > 0){
            alarmInfoService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfo.class));
            List<Long> list = alarmInfoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            alarmNum = list.size();
        }
        // 生成报警信息
        createAlarmMsg(alarmInfoList);
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    private ResponseUtil uploadSxData(UploadDataApp uploadData, List<Point> pointList) {
        boolean checkTime = false;
        List<PointDataZVO> dataZList = new ArrayList<>();
        List<AlarmInfoVO> alarmInfoList = new ArrayList<>();
        List<DataApp> dataList = uploadData.getDataList();
        for (int i = 0; i < dataList.size(); i++) {
            DataApp data = dataList.get(i);
            PointDataZVO dataZ = new PointDataZVO();
            // 表格列数据格式检查
            String checkInfo = checkSxDataApp(data, dataZ, i, pointList);
            if (StringUtils.isNotEmpty(checkInfo)){
                return ResponseUtil.failure(500, checkInfo);
            }
            // 观测时间有效性验证
            if(!checkTime && dataZ.getGetTime()!=null){
                String checkTimeInfo = checkPtTimeApp(dataZ);
                checkTime = true;
                if (StringUtils.isNotEmpty(checkTimeInfo)){
                    return ResponseUtil.failure(500, checkTimeInfo);
                }
            }
            dataZList.add(dataZ);
        }
        if (dataZList.size() == 0){
            return ResponseUtil.failure(500, "导入表格中无有效数据行");
        }
        // 测点有效性验证
        String result = checkSxDb(dataZList, pointList);
        if (StringUtils.isNotEmpty(result)){
            return ResponseUtil.failure(500, result);
        }
        // 中间数据计算
        UploadData data = new UploadData();
        data.setMissionId(uploadData.getMissionId());
        data.setType(uploadData.getType());
        dataZList.forEach(item -> calculateData(item, data, alarmInfoList));

        int ptNum = dataZList.size();
        int alarmNum = 0;
        // 数据入库
        if (dataZList.size() > 0){
            pointDataZService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZ.class));
            dataZRealService.saveBatch(DzBeanUtils.listCopy(dataZList, PointDataZReal.class));
        }
        if (alarmInfoList.size() > 0){
            alarmInfoService.saveBatch(DzBeanUtils.listCopy(alarmInfoList, AlarmInfo.class));
            List<Long> list = alarmInfoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            alarmNum = list.size();
        }
        // 生成报警信息
        createAlarmMsg(alarmInfoList);
        return ResponseUtil.success("入库成功 "  + ptNum + " 测点; " + alarmNum + " 点超限");
    }

    /**
     * 支撑轴力数据检查
     * @param data 上传数据
     * @param dataZ 入库数据
     * @param i 表格数据行
     */
    private String checkZlDataApp(DataApp data, PointDataZVO dataZ, int i, List<TablePtData> tablePtList) {
        List<String> statusList = Arrays.asList("正常", "停测", "破坏重埋", "基准点修正");
        List<String> statusList2 = Arrays.asList("正常", "破坏");
        //①非空检查 “PID”、“测点名”、“传感器编号”、“位置”、“测点状态”5列数值非空；
        if (data.getPid() == null || StringUtils.isEmpty(data.getName()) || StringUtils.isEmpty(data.getJxgCode())
                || StringUtils.isEmpty(data.getLocation()) || StringUtils.isEmpty(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: PID、测点名、传感器编号、位置、测点状态列不为空";
        }
        //②测点状态有效性检查和传感器状态有效性检查
        //a.“测点状态”列的值只能是“正常,停测,破坏重埋和基准点修正”4项中的内容；
        if(!statusList.contains(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能是[正常,停测,破坏重埋和基准点修正]";
        }
        //b.“传感器状态”列只能是“正常，破坏”2项中的内容；
        if(StringUtils.isNotEmpty(data.getSensorStatus()) && !statusList2.contains(data.getSensorStatus())){
            return "表格第" + (i+1) + "行数据错误: 传感器状态只能是[正常,破坏]";
        }
        //c.相同PID的“测点状态”必须相同；
        List<TablePtData> dataList = tablePtList.stream().filter(item -> item.getPid().equals(dataZ.getPid())).collect(Collectors.toList());
        if (dataList.size()>0 && !dataList.get(0).getStatus().equals(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: 相同PID的测点状态必须相同";
        }
        //d.“测点状态”为“停测”时，传感器状态必须是空；
        if("停测".equals(data.getPointStatus()) && StringUtils.isNotEmpty(data.getSensorStatus())){
            return "表格第" + (i+1) + "行数据错误: 测点状态为停测时传感器状态必须为空";
        }
        // ④观测值 列的值除空值外，都是数值；
        if (StringUtils.isNotEmpty(data.getZ())){
            try {
                dataZ.setZ(Double.valueOf(data.getZ()));
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列只能是数值格式";
            }
            // ⑤观测值、测点状态、传感器状态、观测时间和备注等5列内容逻辑有效性检查
            // a.“观测值”非空，测点状态不能是“停测”，传感器状态必须是“正常”，必须有观测时间；
            if ("停测".equals(data.getPointStatus()) || !"正常".equals(data.getSensorStatus()) || data.getGetTime()==null){
                return "表格第" + (i+1) + "行数据错误: 观测值非空时，测点状态不能为停测，传感器状态必须是正常，观测时间不能为空";
            }
        }
        // 温度格式验证
        if (StringUtils.isNotEmpty(data.getTemp())){
            try {
                Double.parseDouble(data.getTemp());
            }catch (Exception e){
                return "表格第" + (i+1) + "行数据错误: 温度列非空时只能是数值";
            }
        }
        //属性赋值
        dataZ.setPid(data.getPid()).setName(data.getName()).setStatus(data.getPointStatus())
                .setGetTime(data.getGetTime()).setNote(data.getNote());
        return null;
    }

    /**
     * 水平分层数据检查
     * @param data 上传数据
     * @param dataZ 入库数据
     * @param i 表格数据行
     */
    private String checkFcDataApp(DataApp data, PointDataZVO dataZ, int i, List<Point> pointList) {
        // ①非空检查 “PID”、“测点名”、“深度”、“观测值”、“测点状态”和“观测时间”6列数值非空；
        if (data.getPid()==null || StringUtils.isEmpty(data.getName()) || StringUtils.isEmpty(data.getDepth()) || data.getZ()==null
                || StringUtils.isEmpty(data.getPointStatus()) || data.getGetTime()==null){
            return "表格第" + (i+1) + "行数据错误: 除备注外所有列不为空";
        }
        dataZ.setPid(data.getPid());
        dataZ.setGpName(data.getName());
        // ②测点状态列的值只能是“正常”；
        if(!"正常".equals(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能[正常]";
        }
        dataZ.setGetTime(data.getGetTime());
        dataZ.setName(data.getDepth());
        try {
            dataZ.setZ(Double.valueOf(data.getZ()));
        }catch (Exception e){
            return  "表格第" + (i+1) + "行数据错误: 观测值列只能是数值格式";
        }
        dataZ.setStatus(data.getPointStatus());
        dataZ.setNote(data.getNote());
        return null;
    }

    /**
     * 竖向位移数据检查
     * @param data 上传数据
     * @param dataZ 入库数据
     * @param i 表格数据行
     */
    private String checkSxDataApp(DataApp data, PointDataZVO dataZ, int i, List<Point> points) {
        List<String> statusList = Arrays.asList("正常", "停测", "破坏重埋", "基准点修正");
        // ①非空检查 “PID”、“测点名”、“测点状态”3列数值非空；
        if (data.getPid()==null || StringUtils.isEmpty(data.getName()) || StringUtils.isEmpty(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: PID、测点名、测点状态三列不为空";
        }
        dataZ.setPid(data.getPid());
        //② 测点状态值只能是“正常,停测,破坏重埋和基准点修正”4项中的内容
        if(!statusList.contains(data.getPointStatus())){
            return "表格第" + (i+1) + "行数据错误: 测点状态只能是[正常,停测,破坏重埋和基准点修正]";
        }
        if (data.getGetTime() != null) {dataZ.setGetTime(data.getGetTime());}
        //④ “观测值”列的值除空值外，都是数值；
        if (StringUtils.isNotEmpty(data.getX())){
            try {
                Double.valueOf(data.getX());
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        if (StringUtils.isNotEmpty(data.getY())){
            try {
                Double.valueOf(data.getY());
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        if (StringUtils.isNotEmpty(data.getZ())){
            try {
                dataZ.setZ(Double.valueOf(data.getZ()));
            }catch (Exception e){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时只能是数值格式";
            }
        }
        // 计算Z值
        if (data.getIsXy() == 1 && StringUtils.isNotEmpty(data.getX()) && StringUtils.isNotEmpty(data.getY())) {
            Optional<Point> optional = points.stream().filter(item -> item.getId().equals(dataZ.getPid())).findAny();
            double azimuth = optional.map(Point::getAzimuth).orElse(0.0);
            double z = BaseDataBiz.coordTransfer(Double.parseDouble(data.getX()), Double.parseDouble(data.getY()), azimuth).get(1);
            dataZ.setZ(z);
        }
        //⑤观测值、测点状态、观测时间和备注等4列内容逻辑有效性检查
        //a.“观测值”非空，测点状态不能是“停测”，必须有观测时间；
        //b.“观测值”空，测点状态只能是“停测”，观测时间必须是空，备注不能是空；
        if((data.getIsXy() ==1 && data.getX() == null && data.getY() == null) || (data.getIsXy()==0 && data.getZ() == null)
                || (data.getIsXy()==2 && data.getX() == null && data.getY() == null && data.getZ() == null)){
            if (!"停测".equals(data.getPointStatus()) || data.getGetTime()!=null || StringUtils.isEmpty(data.getNote())){
                return  "表格第" + (i+1) + "行数据错误: 观测值列为空时状态只能是停测，观测时间为空，备注不为空";
            }
        }
        if((data.getIsXy()==1 && StringUtils.isNotEmpty(data.getX()) && StringUtils.isNotEmpty(data.getY()))
                || (data.getIsXy()==0 && StringUtils.isNotEmpty(data.getZ()))
                || (data.getIsXy()==2 && StringUtils.isNotEmpty(data.getX()) && StringUtils.isNotEmpty(data.getY()) && StringUtils.isNotEmpty(data.getZ()))){
            if ("停测".equals(data.getPointStatus()) || data.getGetTime()==null){
                return  "表格第" + (i+1) + "行数据错误: 观测值列非空时状态不能是停测，观测时间不为空";
            }
        }
        dataZ.setName(data.getName());
        dataZ.setStatus(data.getPointStatus());
        dataZ.setNote(data.getNote());
        return null;
    }

    @Override
    public ResponseUtil getZlUploadData(Long groupId){
        List<PointDataZlVO> dataList = new ArrayList<>();
        // 查询监测任务包含的测点信息
        List<Point> points = pointService.queryByGroupId(groupId);
        List<Long> pidList = points.stream().map(Point::getId).collect(Collectors.toList());
        List<SensorZl> sensorZls = new ArrayList<>();
        if (pidList.size() > 0){
            LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SensorZl::getPointId, pidList).orderByDesc(SensorZl::getCreateTime);
            sensorZls = sensorZlService.list(wrapper);
        }
        // PID 	测点名	传感器编号	位置	观测值(Hz)	温度(℃)	测点状态	传感器状态	时间	备注
        for (Point point : points) {
            // 获取传感器列表
            List<SensorZl> list = sensorZls.stream().filter(item -> item.getPointId().equals(point.getId()))
                    .collect(Collectors.toList());
            if (list.size() > 0) {
                for (SensorZl zl : list) {
                    PointDataZlVO vo = new PointDataZlVO();
                    vo.setPid(point.getId());
                    vo.setName(point.getName());
                    vo.setJxgCode(zl.getJxgCode());
                    vo.setLocation(zl.getLocation());
                    vo.setPointStatus(point.getStop() ? "停测" : "正常");
                    vo.setSensorStatus(zl.getBroken() ? "破坏" : "正常");
                    vo.setGetTime(new Date());
                    dataList.add(vo);
                }
            }
        }
        return ResponseUtil.success(dataList);
    }

    /**
     * 生成报警信息
     */
    public void createAlarmMsg(List<AlarmInfoVO> infoList){
        ThreadPoolUtil.getPool().execute(() -> {
            if(infoList==null || infoList.isEmpty()) {
                return;
            }
            List<Long> pidList = infoList.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
            List<Point> points = pointService.listByIds(pidList);
            if (points.isEmpty()){
                return;
            }
            List<Long> ptGroupIds = points.stream().map(Point::getPtGroupId).distinct().collect(Collectors.toList());
            List<PtGroup> ptGroups = ptGroupService.listByIds(ptGroupIds);
            ptGroups = ptGroups.stream().filter(item -> StringUtils.isNoneEmpty(item.getAlarmReceiveIds(), item.getAlarmDistributeIds())).collect(Collectors.toList());
            if (ptGroups.isEmpty()){
                return;
            }
            List<AlarmDistribute> distributes = new ArrayList<>();
            List<Groups> groups = new ArrayList<>();
            List<User> users = new ArrayList<>();
            // 获取分发，人员编组和人员信息
            getExtraInfo(ptGroups, distributes, groups, users);
            if (distributes.isEmpty() || groups.isEmpty() || users.isEmpty()){
                return;
            }
            List<Long> projectIds = infoList.stream().map(AlarmInfoVO::getProjectId).collect(Collectors.toList());
            List<Long> missionIds = infoList.stream().map(AlarmInfoVO::getMissionId).collect(Collectors.toList());
            List<Project> projects = projectService.list(new LambdaQueryWrapper<Project>().in(Project::getId, projectIds)
                    .select(Project::getId,Project::getName));
            List<ProMission> missions = missionService.list(new LambdaQueryWrapper<ProMission>().in(ProMission::getId, missionIds)
                    .select(ProMission::getId,ProMission::getName,ProMission::getTypeId,ProMission::getValueUnit,ProMission::getDeltUnit));
            List<MonitorType> monitorTypes = monitorTypeService.list();
            for (PtGroup ptGroup : ptGroups) {
                //获取测点组中报警信息集合
                List<Long> pointIds = points.stream().filter(item -> item.getPtGroupId().equals(ptGroup.getId())).map(Point::getId).collect(Collectors.toList());
                List<AlarmInfoVO> list = infoList.stream().filter(item -> pointIds.contains(item.getPtId())).collect(Collectors.toList());
                if (list.isEmpty()) {
                    continue;
                }
                // 多线程提高执行效率
                ThreadPoolUtil.getPool().execute(() -> {
                    sentAlarmMsg(ptGroup, list, distributes, users, projects, missions, monitorTypes);
                });
            }
        });
    }

    /**
     * 查询分发规则和分发人员信息
     */
    private void getExtraInfo(List<PtGroup> ptGroups, List<AlarmDistribute> distributes, List<Groups> groups, List<User> users) {
        // 获取分发信息
        List<String> distributeStr = ptGroups.stream().map(PtGroup::getAlarmDistributeIds).collect(Collectors.toList());
        List<Long> distributeIds = new ArrayList<>();
        distributeStr.forEach(item -> {
            for (String distributeId : item.split(",")) {
                distributeIds.add(Long.valueOf(distributeId));
            }
        });
        distributes.addAll(distributeService.listByIds(distributeIds));
        // 获取编组信息
        List<String> receives = ptGroups.stream().map(PtGroup::getAlarmReceiveIds).collect(Collectors.toList());
        List<Long> receiveIds = new ArrayList<>();
        receives.forEach(item -> {
            for (String receiveId : item.split(",")) {
                receiveIds.add(Long.valueOf(receiveId));
            }
        });
        groups.addAll(groupService.listByIds(receiveIds));
        // 获取编组包含人员信息
        List<Long> groupIds = groups.stream().map(Groups::getId).collect(Collectors.toList());
        if (!groupIds.isEmpty()){
            users.addAll(userService.listByGroupIds(groupIds));
        }
    }

    /**
     * 发送报警信息
     */
    private void sentAlarmMsg(PtGroup ptGroup, List<AlarmInfoVO> list, List<AlarmDistribute> distributes, List<User> users, List<Project> projects, List<ProMission> missions, List<MonitorType> monitorTypes) {
        String[] splitReceive = ptGroup.getAlarmReceiveIds().split(",");
        String[] splitDistribute = ptGroup.getAlarmDistributeIds().split(",");
        Company company = companyService.getById(users.get(0).getCompanyId());
        for (int i = 0; i < splitReceive.length; i++) {
            String receive = splitReceive[i];
            List<User> userList = users.stream().filter(item -> (item.getGroupId() + "").equals(receive)).collect(Collectors.toList());
            String distributeId = i < splitDistribute.length ? splitDistribute[i] : splitDistribute[splitDistribute.length - 1];
            Optional<AlarmDistribute> optional = distributes.stream().filter(item -> (item.getId() + "").equals(distributeId)).findAny();
            if (userList.isEmpty() || !optional.isPresent()
                    || StringUtils.isAllEmpty(optional.get().getSmsAlarmLevel(),optional.get().getWxAlarmLevel())){
                continue;
            }
            AlarmDistribute alarmDistribute = optional.get();
            String smsAlarmLevel = alarmDistribute.getSmsAlarmLevel();
            // 发送短信报警
            if (StringUtils.isNotEmpty(smsAlarmLevel)){
                //判断是否开通了短信业务
                if (company != null && company.getEnableSms()) {
                    sendSmsMsg(smsAlarmLevel, list, userList, projects, missions, monitorTypes);
                } else {
                    log.info("公司未开通短信业务");
                }
            }
            String wxAlarmLevel = alarmDistribute.getWxAlarmLevel();
            // 发送微信报警: 0419 确认使用企业微信应用消息发送报警
            if (StringUtils.isNotEmpty(wxAlarmLevel)){
//                sendWxMsg(wxAlarmLevel, list, userList, projects, missions, monitorTypes);
                sendQwTextMsg(wxAlarmLevel, list, userList, projects, missions, monitorTypes);
            }
        }
    }

    /**
     * 短信报警
     */
    private void sendSmsMsg(String smsAlarmLevel, List<AlarmInfoVO> list, List<User> userList, List<Project> projects, List<ProMission> missions, List<MonitorType> monitorTypes) {
        //获取包含报警等级
        List<String> alarmLevel = getAlarmLevelList(smsAlarmLevel);

        List<User> users = userList.stream().filter(item -> StringUtils.isNotEmpty(item.getPhone())).collect(Collectors.toList());
        for (AlarmInfoVO infoVO : list) {
            if (alarmLevel.contains(infoVO.getAlarmLevel())){
                // 构建短信
                Optional<Project> project = projects.stream().filter(item -> item.getId().equals(infoVO.getProjectId())).findAny();
                Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(infoVO.getMissionId())).findAny();
                Map<String, String> map = new HashMap<>(16);
                map.put("project", project.isPresent() ? project.get().getName() : "未知工程");
                map.put("mission", mission.isPresent() ? mission.get().getName() : "未知任务");
                // 构建报警内容
                ProMission proMission = mission.orElse(null);
                createAlarmContent(infoVO, 1, proMission, monitorTypes, map);
                users.forEach(user -> {
                    // 发送信息变量顺序: project mission point time alarm_item value over_level limit_value
                    String content = map.get("project") + "|" + map.get("mission") + "|" + map.get("point")
                            + "|" + map.get("time") + "|" + map.get("alarm_item") + "|" + map.get("value")
                            + "|" + map.get("over_level") + "|" + map.get("limit_value");
                    HySmsUtil.sendAlarmMsg(user.getPhone(), 1, content);
                    // 发送信息
//                    SmsUtil.sendAlarmMsg(map, user.getPhone(), 1);
                });
            }
        }
    }

    /**
     * 创建报警内容
     * type : 1 - 短信； 2 - 微信； 3 - 企微
     */
    private String createAlarmContent(AlarmInfoVO infoVO, int type, ProMission mission, List<MonitorType> monitorTypes, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        String[] originInfo = infoVO.getAlarmOrigin().split(",").length == 3 ? infoVO.getAlarmOrigin().split(",") : new String[]{"", "", ""};
        String[] alarmLevel = getAlarmLevelString(infoVO.getAlarmLevel());
        // 类型， 测量值单位， 变化量单位， 变化速率单位
        String[] valueKey = {"", "", "", ""};
        double value = 0.0 ; double deltValue = 0.0;double TotalValue = 0.0; double vDeltValue = 0.0;
        if (mission != null){
            valueKey[1] = mission.getValueUnit() !=null ? ("(" + mission.getValueUnit() + ")") : "";
            valueKey[2] = mission.getDeltUnit() !=null ? ("(" + mission.getDeltUnit() + ")") : "";
            valueKey[3] = mission.getDeltUnit() !=null ? ("(" + mission.getDeltUnit() + "/d)") : "";
            Optional<MonitorType> monitorType = monitorTypes.stream().filter(item -> item.getId().equals(mission.getTypeId())).findAny();
            if (monitorType.isPresent()){
                if (MissionTypeConst.HAND_XYZ_OFFSET.equals(monitorType.get().getType())
                        || MissionTypeConst.AUTO_XYZ_OFFSET.equals(monitorType.get().getType())){
                    TableDataCalculate dataCalculate = getData(infoVO, valueKey, mission);
                    if (dataCalculate != null){
                        value = dataCalculate.getValue();
                        deltValue = dataCalculate.getDeltValue();
                        TotalValue = dataCalculate.getTotalValue();
                        vDeltValue = dataCalculate.getVDeltValue();
                    }
                }else {
                    LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<PointDataZ>()
                            .eq(PointDataZ::getRecycleNum, infoVO.getRecycleNum()).eq(PointDataZ::getPid, infoVO.getPtId())
                            .orderByDesc(PointDataZ::getGetTime);
                    List<PointDataZ> dataZList = pointDataZService.list(wrapper);
                    if (!dataZList.isEmpty()){
                       value = dataZList.get(0).getZ();
                       deltValue = dataZList.get(0).getDeltZ();
                       TotalValue = dataZList.get(0).getTotalZ();
                       vDeltValue = dataZList.get(0).getVDeltZ();
                    }
                }
            }
        }
        // 报警信息--工程名称：${project}；监测任务：${mission}；
        // 报警内容 [监测点：${point}；报警项：${alarmItem}；
        // 报警等级：${alarmLevel}本次值${value}变化量${deltValue}累计变化量${totalValue}变化速率${vDeltValue}；
        // 报警时间：${time}] 请及时处理！
        if (type == 1){
            map.put("point", originInfo[0]);
            map.put("time", dateFormat.format(infoVO.getAlarmTime()));
            map.put("alarm_item", valueKey[0] + getAlarmTypeString(originInfo[2]));
            map.put("value", getAlarmValueString(getAlarmTypeString(originInfo[2]), valueKey, infoVO, value, deltValue, vDeltValue, TotalValue));
            map.put("over_level", alarmLevel[1]);
            String thresholdStr = infoVO.getAbs() ? infoVO.getThreshold() + "(绝对值)" : infoVO.getThreshold();
            map.put("limit_value", thresholdStr);
        }
        if (type == 2){
            sb.append("测点名: ").append(originInfo[0]).append("\r\n")
                    .append("报警项: ").append(originInfo[2]).append("\r\n")
                    .append("报警等级: ").append(alarmLevel[0]).append("\r\n")
                    .append(valueKey[0]).append("本次值").append(valueKey[1]).append(": ").append(String.format("%.4f", infoVO.getVal() != null ? infoVO.getVal() : value)).append("\r\n")
                    .append(valueKey[0]).append("变化量").append(valueKey[2]).append(": ").append(String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : deltValue)).append("\r\n")
                    .append(valueKey[0]).append("累计变化量").append(valueKey[2]).append(": ").append(String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : TotalValue)).append("\r\n")
                    .append(valueKey[0]).append("变化速率").append(valueKey[3]).append(": ").append(String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : vDeltValue)).append("\r\n")
                    .append("报警时间：").append(dateFormat.format(infoVO.getAlarmTime()));
        }
        if (type == 3){
            map.put("point", "测点名: " + originInfo[0]);
            map.put("alarmItem", "报警项: " + originInfo[2]);
            map.put("alarmLevel", "报警等级：" + alarmLevel[0]);
            String thresholdStr = infoVO.getAbs() ? infoVO.getThreshold() + "(绝对值)" : infoVO.getThreshold();
            map.put("threshold", "报警阈值：" + thresholdStr);
            map.put("value", valueKey[0] + "本次值" + valueKey[1] + ": " + String.format("%.4f", infoVO.getVal() != null ? infoVO.getVal() : value));
            map.put("deltValue", valueKey[0] + "变化量" + valueKey[2] + ": " + String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : deltValue));
            map.put("totalValue", valueKey[0] + "累计变化量" + valueKey[2] + ": " + String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : TotalValue));
            map.put("vDeltValue", valueKey[0] + "变化速率" + valueKey[3] + ": " + String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : vDeltValue));
            map.put("time", "报警时间：" + dateFormat.format(infoVO.getAlarmTime()));
        }
        return sb.toString();
    }

    /**
     * 构建数据
     */
    private TableDataCalculate getData(AlarmInfoVO infoVO, String[] valueKey, ProMission mission) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<PointDataXyzh>()
                .eq(PointDataXyzh::getRecycleNum, infoVO.getRecycleNum()).eq(PointDataXyzh::getPid, infoVO.getPtId())
                .orderByDesc(PointDataXyzh::getGetTime);
        List<PointDataXyzh> dataXyzhList = pointDataXyzhService.list(wrapper);
        if (dataXyzhList.size() > 0){
            TableDataCalculate data = new TableDataCalculate();
            String[] originInfo = infoVO.getAlarmOrigin().split(",");
            if (originInfo.length != 3){return null;}
            PointDataXyzh dataXyzh = dataXyzhList.get(0);
            // X坐标,Y坐标,Z坐标,平面位移(P),平行断面位移(S),垂直断面位移(T)
            switch (originInfo[1]){
                case "X坐标":
                    data.setValue(dataXyzh.getX());
                    data.setDeltValue(dataXyzh.getDeltX());
                    data.setTotalValue(dataXyzh.getTotalX());
                    data.setVDeltValue(dataXyzh.getVDeltX());
                    valueKey[0] = "X";
                    break;
                case "Y坐标":
                    data.setValue(dataXyzh.getY());
                    data.setDeltValue(dataXyzh.getDeltY());
                    data.setTotalValue(dataXyzh.getTotalY());
                    data.setVDeltValue(dataXyzh.getVDeltY());
                    valueKey[0] = "Y";
                    break;
                case "Z坐标":
                    data.setValue(dataXyzh.getZ());
                    data.setDeltValue(dataXyzh.getDeltZ());
                    data.setTotalValue(dataXyzh.getTotalZ());
                    data.setVDeltValue(dataXyzh.getVDeltZ());
                    valueKey[0] = "Z";
                    break;
                case "平面位移(P)":
                    data.setValue(dataXyzh.getP());
                    data.setDeltValue(dataXyzh.getDeltP());
                    data.setTotalValue(dataXyzh.getTotalP());
                    data.setVDeltValue(dataXyzh.getVDeltP());
                    valueKey[0] = "P";
                    break;
                case "平行断面位移(S)":
                    data.setValue(dataXyzh.getS());
                    data.setDeltValue(dataXyzh.getDeltS());
                    data.setTotalValue(dataXyzh.getTotalS());
                    data.setVDeltValue(dataXyzh.getVDeltS());
                    valueKey[0] = "S";
                    break;
                case "垂直断面位移(T)":
                    data.setValue(dataXyzh.getT());
                    data.setDeltValue(dataXyzh.getDeltT());
                    data.setTotalValue(dataXyzh.getTotalT());
                    data.setVDeltValue(dataXyzh.getVDeltT());
                    valueKey[0] = "T";
                    break;
            }
            return data;
        }
        return null;
    }

    /**
     * 报警等级装换
     */
    private String[] getAlarmLevelString(String alarmLevel) {
        switch (alarmLevel){
            case "1":
                return new String[]{"超预警值", "预警"};
            case "2":
                return new String[]{"超报警值", "报警"};
            case "3":
                return new String[]{"超控制值", "控制"};
            default:
                return new String[]{"未知等级", "未知"};
        }
    }

    /**
     * 报警等级类型
     */
    private String getAlarmTypeString(String alarmType) {
        switch (alarmType){
            case "单次变化量":
                return "单次";
            case "累计变化量":
                return "累计";
            case "日变化速率":
                return "日速率";
            case "测量值":
                return "测量值";
            default:
                return "未知";
        }
    }

    /**
     * 获取报警值+单位
     */
    private String getAlarmValueString(String alarmType, String[] valueKey, AlarmInfoVO infoVO,
                                       double val, double dVal, double vdVal, double tVal) {
        // 类型， 测量值单位， 变化量单位， 变化速率单位
        switch (alarmType){
            case "单次":
                return String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : dVal) + valueKey[2];
            case "累计":
                return String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : tVal) + valueKey[2];
            case "日速率":
                return String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : vdVal) + valueKey[3];
            case "测量值":
                return String.format("%.4f", infoVO.getVal() != null ? infoVO.getVal() : val) + valueKey[1];
            default:
                return String.format("%.2f", infoVO.getVal() != null ? infoVO.getVal() : 0.0) + "mm";
        }
    }

    /**
     * 获取报警等级集合
     **/
    private List<String> getAlarmLevelList(String alarmLevelStr) {
        String[] smsLevels = alarmLevelStr.split(",");
        List<String> alarmLevel = new ArrayList<>();
        for (String smsLevel : smsLevels) {
            switch (smsLevel){
                case "超预警值":
                    alarmLevel.add("1");
                    break;
                case "超报警值":
                    alarmLevel.add("2");
                    break;
                case "超控制值":
                    alarmLevel.add("3");
                    break;
                default:
            }
        }
        return alarmLevel;
    }

    /**
     * 微信报警
     */
    private void sendWxMsg(String wxAlarmLevel, List<AlarmInfoVO> list, List<User> userList, List<Project> projects, List<ProMission> missions, List<MonitorType> monitorTypes) {
        //获取包含报警等级
        List<String> alarmLevel = getAlarmLevelList(wxAlarmLevel);

        List<User> users = userList.stream().filter(item -> StringUtils.isNotEmpty(item.getAppId())).collect(Collectors.toList());
        for (AlarmInfoVO infoVO : list) {
            if (alarmLevel.contains(infoVO.getAlarmLevel())){
                Optional<Project> project = projects.stream().filter(item -> item.getId().equals(infoVO.getProjectId())).findAny();
                Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(infoVO.getMissionId())).findAny();
                ProMission proMission = mission.orElse(null);
                String key3Content = createAlarmContent(infoVO, 2, proMission, monitorTypes, new HashMap<>());
                // 构建微信
                ActivityMsgVO vo = new ActivityMsgVO();
                vo.setContent(getAlarmLevelString(infoVO.getAlarmLevel()) + "报警");
                vo.setKey1(project.isPresent() ? project.get().getName() : "未知工程");
                vo.setKey2(mission.isPresent() ? mission.get().getName() : "未知任务");
                // 构建报警内容
                vo.setKey3(key3Content);
                vo.setRemark("请及时处理!");
                users.forEach(user -> {
                    // 发送微息
                    weiXinUtil.sentAlarmTemplate(user.getAppId(), vo);
                });
            }
        }
    }

    /**
     * 企业微信文本信息
     */
    private void sendQwTextMsg(String wxAlarmLevel, List<AlarmInfoVO> list, List<User> userList, List<Project> projects, List<ProMission> missions, List<MonitorType> monitorTypes) {
        //获取包含报警等级
        List<String> alarmLevel = getAlarmLevelList(wxAlarmLevel);

        List<User> filterUsers = userList.stream().filter(item -> StringUtils.isNotEmpty(item.getAppId())).collect(Collectors.toList());
        if (filterUsers.isEmpty()){
            return;
        }
        String useridStr = filterUsers.stream().map(User::getAppId).collect(Collectors.joining("|"));
        //修改：25-03-12同一个点的报警信息合并为一条发送
        List<Long> pidList = list.stream().map(AlarmInfoVO::getPtId).distinct().collect(Collectors.toList());
        for (Long pid : pidList) {
            List<AlarmInfoVO> alarmList = list.stream().filter(item -> item.getPtId().equals(pid)).collect(Collectors.toList());
            boolean isSend = false;
            Optional<Project> project = projects.stream().filter(item -> item.getId().equals(alarmList.get(0).getProjectId())).findAny();
            Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(alarmList.get(0).getMissionId())).findAny();
            ProMission proMission = mission.orElse(null);
            //构造描述信息
            StringBuilder sb = new StringBuilder();
            sb.append("【超限报警信息】\r\n\r\n");
            sb.append("工程名称: ").append(project.orElse(new Project()).getName()).append("\r\n");
            sb.append("监测任务: ").append(mission.orElse(new ProMission()).getName()).append("\r\n");

            AppMsgVO msgVO = new AppMsgVO();
            msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000002L);
            for (AlarmInfoVO infoVO : alarmList) {
                if (alarmLevel.contains(infoVO.getAlarmLevel())){
                    Map<String, String> map = new HashMap<>();
                    createAlarmContent(infoVO, 3, proMission, monitorTypes, map);
                    if (isSend) {
                        sb.append("\r\n");
                    }
                    sb.append(map.get("point")).append("\r\n");
                    sb.append(map.get("alarmItem")).append("(").append(getAlarmLevelString(infoVO.getAlarmLevel())[0]).append(")\r\n");
                    if (map.get("alarmItem").contains("测量值")){
                        sb.append(map.get("value")).append("\r\n");
                    }
                    if (map.get("alarmItem").contains("单次变化量")){
                        sb.append(map.get("deltValue")).append("\r\n");
                    }
                    if (map.get("alarmItem").contains("累计变化量")){
                        sb.append(map.get("totalValue")).append("\r\n");
                    }
                    if (map.get("alarmItem").contains("日变化速率")){
                        sb.append(map.get("vDeltValue")).append("\r\n");
                    }
                    sb.append(map.get("threshold"));
                    isSend = true;
                }
            }
            if (isSend) {
                TextVO textVO = new TextVO(sb.toString());
                msgVO.setText(textVO);
                //发送应用信息
                log.info("发送测点报警信息: {}", sb);
                qwUtil.sendAppTextMsg(msgVO);
            }
        }
//        for (AlarmInfoVO infoVO : list) {
//            if (alarmLevel.contains(infoVO.getAlarmLevel())){
//                Optional<Project> project = projects.stream().filter(item -> item.getId().equals(infoVO.getProjectId())).findAny();
//                Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(infoVO.getMissionId())).findAny();
//                ProMission proMission = mission.orElse(null);
//
//                AppMsgVO msgVO = new AppMsgVO();
//                msgVO.setTouser(useridStr).setMsgtype("text").setAgentid(1000002L);
//                //构造描述信息
//                StringBuilder sb = new StringBuilder();
//                sb.append("【").append(getAlarmLevelString(infoVO.getAlarmLevel())).append("信息】\r\n\r\n");
//                sb.append("工程名称: ").append(project.orElse(new Project()).getName()).append("\r\n");
//                sb.append("监测任务: ").append(mission.orElse(new ProMission()).getName()).append("\r\n");
//                Map<String, String> map = new HashMap<>();
//                createAlarmContent(infoVO, 3, proMission, monitorTypes, map);
//                sb.append(map.get("point")).append("\r\n");
//                sb.append(map.get("alarmItem")).append("\r\n");
//                if (map.get("alarmItem").contains("测量值")){
//                    sb.append(map.get("value")).append("\r\n");
//                }
//                if (map.get("alarmItem").contains("单次变化量")){
//                    sb.append(map.get("deltValue")).append("\r\n");
//                }
//                if (map.get("alarmItem").contains("累计变化量")){
//                    sb.append(map.get("totalValue")).append("\r\n");
//                }
//                if (map.get("alarmItem").contains("日变化速率")){
//                    sb.append(map.get("vDeltValue")).append("\r\n");
//                }
//                sb.append(map.get("threshold"));
//                TextVO textVO = new TextVO(sb.toString());
//                msgVO.setText(textVO);
//                //发送应用信息
//                qwUtil.sendAppTextMsg(msgVO);
//            }
//        }
    }

    /**
     * 企业微信文本卡片信息
     */
    private void sendQwCardMsg(String wxAlarmLevel, List<AlarmInfoVO> list, List<User> userList, List<Project> projects, List<ProMission> missions, List<MonitorType> monitorTypes) {
        //获取包含报警等级
        List<String> alarmLevel = getAlarmLevelList(wxAlarmLevel);

        List<User> filterUsers = userList.stream().filter(item -> StringUtils.isNotEmpty(item.getAppId())).collect(Collectors.toList());
        if (filterUsers.size() == 0){
            return;
        }
        String useridStr = filterUsers.stream().map(User::getAppId).collect(Collectors.joining("|"));
        for (AlarmInfoVO infoVO : list) {
            if (alarmLevel.contains(infoVO.getAlarmLevel())){
                Optional<Project> project = projects.stream().filter(item -> item.getId().equals(infoVO.getProjectId())).findAny();
                Optional<ProMission> mission = missions.stream().filter(item -> item.getId().equals(infoVO.getMissionId())).findAny();
                ProMission proMission = mission.orElse(null);

                AppMsgVO msgVO = new AppMsgVO();
                msgVO.setTouser(useridStr).setMsgtype("textcard").setAgentid(1000002L);
                TextCardVO cardVO = new TextCardVO();
                cardVO.setTitle(getAlarmLevelString(infoVO.getAlarmLevel()) + "报警");
                //构造描述信息
                StringBuilder sb = new StringBuilder();
                sb.append("<div>工程名称: ").append(project.orElse(new Project()).getName()).append("</div>");
                sb.append("<div>监测任务: ").append(mission.orElse(new ProMission()).getName()).append("</div>");
                Map<String, String> map = new HashMap<>();
                createAlarmContent(infoVO, 3, proMission, monitorTypes, map);
                sb.append("<div>").append(map.get("point")).append("</div>");
                sb.append("<div>").append(map.get("alarmItem")).append("</div>");
                sb.append("<div>").append(map.get("alarmLevel")).append("</div>");
                sb.append("<div>").append(map.get("value")).append("</div>");
                sb.append("<div>").append(map.get("deltValue")).append("</div>");
                sb.append("<div>").append(map.get("totalValue")).append("</div>");
                sb.append("<div>").append(map.get("vDeltValue")).append("</div>");
                sb.append("<div>").append(map.get("time")).append("</div>");
                sb.append("<div class=\\\"highlight\\\">请及时处理！</div>");
                cardVO.setDescription(sb.toString());
                cardVO.setUrl("https://www.baidu.com");
                cardVO.setBtntxt("更多");
                msgVO.setTextcard(cardVO);
                //发送应用信息
                qwUtil.sendAppCartMsg(msgVO);
            }
        }
    }
    // endregion

}
