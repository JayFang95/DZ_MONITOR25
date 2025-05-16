package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.IDataOtherBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.FileUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmInfo;
import com.dzkj.entity.data.*;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.survey.RobotSurveyData;
import com.dzkj.service.alarm_setting.IAlarmInfoService;
import com.dzkj.service.data.*;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtStopService;
import com.dzkj.service.survey.IRobotSurveyDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/20
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
@Component
public class DataOtherBizImpl implements IDataOtherBiz {

    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataZRealService dataZRealService;
    @Autowired
    private IPointDataXyzService dataXyzService;
    @Autowired
    private IPointDataZlService dataZlService;
    @Autowired
    private IPtStopService stopService;
    @Autowired
    private IRobotSurveyDataService surveyDataService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IAlarmInfoService alarmInfoService;
    @Autowired
    private IRobotSurveyDataService robotSurveyDataService;

    @Override
    public IPage<RobotSurveyDataVO> getXyzPage(Integer pi, Integer ps, OtherDataCondition condition) {
        setTimeDate(condition);
        IPage<RobotSurveyDataVO> page = new Page<>(pi, ps);
        if(ps == CommonConstant.SEARCH_ALL_NO){
            List<RobotSurveyDataVO> list = DzBeanUtils.listCopy(surveyDataService.getList(condition), RobotSurveyDataVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = DzBeanUtils.pageCopy(surveyDataService.getPage(pi, ps, condition), RobotSurveyDataVO.class);
        }
        page.getRecords().forEach(item -> item.setFileName(DateUtil.dateToDateString(item.getCreateTime(), DateUtil.yyyy_MM_dd_HH_mm_ss_EN) + ".txt"));
        return page;
    }

    @Override
    public IPage<PointDataXyzhRealVO> xyzHandPage(Integer pi, Integer ps, OtherDataCondition condition){
        Page<PointDataXyzhRealVO> page = new Page<>(pi, ps);
        if (condition.getGroupIds() == null || condition.getGroupIds().size()<=0){
            page.setRecords(new ArrayList<>());
            page.setTotal(0);
            return page;
        }
        setTimeDate(condition);
        if(ps == CommonConstant.SEARCH_ALL_NO){
            List<PointDataXyzhRealVO> list = dataXyzhRealService.getList(condition);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = dataXyzhRealService.getPage(pi, ps, condition);
        }
        return page;
    }

    /**
     * @description 设置创建时间
    **/
    private void setTimeDate(OtherDataCondition condition) {
        Integer timeNum = condition.getTimeNum();
        if (timeNum!=null && timeNum > 0){
            condition.setCreateTime(DateUtil.getDateOfDay(new Date(), -timeNum));
        }
    }

    @Override
    public IPage<PointDataXyzVO> getXyPage(Integer pi, Integer ps, OtherDataCondition condition) {
        Page<PointDataXyzVO> page = new Page<>(pi, ps);
        if (condition.getGroupIds() == null || condition.getGroupIds().size()<=0){
            page.setRecords(new ArrayList<>());
            page.setTotal(0);
            return page;
        }
        setTimeDate(condition);
        if(ps == CommonConstant.SEARCH_ALL_NO){
            List<PointDataXyzVO> list = dataXyzService.getList(condition);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = dataXyzService.getPage(pi, ps, condition);
        }
        return page;
    }

    @Override
    public IPage<PointDataZlVO> getZlPage(Integer pi, Integer ps, OtherDataCondition condition) {
        Page<PointDataZlVO> page = new Page<>(pi, ps);
        if (condition.getGroupIds() == null || condition.getGroupIds().size()<=0){
            page.setRecords(new ArrayList<>());
            page.setTotal(0);
            return page;
        }
        setTimeDate(condition);
        if(ps == CommonConstant.SEARCH_ALL_NO){
            List<PointDataZlVO> list = dataZlService.getList(condition);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = dataZlService.getPage(pi, ps, condition);
        }
        return page;
    }

    @Override
    public IPage<PtStopVO> getStopPage(Integer pi, Integer ps, PtDataStopCondition condition) {
        IPage<PtStopVO> page = new Page<>(pi, ps);
        if (condition.getGroupIds() == null || condition.getGroupIds().size()<=0){
            page.setRecords(new ArrayList<>());
            page.setTotal(0);
            return page;
        }
        Integer timeNum = condition.getTimeNum();
        if (timeNum!=null && timeNum > 0){
            condition.setCreateTime(DateUtil.getDateOfDay(new Date(), -timeNum));
        }
        if(ps == CommonConstant.SEARCH_ALL_NO){
            List<PtStopVO> list = stopService.getList(condition);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        }else {
            page = stopService.getPage(pi, ps, condition);
        }
        return page;
    }

    @Override
    public boolean deleteData(OtherDataCondition cond) {
        if (cond.getMissionIds() == null || cond.getMissionIds().isEmpty()){
            return false;
        }
        LambdaQueryWrapper<RobotSurveyData> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RobotSurveyData::getMissionId, cond.getMissionIds());
        robotSurveyDataService.remove(wrapper);
        List<Point> points = pointService.queryByMissionIds(cond.getMissionIds());
        List<Long> pidList = points.stream().map(Point::getId).collect(Collectors.toList());
        if (pidList.isEmpty()){
            return false;
        }
        LambdaQueryWrapper<PointDataZ> wrapper1 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PointDataZReal> wrapper2 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PointDataXyzh> wrapper3 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PointDataXyzhReal> wrapper4 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PointDataXyz> wrapper5 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<PointDataZl> wrapper6 = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<AlarmInfo> wrapper7 = new LambdaQueryWrapper<>();
        wrapper1.in(PointDataZ::getPid, pidList);
        wrapper2.in(PointDataZReal::getPid, pidList);
        wrapper3.in(PointDataXyzh::getPid, pidList);
        wrapper4.in(PointDataXyzhReal::getPid, pidList);
        wrapper5.in(PointDataXyz::getPid, pidList);
        wrapper6.in(PointDataZl::getPid, pidList);
        wrapper7.in(AlarmInfo::getPtId, pidList);
        if (cond.getDeleteTime()!=null && cond.getDeleteTime().size() == 2){
            wrapper1.ge(PointDataZ::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataZ::getGetTime, cond.getDeleteTime().get(1));
            wrapper2.ge(PointDataZReal::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataZReal::getGetTime, cond.getDeleteTime().get(1));
            wrapper3.ge(PointDataXyzh::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataXyzh::getGetTime, cond.getDeleteTime().get(1));
            wrapper4.ge(PointDataXyzhReal::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataXyzhReal::getGetTime, cond.getDeleteTime().get(1));
            wrapper5.ge(PointDataXyz::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataXyz::getGetTime, cond.getDeleteTime().get(1));
            wrapper6.ge(PointDataZl::getGetTime, cond.getDeleteTime().get(0))
                    .le(PointDataZl::getGetTime, cond.getDeleteTime().get(1));
            wrapper7.ge(AlarmInfo::getAlarmTime, cond.getDeleteTime().get(0))
                    .le(AlarmInfo::getAlarmTime, cond.getDeleteTime().get(1));
        }else {
            wrapper1.le(PointDataZ::getGetTime, new Date());
            wrapper2.le(PointDataZReal::getGetTime, new Date());
            wrapper3.le(PointDataXyzh::getGetTime, new Date());
            wrapper4.le(PointDataXyzhReal::getGetTime, new Date());
            wrapper5.le(PointDataXyz::getGetTime, new Date());
            wrapper6.le(PointDataZl::getGetTime, new Date());
            wrapper7.le(AlarmInfo::getAlarmTime, new Date());
        }
        dataZService.remove(wrapper1);
        dataZRealService.remove(wrapper2);
        dataXyzhService.remove(wrapper3);
        dataXyzhRealService.remove(wrapper4);
        dataXyzService.remove(wrapper5);
        dataZlService.remove(wrapper6);
        alarmInfoService.remove(wrapper7);
        return true;
    }

