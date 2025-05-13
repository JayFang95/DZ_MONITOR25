package com.dzkj.dataSwap.biz.impl;

import com.dzkj.biz.data.vo.DateCell;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.common.Angle;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ExcelUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushPointBiz;
import com.dzkj.dataSwap.enums.*;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.dataSwap.vo.PushUploadParam;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PushPoint;
import com.dzkj.entity.param_set.Point;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPushPointService;
import com.dzkj.service.param_set.IPointService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6 14:18
 * @description 推送任务业务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushPointBizImpl implements IPushPointBiz {

    @Autowired
    private IPushPointService pushPointService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;

    @Override
    public List<PushPointVO> queryList(Long taskId) {
        return DzBeanUtils.listCopy(pushPointService.queryList(taskId), PushPointVO.class);
    }

    @Override
    public ResponseUtil add(PushPointVO data) {
        if (pushPointService.findSameCodeNotInTask(data)){
            return ResponseUtil.failure(500, "设备编号["+data.getCode()+"]已存在");
        }
        if (pushPointService.findCodeAndDeviceCategoryNotSame(data)){
            return ResponseUtil.failure(500, "设备编号设备类型不匹配");
        }
        boolean b = pushPointService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil edit(PushPointVO data) {
        if (pushPointService.findSameCodeNotInTask(data)){
            return ResponseUtil.failure(500, "设备编号已存在");
        }
        if (pushPointService.findCodeAndDeviceCategoryNotSame(data)){
            return ResponseUtil.failure(500, "设备编号设备类型不匹配");
        }
        boolean b = pushPointService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = pushPointService.delete(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "删除失败");
    }

    @Override
    public List<PointVO> listPoint(Long missionId, Long pushTaskId, Long pointId) {
        return DzBeanUtils.listCopy(pointService.listPoint(missionId, pushTaskId, pointId), PointVO.class);
    }

    @Override
    public ResponseUtil uploadBatch(PushUploadParam uploadParam, MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null){
            return ResponseUtil.failure(500, "文件不存在");
        }
        if (!(filename.endsWith(".xlsx") || filename.endsWith(".xls"))){
            return ResponseUtil.failure(500, "上传文件格式错误:请选择excel表格");
        }
        try {
            InputStream inputStream = file.getInputStream();
            Workbook wb;
            if (filename.endsWith(".xlsx")){
                wb = new XSSFWorkbook(inputStream);
            }else {
                wb = new HSSFWorkbook(inputStream);
            }
            Sheet sheet = wb.getSheetAt(0);
            // 读取表格数据
            // 点名 设备编号 设备类型 目标大分类 目标小分类 铁路类型 业务类型 铁路里程 测点高度 功能类型 报警数据处理方法
            // 东坐标(m)	北坐标(m) 高程(m)	水平角(D.mmss) 竖直角(D.mmss)	斜距(m)	采集时间
            List<PushPoint> pushPoints = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum() ; i++) {
                if (sheet.getRow(i) == null){
                    break;
                }
                boolean empty = true;
                for (Cell cell : sheet.getRow(i)) {
                    if (cell.getCellType() != CellType.BLANK) {
                        empty = false;
                    }
                }
                if (empty){
                    break;
                }
                PushPoint pushPoint = new PushPoint();
                Double height = ExcelUtil.getNumericValue(sheet, i, 8);
                Double y = ExcelUtil.getNumericValue(sheet, i, 11);
                Double x = ExcelUtil.getNumericValue(sheet, i, 12);
                Double z = ExcelUtil.getNumericValue(sheet, i, 13);
                Double ha = ExcelUtil.getNumericValue(sheet, i, 14);
                Double va = ExcelUtil.getNumericValue(sheet, i, 15);
                Double sd = ExcelUtil.getNumericValue(sheet, i, 16);
                DateCell getTime = ExcelUtil.getDateValue(sheet, i, 17);
                Double pid = ExcelUtil.getNumericValue(sheet, i, 18);
                pushPoint.setPushTaskId(uploadParam.getPushTaskId());
                pushPoint.setProjectCode(uploadParam.getProjectCode());
                pushPoint.setPointId(pid == null ? 0 : pid.longValue());
                pushPoint.setPtCode(ExcelUtil.getStringValue(sheet, i, 0));
                pushPoint.setCode(ExcelUtil.getStringValue(sheet, i, 1));
                pushPoint.setDeviceCatagory(DeviceCategoryEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 2)));
                pushPoint.setMainCatagory(MainCategoryEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 3)));
                pushPoint.setSubCatagory(SubCategoryEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 4)));
                pushPoint.setRailwayType(RailwayTypeEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 5)));
                pushPoint.setFuncAttr(FuncAttrEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 6)));
                pushPoint.setKiloMark(ExcelUtil.getStringValue(sheet, i, 7));
                pushPoint.setHeight(height == null ? 0 : height);
                pushPoint.setFuncType(FuncTypeEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 9)));
                pushPoint.setAlarmHandler(PtAlarmHandleEnum.getCodeByName(ExcelUtil.getStringValue(sheet, i, 10)));
                pushPoint.setY(y == null ? 0 : new BigDecimal(y).setScale(4, RoundingMode.HALF_UP).doubleValue());
                pushPoint.setX(x == null ? 0 : new BigDecimal(x).setScale(4, RoundingMode.HALF_UP).doubleValue());
                pushPoint.setZ(z == null ? 0 : new BigDecimal(z).setScale(4, RoundingMode.HALF_UP).doubleValue());
                pushPoint.setHa(ha == null ? "0" : new BigDecimal(ha).setScale(4, RoundingMode.HALF_UP).doubleValue()  + "");
                pushPoint.setVa(va == null ? "0" : new BigDecimal(va).setScale(4, RoundingMode.HALF_UP).doubleValue()  + "");
                pushPoint.setDistance(sd == null ? 0 : new BigDecimal(sd).setScale(4, RoundingMode.HALF_UP).doubleValue());
                pushPoint.setDataTime(getTime.getGetTime() != null ? getTime.getGetTime() : new Date());
                pushPoints.add(pushPoint);
            }
            if (pushPoints.size() == 0){
                return ResponseUtil.failure(500, "导入表格中无有效数据行");
            }
            boolean b = pushPointService.saveBatch(pushPoints);
            return b ? ResponseUtil.success() : ResponseUtil.failure("批量添加推送点失败");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.failure(500, "读取excel数据异常");
        }
    }

    @Override
    public void download(Long pushTaskId, Long missionId, HttpServletResponse response) {
        //查询所有未添加的推送点信息
        List<Point> points = pointService.listPoint(missionId, pushTaskId, 0L);
        List<PointDataXyzh> dataXyzhList;
        if (points.size() == 0){
            dataXyzhList = new ArrayList<>();
        } else {
            List<Long> pidList = points.stream().map(Point::getId).collect(Collectors.toList());
            //获取所有测点的第一期测量数据
            dataXyzhList = dataXyzhService.getEarliestRecycleData(pidList);
        }
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            String locationPattern = "static/推送点信息表(样例).xlsx";
            Resource[] resources = resolver.getResources(locationPattern);
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            Sheet sheet = wb.getSheetAt(0);

            // 点名 设备编号 设备类型 目标大分类 目标小分类 铁路类型 业务类型 铁路里程 测点高度 功能类型 报警数据处理方法
            // 东坐标(m)	北坐标(m) 高程(m)	水平角(D.mmss) 竖直角(D.mmss)	斜距(m)	采集时间
            int rowIndex = 0;
            for (Point point : points) {
                rowIndex++;
                Row rowPt = sheet.getRow(rowIndex);
                Cell name = rowPt.getCell(0);
                name.setCellValue(point.getName());
                //设置推送点下拉列默认值
                Cell deviceCategory = rowPt.getCell(2);
                deviceCategory.setCellValue("全站仪");
                Cell mainCategory = rowPt.getCell(3);
                mainCategory.setCellValue("线上目标");
                Cell subCategory = rowPt.getCell(4);
                subCategory.setCellValue("轨道");
                Cell railwayType = rowPt.getCell(5);
                railwayType.setCellValue("普铁");
                Cell funcAttr = rowPt.getCell(6);
                funcAttr.setCellValue("位移测量");
                Cell height = rowPt.getCell(8);
                height.setCellValue(0);
                Cell funcType = rowPt.getCell(9);
                funcType.setCellValue("监测点".equals(point.getType()) ? "监测点" : ("控制点".equals(point.getType()) ? "基准点" : "测站点"));
                Cell alarmType = rowPt.getCell(10);
                alarmType.setCellValue("不上传");
                Cell pid = rowPt.getCell(18);
                pid.setCellValue(point.getId());
                //设置观测数据
                Optional<PointDataXyzh> optional = dataXyzhList.stream().filter(data -> data.getPid().equals(point.getId())).findAny();
                if (optional.isPresent()) {
                    PointDataXyzh find = optional.get();
                    Cell y = rowPt.getCell(11);
                    y.setCellValue(find.getY());
                    Cell x = rowPt.getCell(12);
                    x.setCellValue(find.getX());
                    Cell z = rowPt.getCell(13);
                    z.setCellValue(find.getZ());
                    Cell ha = rowPt.getCell(14);
                    ha.setCellValue(Double.parseDouble(new Angle(find.getHa(), 1, false).showDms(4)));
                    Cell va = rowPt.getCell(15);
                    va.setCellValue(Double.parseDouble(new Angle(find.getVa(), 1, false).showDms(4)));
                    Cell sd = rowPt.getCell(16);
                    sd.setCellValue(find.getSd());
                    Cell getTime = rowPt.getCell(17);
                    getTime.setCellValue(find.getGetTime());
                } else {
                    Cell getTime = rowPt.getCell(17);
                    getTime.setCellValue(new Date());
                }
            }
            // 写入导出
            exportExcel( response, wb);
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
    private void exportExcel(HttpServletResponse response, Workbook wb) throws IOException {
        try {
            // 设置表格名称
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String formatDate = format.format(new Date());
            String fileName = "推送点信息表_" + formatDate + ".xlsx";
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

}
