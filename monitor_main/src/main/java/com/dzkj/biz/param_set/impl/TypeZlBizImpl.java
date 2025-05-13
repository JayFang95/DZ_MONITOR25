package com.dzkj.biz.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.param_set.ITypeZlBiz;
import com.dzkj.biz.param_set.vo.TypeZlVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.param_set.TypeZl;
import com.dzkj.entity.project.ProMission;
import com.dzkj.service.param_set.ITypeZlService;
import com.dzkj.service.project.IProMissionService;
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

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 支撑类型业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class TypeZlBizImpl implements ITypeZlBiz {

    @Autowired
    private ITypeZlService typeZlService;
    @Autowired
    private IProMissionService missionService;

    @Override
    public List<TypeZlVO> getList(Long missionId) {
        LambdaQueryWrapper<TypeZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeZl::getMissionId, missionId).orderByDesc(TypeZl::getCreateTime);
        return DzBeanUtils.listCopy(typeZlService.list(wrapper), TypeZlVO.class);
    }

    @Override
    public ResponseUtil add(TypeZlVO data) {
        if (typeZlService.checkName(data)){
            return ResponseUtil.failure(500,"类型名称工程内重复");
        }
        boolean b = typeZlService.save(DzBeanUtils.propertiesCopy(data, TypeZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(TypeZlVO data) {
        if (typeZlService.checkName(data)){
            return ResponseUtil.failure(500,"类型名称工程内重复");
        }
        boolean b = typeZlService.updateById(DzBeanUtils.propertiesCopy(data, TypeZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = typeZlService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public void exportData(Long missionId, HttpServletResponse response) {
        try {
            List<TypeZlVO> list = getList(missionId);
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/支撑类型信息(样例).xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 填充数据
            for (TypeZlVO data : list) {
                rowIndex++;
                Row row = sheet.getRow(rowIndex);
                Cell cell0 = row.getCell(0);
                cell0.setCellValue(data.getName());
                Cell cell1 = row.getCell(1);
                cell1.setCellValue(data.getA());
                Cell cell2 = row.getCell(2);
                cell2.setCellValue(data.getEs());
                Cell cell3 = row.getCell(3);
                cell3.setCellValue(data.getAcecAses());
                Cell cell4 = row.getCell(4);
                cell4.setCellValue(data.getR());
                Cell cell5 = row.getCell(5);
                cell5.setCellValue(data.getTh());
                Cell cell6 = row.getCell(6);
                cell6.setCellValue(data.getType());
            }
            ProMission mission = missionService.getById(missionId);
            String fileName = mission == null ? "未知任务_轴力类型信息表.xlsx" : mission.getName()+"_轴力类型信息表.xlsx";
            response.reset();
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1));
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ResponseUtil importData(List<TypeZlVO> list) {
        // 删除原任务下数据
        if (list.size() <= 0){
            return ResponseUtil.failure(500, "导入数据列表为空");
        }
        Long projectId = list.get(0).getProjectId();
        Long missionId = list.get(0).getMissionId();
        LambdaQueryWrapper<TypeZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeZl::getProjectId, projectId).eq(TypeZl::getMissionId, missionId);
        typeZlService.remove(wrapper);
        boolean b = typeZlService.saveBatch(DzBeanUtils.listCopy(list, TypeZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "批量导入数据失败");
    }

    @Override
    public void exportCalculateExcel(HttpServletResponse response) {
        try {
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/ECAC_ESA 参数计算模板.xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            response.reset();
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String("参数计算模板.xlsx".getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1));
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //region
    //endregion

}