    private void formatDate(OtherDataCondition cond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (cond.getDeleteTime() != null && cond.getDeleteTime().size()==2){
                List<Date> list = new ArrayList<>();
                String start = format.format(cond.getDeleteTime().get(0));
                list.add(format.parse(start));
                String end = format.format(cond.getDeleteTime().get(1));
                end += " 23:59:59";
                list.add(parse.parse(end));
                cond.setDeleteTime(list);
            }
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void xyExport(List<PointDataXyzVO> list, HttpServletResponse response) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/水平位移XY原始表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            String fileName = list.size() >0 ? list.get(0).getMissionName() + "_原始数据.xlsx" : "水平XY原始数据.xlsx";
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 添加原始数据 所属工程	编组名	测点名	X(m)	Y(m)	测点状态	是否停测	观测时间	备注
            for (PointDataXyzVO data : list) {
                rowIndex ++;
                Row row = sheet.getRow(rowIndex);
                row.getCell(0).setCellValue(data.getProjectName());
                row.getCell(1).setCellValue(data.getGroupName());
                row.getCell(2).setCellValue(data.getName());
                row.getCell(3).setCellValue(data.getX());
                row.getCell(4).setCellValue(data.getY());
                row.getCell(5).setCellValue(data.getStatus());
                row.getCell(6).setCellValue(data.getStop() ? "是" : "否");
                row.getCell(7).setCellValue(data.getGetTime());
                row.getCell(8).setCellValue(data.getNote());
            }
            exportExcel(fileName, response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 导出文件
     */
    private void exportExcel(String fileName ,HttpServletResponse response, Workbook wb) throws IOException {
        try {
            // 设置表格名称
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
    public void zlExport(List<PointDataZlVO> list, HttpServletResponse response) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/支撑轴力原始表.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            String fileName = list.size() >0 ? list.get(0).getMissionName() + "_原始数据.xlsx" : "轴力原始数据.xlsx";
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 所属工程	编组名	测点名	传感器编号 位置 观测值(Hz) 温度(℃) 测点状态 传感器状态	时间 备注
            for (PointDataZlVO data : list) {
                rowIndex ++;
                Row row = sheet.getRow(rowIndex);
                row.getCell(0).setCellValue(data.getProjectName());
                row.getCell(1).setCellValue(data.getGroupName());
                row.getCell(2).setCellValue(data.getName());
                row.getCell(3).setCellValue(data.getJxgCode());
                row.getCell(4).setCellValue(data.getLocation());
                row.getCell(5).setCellValue(data.getF());
                row.getCell(6).setCellValue(data.getTemp());
                row.getCell(7).setCellValue(data.getPointStatus());
                row.getCell(8).setCellValue(data.getSensorStatus());
                row.getCell(9).setCellValue(data.getGetTime());
                row.getCell(10).setCellValue(data.getNote());
            }
            exportExcel(fileName, response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void stopExport(List<PtStopVO> list, HttpServletResponse response) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/测点停测记录表(样例).xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            String fileName = list.size() >0 ? list.get(0).getMissionName() + "_停测记录.xlsx" : "测点停测记录.xlsx";
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 所属工程	监测任务	测点编组	测点名	停测原因	停测时间
            for (PtStopVO data : list) {
                rowIndex ++;
                Row row = sheet.getRow(rowIndex);
                row.getCell(0).setCellValue(data.getProjectName());
                row.getCell(1).setCellValue(data.getMissionName());
                row.getCell(2).setCellValue(data.getGroupName());
                row.getCell(3).setCellValue(data.getPointName());
                row.getCell(4).setCellValue(data.getReason());
                row.getCell(5).setCellValue(data.getCreateTime());
            }
            exportExcel(fileName, response, wb);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void downloadProcess(String fileName, HttpServletResponse response){
        String logPath = FileUtil.BASE_PATH + File.separatorChar + "UserLog";
        Path path = Paths.get(logPath, fileName);
        if (!Files.exists(path)) {
            String[] split = fileName.split("\\.");
            String newFilename = split[0].substring(0, split[0].length() - 2) + "." + split[1];
            path = Paths.get(logPath, newFilename);
        }
        // 判断是否存在文件
        if (Files.exists(path)) {
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1).replace(',', '_'));
            response.addHeader("filesize", String.valueOf(path.toFile().length()));
            try {
                Files.copy(path, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            try {
                response.setContentType("text/plain; charset=UTF-8");
                response.getOutputStream().write("记录文件不存在".getBytes(StandardCharsets.UTF_8));
                response.getOutputStream().flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            log.error("文件不存在");
        }
    }

    @Override
    public ResponseUtil downloadProcessCheck(String fileName) {
        String logPath = FileUtil.BASE_PATH + File.separatorChar + "UserLog";
        Path path = Paths.get(logPath, fileName);
        if (!Files.exists(path)) {
            String[] split = fileName.split("\\.");
            String newFilename = split[0].substring(0, split[0].length() - 2) + "." + split[1];
            path = Paths.get(logPath, newFilename);
        }
        // 判断是否存在文件
        if (Files.exists(path)) {
            return ResponseUtil.success(true);
        } else {
            return ResponseUtil.success(false);
        }
    }
}
