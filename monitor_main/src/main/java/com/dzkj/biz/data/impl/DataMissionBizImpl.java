package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.IDataMissionBiz;
import com.dzkj.biz.data.vo.MissionDataCondition;
import com.dzkj.biz.data.vo.MissionDataExport;
import com.dzkj.biz.data.vo.PointDataXyzhVO;
import com.dzkj.biz.data.vo.PointDataZVO;
import com.dzkj.common.Angle;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.project.CustomDisplay;
import com.dzkj.service.data.IPointDataXyzhService;
import com.dzkj.service.data.IPointDataZService;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.project.ICustomDisplayService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/17
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class DataMissionBizImpl implements IDataMissionBiz {

    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private ICustomDisplayService displayService;
    @Autowired
    private IPointService pointService;

    @Override
    public ResponseUtil getPage(Integer pi, Integer ps, MissionDataCondition condition) {
        if (condition.getPidList() == null || condition.getPidList().size() == 0){
           return ResponseUtil.success(new Page<>(pi, ps, 0));
        }
        formatDate(condition);
        if (condition.getIsXyz()!=null && condition.getIsXyz()){
            return getXyzPage(pi, ps, condition);
        }else {
            return getCommonPage(pi, ps, condition);
        }
    }

    /**
     * @description 获取非三维位移分页信息
     **/
    private ResponseUtil getCommonPage(Integer pi, Integer ps, MissionDataCondition condition) {
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(condition.getOverLimit()!=null, PointDataZ::getOverLimit, condition.getOverLimit())
                .ge(condition.getStartDate()!=null, PointDataZ::getGetTime, condition.getStartDate())
                .le(condition.getEndDate()!=null, PointDataZ::getGetTime, condition.getEndDate())
                .orderByDesc(PointDataZ::getGetTime).orderByDesc(PointDataZ::getRecycleNum);
        if (condition.getIsFc()!=null && condition.getIsFc()){
            LambdaQueryWrapper<Point> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.in(Point::getPtGroupId, condition.getPidList());
            List<Long> pidList = pointService.list(wrapper1).stream().map(Point::getId).collect(Collectors.toList());
            if (pidList.size() == 0){
                return ResponseUtil.success(new ArrayList<>());
            }
            wrapper.in(PointDataZ::getPid, pidList);
            List<PointDataZVO> listCopy = DzBeanUtils.listCopy(dataZService.list(wrapper), PointDataZVO.class);
            Optional<PointDataZVO> max = listCopy.stream().max(Comparator.comparing(PointDataZVO::getRecycleNum));
            if (max.isPresent()){
                PointDataZVO data = max.get();
                List<PointDataZVO> collect = listCopy.stream().filter(item -> item.getRecycleNum().equals(data.getRecycleNum())).collect(Collectors.toList());
                collect.forEach(item -> {
                    if (!item.getName().contains(".")){
                        item.setName(item.getName() + ".0");
                    }
                });
                return ResponseUtil.success(collect);
            }else {
                return ResponseUtil.success(new ArrayList<>());
            }
        }else {
            wrapper.in(PointDataZ::getPid, condition.getPidList());
            IPage<PointDataZVO> page;
            if(ps == CommonConstant.SEARCH_ALL_NO){
                page = new Page<>(pi, ps);
                List<PointDataZVO> list = DzBeanUtils.listCopy(dataZService.list(wrapper), PointDataZVO.class);
                page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
                page.setRecords(list);
            }else {
                page = DzBeanUtils.pageCopy(dataZService.page(new Page<>(pi, ps), wrapper), PointDataZVO.class);
            }
            return ResponseUtil.success(page);
        }
    }

    /**
     * @description 获取三维位移分页信息
     **/
    private ResponseUtil getXyzPage(Integer pi, Integer ps, MissionDataCondition condition) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        IPage<PointDataXyzhVO> page;
        if (condition.getIsMultiCycle() != null && !condition.getIsMultiCycle()){
            List<PointDataXyzhVO> list;
            if (condition.getCycleNum() != null && condition.getCycleNum() > 0){
                wrapper.in(PointDataXyzh::getPid, condition.getPidList())
                        .eq(PointDataXyzh::getRecycleNum, condition.getCycleNum())
                        .orderByDesc(PointDataXyzh::getGetTime);
                list = DzBeanUtils.listCopy(dataXyzhService.list(wrapper), PointDataXyzhVO.class);
            } else {
                list = DzBeanUtils.listCopy(dataXyzhService.queryLatestData(condition.getPidList()), PointDataXyzhVO.class);
            }
            page = new Page<>(pi, ps);
            page.setTotal(list.size());
            page.setRecords(list);
        } else {
            wrapper.in(PointDataXyzh::getPid, condition.getPidList())
                    .eq(condition.getOverLimit()!=null, PointDataXyzh::getOverLimit, condition.getOverLimit())
                    .ge(condition.getStartDate()!=null, PointDataXyzh::getGetTime, condition.getStartDate())
                    .le(condition.getEndDate()!=null, PointDataXyzh::getGetTime, condition.getEndDate())
                    .orderByDesc(PointDataXyzh::getGetTime).orderByDesc(PointDataXyzh::getRecycleNum);
            if(ps == CommonConstant.SEARCH_ALL_NO){
                page = new Page<>(pi, ps);
                List<PointDataXyzhVO> list = DzBeanUtils.listCopy(dataXyzhService.list(wrapper), PointDataXyzhVO.class);
                page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
                page.setRecords(list);
            }else {
                page = DzBeanUtils.pageCopy(dataXyzhService.page(new Page<>(pi, ps), wrapper), PointDataXyzhVO.class);
            }
        }
        if (page.getTotal() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            List<PointDataXyzhVO> records = page.getRecords();
            for (PointDataXyzhVO record : records) {
                Date getTime = record.getGetTime();
                if (getTime != null) {
                    String str = format.format(getTime);
                    String str2 = format2.format(getTime);
                    record.setGetTimeStr(str + "\r\n" + str2);
                }
            }
            page.setRecords(records);
        }
        return ResponseUtil.success(page);
    }

    /**
     * @description 时间参数格式化
     **/
    private void formatDate(MissionDataCondition condition) {
        if (condition.getStartDate() != null){
            condition.setStartDate(condition.getStartDate());
        }
        if (condition.getEndDate() != null){
            condition.setEndDate(condition.getEndDate());
        }
    }

    @Override
    public void exportList(Long missionId, MissionDataExport dataExport, HttpServletResponse response) {
        LambdaQueryWrapper<CustomDisplay> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomDisplay::getMissionId, missionId).orderByAsc(CustomDisplay::getSeq);
        List<CustomDisplay> displays = displayService.list(wrapper);
        if (displays.size() == 0){
            return;
        }
        if(dataExport.getIsXyzh()!=null && dataExport.getIsXyzh()){
            if(dataExport.getPointDataXyzhList()==null || dataExport.getPointDataXyzhList().size()==0){
               return;
            }
            exportXyzList( dataExport, displays, response);
        }else {
            if(dataExport.getPointDataZList()==null || dataExport.getPointDataZList().size()==0){
                return;
            }
            exportCommonList(dataExport, displays, response);
        }
    }

    /**
     * 导出常规类型监测任务数据
     */
    private void exportCommonList(MissionDataExport dataExport, List<CustomDisplay> displays, HttpServletResponse response) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        int rowNum = dataExport.getPointDataZList().size();
        int colNum = (int) displays.stream().filter(CustomDisplay::getEnableDisplay).count();
        // 边框样式
        CellStyle borderStyle = createBorderStyle(wb);
        for (int i = 0; i <= rowNum + 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j <colNum ; j++) {
                row.createCell(j).setCellStyle(borderStyle);
            }
        }
        // 设置标题
        setTitle(wb, sheet, dataExport.getTitle(), colNum);
        // 设置列头
        setHeader(wb, sheet, colNum, displays);
        // 填充数据
        int colIndex = addDataCommon(wb, sheet, displays, dataExport.getPointDataZList());
        //图表列合并
        CellRangeAddress region = new CellRangeAddress(rowNum + 3, rowNum + 3, 0, colNum - 1);
        sheet.addMergedRegion(region);
        // 图表信息
        if (dataExport.getIsSpFc()!=null && dataExport.getIsSpFc()){
            addEChartPic(wb, sheet, dataExport.getImgDataUrl(), rowNum + 3, rowNum + 36 , 0 , Math.min(colNum -1, 4));
        }else {
            addEChartPic(wb, sheet, dataExport.getImgDataUrl(), rowNum + 3, rowNum + 24 , 0 , Math.min(colNum -1, 10));
        }
        //设置列宽
        for (int i = 0; i < colNum; i++)
        {
            if (colIndex != i){
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 10 /10 );
            }
        }
        exportExcel(dataExport.getTitle() + ".xlsx", response, wb);
    }

    /**
     * 填充数据
     */
    private int addDataCommon(Workbook wb, Sheet sheet, List<CustomDisplay> displays, List<PointDataZVO> list) {
        int index = -1;
        int overColNum = -1;
        CellStyle textStyle = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setFontName("宋体");
        textStyle.setFont(font1);
        textStyle.setAlignment(HorizontalAlignment.CENTER);
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);
        textStyle.setBorderTop(BorderStyle.THIN);
        CellStyle timeStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss");
        timeStyle.setDataFormat(dateFormat);
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        timeStyle.setFont(font);
        timeStyle.setAlignment(HorizontalAlignment.CENTER);
        timeStyle.setBorderBottom(BorderStyle.THIN);
        timeStyle.setBorderLeft(BorderStyle.THIN);
        timeStyle.setBorderRight(BorderStyle.THIN);
        timeStyle.setBorderTop(BorderStyle.THIN);
        for (CustomDisplay display : displays) {
            final Integer conversion = display.getConversion() == 0 ? 1 : display.getConversion();
            if (display.getEnableDisplay()){
                CellStyle numStyle = wb.createCellStyle();
                numStyle.setAlignment(HorizontalAlignment.RIGHT);
                numStyle.setFont(font);
                numStyle.setDataFormat(wb.createDataFormat().getFormat(getFormatStr(display.getDecimalNum())));
                numStyle.setBorderBottom(BorderStyle.THIN);
                numStyle.setBorderLeft(BorderStyle.THIN);
                numStyle.setBorderRight(BorderStyle.THIN);
                numStyle.setBorderTop(BorderStyle.THIN);
                index++;
                List<String> txtList;
                List<Double> numList;
                List<Date> timeList;
                switch (display.getTagName()){
                    case "PtName":
                        txtList = list.stream().map(PointDataZVO::getName).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "RecycleNum":
                        txtList = list.stream().map(item -> item.getRecycleNum()+"").collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "Z0":
                        numList = list.stream().map(item -> item.getZ0()==null ? null : item.getZ0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "ZPrev":
                        numList = list.stream().map(item -> item.getZPrev()==null ? null : item.getZPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "Z":
                        numList = list.stream().map(item -> item.getZ()==null ? null : item.getZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltZ":
                        numList = list.stream().map(item -> item.getDeltZ()==null ? null : item.getDeltZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltZ":
                        numList = list.stream().map(item -> item.getVDeltZ()==null ? null : item.getVDeltZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalZPrev":
                        numList = list.stream().map(item -> item.getTotalZPrev()==null ? null : item.getTotalZPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalZ":
                        numList = list.stream().map(item -> item.getTotalZ()==null ? null : item.getTotalZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "GetTime0":
                        timeList = list.stream().map(PointDataZVO::getGetTime0).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "GetTimePrev":
                        timeList = list.stream().map(PointDataZVO::getPrevGetTime).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "GetTime":
                        timeList = list.stream().map(PointDataZVO::getGetTime).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "DeltTime":
                        CellStyle numStyle2 = wb.createCellStyle();
                        numStyle2.setAlignment(HorizontalAlignment.CENTER);
                        numStyle2.setFont(font);
                        numStyle2.setDataFormat(wb.createDataFormat().getFormat(getFormatStr(display.getDecimalNum())));
                        numStyle2.setBorderBottom(BorderStyle.THIN);
                        numStyle2.setBorderLeft(BorderStyle.THIN);
                        numStyle2.setBorderRight(BorderStyle.THIN);
                        numStyle2.setBorderTop(BorderStyle.THIN);
                        numList = list.stream().map(item -> item.getDeltTime()==null ? null : item.getDeltTime() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle2);
                        break;
                    case "OverLimit":
                        txtList = list.stream().map(item -> item.getOverLimit() ? "是" : "否").collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "OverLimitInfo":
                        overColNum = index;
                        txtList = list.stream().map(PointDataZVO::getOverLimitInfo).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "Status":
                        txtList = list.stream().map(PointDataZVO::getStatus).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "Note":
                        txtList = list.stream().map(PointDataZVO::getNote).collect(Collectors.toList());
                        CellStyle textStyle2 = wb.createCellStyle();
                        textStyle2.setAlignment(HorizontalAlignment.LEFT);
                        textStyle2.setBorderBottom(BorderStyle.THIN);
                        textStyle2.setBorderLeft(BorderStyle.THIN);
                        textStyle2.setBorderRight(BorderStyle.THIN);
                        textStyle2.setBorderTop(BorderStyle.THIN);
                        setTxtColumn(index, txtList, sheet, textStyle2);
                        break;
                    default:
                }
            }
        }
        return overColNum;
    }

    /**
     * 导出三维位移监测数据
     */
    private void exportXyzList(MissionDataExport dataExport, List<CustomDisplay> displays, HttpServletResponse response) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        int rowNum = dataExport.getPointDataXyzhList().size();
        int colNum = (int) displays.stream().filter(CustomDisplay::getEnableDisplay).count();
        // 边框样式
        CellStyle borderStyle = createBorderStyle(wb);
        for (int i = 0; i <= rowNum + 3; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j <colNum ; j++) {
                row.createCell(j).setCellStyle(borderStyle);
            }
        }
        // 设置标题
        setTitle(wb, sheet, dataExport.getTitle(), colNum);
        // 设置列头
        setHeader(wb, sheet, colNum, displays);
        // 填充数据
        int colIndex = addData(wb, sheet, displays, dataExport.getPointDataXyzhList());
        //图表列合并
        CellRangeAddress region = new CellRangeAddress(rowNum + 2, rowNum + 2, 0, colNum - 1);
        sheet.addMergedRegion(region);
        // 图表信息
        addEChartPic(wb, sheet, dataExport.getImgDataUrl(), rowNum + 3, rowNum + 24 , 0 , Math.min(colNum -1, 10));
        //设置列宽
        for (int i = 0; i < colNum; i++)
        {
            if (colIndex != i){
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 10 /10 );
            }
        }
        exportExcel(dataExport.getTitle() + ".xlsx", response, wb);
    }

    /**
     * 导出表格
     */
    private void exportExcel(String fileName, HttpServletResponse response, Workbook wb) {
        try {
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

    /**
     * 添加EChart图片
     */
    private static final String BASE_PATH = "File";
    private void addEChartPic(Workbook wb, Sheet sheet, String dataUrl, int startRow, int endRow, int startCol, int endCol) {
        //拆分base64编码后部分
        String[] imgUrlArr = dataUrl.split("base64,");
        byte[] buffer = Base64.decode(imgUrlArr[1]);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String picPath = BASE_PATH + File.separatorChar + "temp-chart" + File.separatorChar +  format.format(new Date());
        String picName = UUID.randomUUID() +".png";
        File file = new File(picPath);//图片文件
        if (!file.exists()) {
            if (!file.mkdirs()) {
                // 如果创建失败
                throw new RuntimeException("创建文件夹失败");
            }
        }
        FileOutputStream out = null;
        ByteArrayOutputStream outStream = null;
        try {
            //生成图片
            out = new FileOutputStream(new File(file, picName));
            out.write(buffer);
            outStream = new ByteArrayOutputStream(); // 将图片写入流中
            BufferedImage bufferImg = ImageIO.read(new File(file, picName));
            ImageIO.write(bufferImg, "PNG", outStream);
            Drawing<?> patriarch = sheet.createDrawingPatriarch();
            ClientAnchor anchor = new XSSFClientAnchor(0 ,0 ,0 , 0, startCol , startRow, endCol, endRow);
            patriarch.createPicture(anchor, wb.addPicture(outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 填充数据
     */
    private int addData(Workbook wb, Sheet sheet, List<CustomDisplay> displays, List<PointDataXyzhVO> list) {
        int index = -1;
        int overColNum = -1;
        CellStyle textStyle = wb.createCellStyle();
        Font font1 = wb.createFont();
        font1.setFontName("宋体");
        textStyle.setFont(font1);
        textStyle.setAlignment(HorizontalAlignment.CENTER);
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);
        textStyle.setBorderTop(BorderStyle.THIN);
        CellStyle timeStyle = wb.createCellStyle();
        CreationHelper createHelper = wb.getCreationHelper();
        short dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd hh:mm:ss");
        timeStyle.setDataFormat(dateFormat);
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        timeStyle.setFont(font);
        timeStyle.setAlignment(HorizontalAlignment.CENTER);
        timeStyle.setBorderBottom(BorderStyle.THIN);
        timeStyle.setBorderLeft(BorderStyle.THIN);
        timeStyle.setBorderRight(BorderStyle.THIN);
        timeStyle.setBorderTop(BorderStyle.THIN);
        CellStyle textStyle2 = wb.createCellStyle();
        textStyle2.setFont(font);
        textStyle2.setAlignment(HorizontalAlignment.RIGHT);
        textStyle2.setBorderBottom(BorderStyle.THIN);
        textStyle2.setBorderLeft(BorderStyle.THIN);
        textStyle2.setBorderRight(BorderStyle.THIN);
        textStyle2.setBorderTop(BorderStyle.THIN);
        for (CustomDisplay display : displays) {
            final Integer conversion = display.getConversion() == 0 ? 1 : display.getConversion();
            if (display.getEnableDisplay()){
                CellStyle numStyle = wb.createCellStyle();
                numStyle.setAlignment(HorizontalAlignment.RIGHT);
                numStyle.setFont(font);
                numStyle.setDataFormat(wb.createDataFormat().getFormat(getFormatStr(display.getDecimalNum())));
                numStyle.setBorderBottom(BorderStyle.THIN);
                numStyle.setBorderLeft(BorderStyle.THIN);
                numStyle.setBorderRight(BorderStyle.THIN);
                numStyle.setBorderTop(BorderStyle.THIN);
                index++;
                List<String> txtList;
                List<Double> numList;
                List<Date> timeList;
                switch (display.getTagName()){
                    case "PtName":
                        txtList = list.stream().map(PointDataXyzhVO::getName).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "RecycleNum":
                        txtList = list.stream().map(item -> item.getRecycleNum()+"").collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "X0":
                        numList = list.stream().map(item -> item.getX0()==null ? null : item.getX0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "Y0":
                        numList = list.stream().map(item -> item.getY0()==null ? null : item.getY0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "Z0":
                        numList = list.stream().map(item -> item.getZ0()==null ? null : item.getZ0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "P0":
                        numList = list.stream().map(item -> item.getP0()==null ? null : item.getP0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "S0":
                        numList = list.stream().map(item -> item.getP0()==null ? null : item.getS0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "T0":
                        numList = list.stream().map(item -> item.getT0()==null ? null : item.getT0() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "XPrev":
                        numList = list.stream().map(item -> item.getXPrev()==null ? null : item.getXPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "YPrev":
                        numList = list.stream().map(item -> item.getYPrev()==null ? null :item.getYPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "ZPrev":
                        numList = list.stream().map(item -> item.getZPrev()==null ? null : item.getZPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "PPrev":
                        numList = list.stream().map(item -> item.getPPrev()==null ? null : item.getPPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "SPrev":
                        numList = list.stream().map(item -> item.getSPrev()==null ? null : item.getSPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TPrev":
                        numList = list.stream().map(item -> item.getTPrev()==null ? null : item.getTPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "X":
                        numList = list.stream().map(item -> item.getX()==null ? null : item.getX() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "Y":
                        numList = list.stream().map(item -> item.getY()==null ? null : item.getY() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "Z":
                        numList = list.stream().map(item -> item.getZ()==null ? null : item.getZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "P":
                        numList = list.stream().map(item -> item.getP()==null ? null : item.getP() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "S":
                        numList = list.stream().map(item -> item.getS()==null ? null : item.getS() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "T":
                        numList = list.stream().map(item -> item.getT()==null ? null : item.getT() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltX":
                        numList = list.stream().map(item -> item.getDeltX()==null ? null : item.getDeltX() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltY":
                        numList = list.stream().map(item -> item.getDeltY()==null ? null : item.getDeltY() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltZ":
                        numList = list.stream().map(item -> item.getDeltZ()==null ? null : item.getDeltZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltP":
                        numList = list.stream().map(item -> item.getDeltP()==null ? null : item.getDeltP() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltS":
                        numList = list.stream().map(item -> item.getDeltS()==null ? null : item.getDeltS() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "DeltT":
                        numList = list.stream().map(item -> item.getDeltT()==null ? null : item.getDeltT() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltX":
                        numList = list.stream().map(item -> item.getVDeltX()==null ? null : item.getVDeltX() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltY":
                        numList = list.stream().map(item -> item.getVDeltY()==null ? null : item.getVDeltY() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltZ":
                        numList = list.stream().map(item -> item.getVDeltZ()==null ? null : item.getDeltZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltP":
                        numList = list.stream().map(item -> item.getVDeltP()==null ? null : item.getVDeltP() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltS":
                        numList = list.stream().map(item -> item.getVDeltS()==null ? null : item.getVDeltS() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "VDeltT":
                        numList = list.stream().map(item -> item.getVDeltT()==null ? null : item.getVDeltT() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalXPrev":
                        numList = list.stream().map(item -> item.getTotalXPrev()==null ? null : item.getTotalXPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalYPrev":
                        numList = list.stream().map(item -> item.getTotalYPrev()==null ? null : item.getTotalYPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalZPrev":
                        numList = list.stream().map(item -> item.getTotalZPrev()==null ? null : item.getTotalZPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalPPrev":
                        numList = list.stream().map(item -> item.getTotalPPrev()==null ? null : item.getTotalPPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalSPrev":
                        numList = list.stream().map(item -> item.getTotalSPrev()==null ? null : item.getTotalSPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalTPrev":
                        numList = list.stream().map(item -> item.getTotalTPrev()==null ? null : item.getTotalTPrev() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalX":
                        numList = list.stream().map(item -> item.getTotalX()==null ? null : item.getTotalX() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalY":
                        numList = list.stream().map(item -> item.getTotalY()==null ? null : item.getTotalY() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalZ":
                        numList = list.stream().map(item -> item.getTotalZ()==null ? null : item.getTotalZ() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalP":
                        numList = list.stream().map(item -> item.getTotalP()==null ? null : item.getTotalP() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalS":
                        numList = list.stream().map(item -> item.getTotalS()==null ? null : item.getTotalS() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "TotalT":
                        numList = list.stream().map(item -> item.getTotalT()==null ? null : item.getTotalT() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "HA":
                        numList = list.stream().map(item -> item.getHa()==null ? null : item.getHa() * conversion).collect(Collectors.toList());
                        txtList = transFormToDmmss(numList, display.getDecimalNum());
                        setTxtColumn(index, txtList, sheet, textStyle2);
                        break;
                    case "VA":
                        numList = list.stream().map(item -> item.getVa()==null ? null : item.getVa() * conversion).collect(Collectors.toList());
                        txtList = transFormToDmmss(numList, display.getDecimalNum());
                        setTxtColumn(index, txtList, sheet, textStyle2);
                        break;
                    case "SD":
                        numList = list.stream().map(item -> item.getSd()==null ? null : item.getSd() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle);
                        break;
                    case "GetTime0":
                        timeList = list.stream().map(PointDataXyzhVO::getGetTime0).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "GetTimePrev":
                        timeList = list.stream().map(PointDataXyzhVO::getGetTimePrev).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "GetTime":
                        timeList = list.stream().map(PointDataXyzhVO::getGetTime).collect(Collectors.toList());
                        setTimeColumn(index, timeList, sheet, timeStyle);
                        break;
                    case "DeltTime":
                        CellStyle numStyle2 = wb.createCellStyle();
                        numStyle2.setAlignment(HorizontalAlignment.CENTER);
                        numStyle2.setFont(font);
                        numStyle2.setDataFormat(wb.createDataFormat().getFormat(getFormatStr(display.getDecimalNum())));
                        numStyle2.setBorderBottom(BorderStyle.THIN);
                        numStyle2.setBorderLeft(BorderStyle.THIN);
                        numStyle2.setBorderRight(BorderStyle.THIN);
                        numStyle2.setBorderTop(BorderStyle.THIN);
                        numList = list.stream().map(item -> item.getDeltTime()==null ? null : item.getDeltTime() * conversion).collect(Collectors.toList());
                        setNumColumn(index, numList, sheet, numStyle2);
                        break;
                    case "OverLimit":
                        txtList = list.stream().map(item -> item.getOverLimit() ? "是" : "否").collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "OverLimitInfo":
                        overColNum = index;
                        txtList = list.stream().map(PointDataXyzhVO::getOverLimitInfo).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "Status":
                        txtList = list.stream().map(PointDataXyzhVO::getStatus).collect(Collectors.toList());
                        setTxtColumn(index, txtList, sheet, textStyle);
                        break;
                    case "Note":
                        txtList = list.stream().map(PointDataXyzhVO::getNote).collect(Collectors.toList());
                        CellStyle textStyle3 = wb.createCellStyle();
                        textStyle3.setAlignment(HorizontalAlignment.LEFT);
                        textStyle3.setBorderBottom(BorderStyle.THIN);
                        textStyle3.setBorderLeft(BorderStyle.THIN);
                        textStyle3.setBorderRight(BorderStyle.THIN);
                        textStyle3.setBorderTop(BorderStyle.THIN);
                        setTxtColumn(index, txtList, sheet, textStyle3);
                        break;
                    default:
                }
            }
        }
        return overColNum;
    }

    /**
     * 角度转度分秒值
     */
    private List<String> transFormToDmmss(List<Double> numList, Integer decimalNum) {
        List<String> list = new ArrayList<>();
        for (Double value : numList) {
            Angle angle = new Angle(value, 1, false);
            list.add(angle.showDmmss(decimalNum));
        }
        return list;
    }

    /**
     * 设置文字列值
     */
    private void setTimeColumn(int index, List<Date> list, Sheet sheet, CellStyle style) {
        int rowNum = 1;
        for (Date data : list) {
            rowNum++;
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(index);
            cell.setCellValue(data);
            cell.setCellStyle(style);
        }
    }

    /**
     * 设置数值列值
     */
    private void setNumColumn(int index, List<Double> list, Sheet sheet, CellStyle style) {
        int rowNum = 1;
        for (Double data : list) {
            rowNum++;
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(index);
            cell.setCellValue(data == null ? 0.0 : data);
            cell.setCellStyle(style);
        }
    }

    /**
     * 设置文字列值
     */
    private void setTxtColumn(int index, List<String> list, Sheet sheet, CellStyle style) {
        int rowNum = 1;
        for (String data : list) {
            rowNum++;
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(index);
            cell.setCellValue(data);
            cell.setCellStyle(style);
        }
    }

    /**
     * 设置列头，并返回每一列的样式
     */
    private void setHeader(Workbook wb, Sheet sheet, int colNum, List<CustomDisplay> displays) {
        int index = -1;
        Row row = sheet.getRow(1);
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        for (CustomDisplay display : displays) {
            if (display.getEnableDisplay()){
                index++;
                Cell cell = row.getCell(index);
                cell.setCellValue(display.getDisplayName());
                cell.setCellStyle(style);
            }
        }
    }

    /**
     * 获取格式化字符串
     */
    private String getFormatStr(Integer decimalNum) {
        StringBuilder sb = new StringBuilder("0");
        if (decimalNum!=null && decimalNum > 0){
            sb.append(".");
            for (int i = 0; i < decimalNum; i++) {
                sb.append("0");
            }
        }
        return sb.toString();
    }

    /**
     * 设置标题
     */
    private void setTitle(Workbook wb, Sheet sheet, String title, int colNum) {
        Row row = sheet.getRow(0);
        //标题 合并单元格并居中
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, colNum - 1);
        sheet.addMergedRegion(region);
        // 居中加粗
        CellStyle titleStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setFont(font);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderTop(BorderStyle.THIN);
        Cell cell = row.getCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(titleStyle);
    }

    /**
     * 单元格边框
     */
    private CellStyle createBorderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

}
