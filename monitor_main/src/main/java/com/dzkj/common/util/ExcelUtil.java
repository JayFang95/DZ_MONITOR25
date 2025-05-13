package com.dzkj.common.util;

import com.dzkj.biz.data.vo.DateCell;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/21
 * @description excel工具类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class ExcelUtil {

    /**
     * 创建workbook对象
     * SXSSFWorkbook: 功能最全
     * XSSFWorkbook 新版本
     * HSSFWorkbook 旧版本
     *
     * @description 创建workbook对象
     * @author jing.fang
     * @date 2022/3/21 13:42
     * @param filePath filePath
     * @return org.apache.poi.ss.usermodel.Workbook
    **/
    public static Workbook createWorkbook(String filePath) throws IOException {
        if(StringUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("文件路径为空");
        }
        if(!FileUtil.isExit(filePath)){
            throw new FileNotFoundException("文件不存在");
        }
        if(filePath.trim().toLowerCase().endsWith("xls")){
            return new HSSFWorkbook(new FileInputStream(filePath));
        }else if (filePath.trim().toLowerCase().endsWith("xlsx")){
            return new XSSFWorkbook(new FileInputStream(filePath));
        }else {
            throw new IllegalArgumentException("文件格式错误");
        }
    }

    /**
     * 根据表名获取表格对象
     *
     * @description 根据表名获取表格对象
     * @author jing.fang
     * @date 2022/3/21 14:00
     * @param wb wb
     * @param sheetName sheetName
     * @return org.apache.poi.ss.usermodel.Sheet
    **/
    public static Sheet getSheet(Workbook wb, String sheetName){
        return wb.getSheet(sheetName);
    }

    /**
     * 根据表格索引获取表格对象
     *
     * @description 根据表格索引获取表格对象
     * @author jing.fang
     * @date 2022/3/21 14:01
     * @param wb wb
     * @param index index
     * @return org.apache.poi.ss.usermodel.Sheet
    **/
    public static Sheet getSheet(Workbook wb, int index){
        return wb.getSheetAt(index);
    }

    /**
     * 获取表格数据
     *
     * @description
     * @author jing.fang
     * @date 2022/3/21 14:03
     * @param sheet sheet
     * @param columnNum columnNum
     * @param startIndex startIndex
     * @return java.util.List<java.lang.Object[]>
    **/
    public static List<Object[]> listFromSheet(Sheet sheet, int columnNum, int startIndex){
        ArrayList<Object[]> list = new ArrayList<>();
        for (int i = startIndex; i <sheet.getLastRowNum() ; i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getPhysicalNumberOfCells() == 0) {
                continue;
            }
            Object[] cells = new Object[columnNum];
            int emptyNum = 0;
            for (int j = 0; j < columnNum ; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    emptyNum ++;
                }else {
                    if (HSSFDateUtil.isCellDateFormatted(cell)){
                        cells[i] = cell.getDateCellValue();
                    }else {
                        cells[i] = cell.getStringCellValue();
                    }
                }
            }
            if (emptyNum == columnNum){
                return list;
            }
            list.add(cells);
        }
        return list;
    }

    /**
     * 获取字符串类型值
     *
     * @description 获取字符串类型值
     * @author jing.fang
     * @date 2022/4/2 11:02
    **/
    public static String getStringValue(Sheet sheet, int rowIndex, int colIndex){
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        if (cell == null){
            return null;
        }
        switch (cell.getCellTypeEnum()){
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue() + "";
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "是":"否";
            default:
                return null;
        }
    }

    /**
     * 获取字符串类型值
     *
     * @description 获取字符串类型值
     * @author jing.fang
     * @date 2022/4/2 11:02
     **/
    public static Double getNumericValue(Sheet sheet, int rowIndex, int colIndex){
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        if (cell == null){
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC){
            return cell.getNumericCellValue();
        }
        return null;
    }

    /**
     * 获取布尔类型数据
    **/
    public static Boolean getBoolValue(Sheet sheet, int rowIndex, int colIndex){
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        if (cell == null){
            return null;
        }
        if (cell.getCellType() == CellType.BOOLEAN){
           return cell.getBooleanCellValue();
        }
        return null;
    }

    /**
     * 获取日期类型值
     *
     * @description 获取日期类型值
     * @author jing.fang
     * @date 2022/4/2 11:02
     **/
    public static DateCell getDateValue(Sheet sheet, int rowIndex, int colIndex){
        DateCell dateCell = new DateCell();
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        if (cell==null){
            return dateCell;
        }else {
            if (HSSFDateUtil.isCellDateFormatted(cell)){
                dateCell.setGetTime(cell.getDateCellValue());
                return dateCell;
            }else {
                dateCell.setInfo("表格第" + (rowIndex+1) + "行数据错误: 观测时间日期格式错误");
                return dateCell;
            }
        }
    }

}
