package com.dzkj.biz.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.param_set.ISensorZlBiz;
import com.dzkj.biz.param_set.vo.SensorZlVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.param_set.SensorZl;
import com.dzkj.service.param_set.ISensorZlService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 传感器信息业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class SensorZlBizImpl implements ISensorZlBiz {

    @Autowired
    private ISensorZlService sensorZlService;

    @Override
    public List<SensorZlVO> getList(Long pointId) {
        LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorZl::getPointId, pointId).orderByDesc(SensorZl::getCreateTime);
        return DzBeanUtils.listCopy(sensorZlService.list(wrapper), SensorZlVO.class);
    }

    @Override
    public ResponseUtil add(SensorZlVO data) {
        if (sensorZlService.checkName(data)){
            return ResponseUtil.failure(500, "钢筋计编号工程内重复");
        }
        boolean b = sensorZlService.save(DzBeanUtils.propertiesCopy(data, SensorZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(SensorZlVO data) {
        if (sensorZlService.checkName(data)){
            return ResponseUtil.failure(500, "钢筋计编号工程内重复");
        }
        boolean b = sensorZlService.updateById(DzBeanUtils.propertiesCopy(data, SensorZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = sensorZlService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public void exportData(Long missionId, HttpServletResponse response) {
        try {
            // 查询任务包含测点传感器列表
            List<SensorZlVO> list = DzBeanUtils.listCopy(sensorZlService.getListByMissionId(missionId), SensorZlVO.class);
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/传感器信息(样例).xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 填充数据
            for (SensorZlVO data : list) {
                rowIndex++;
                Row row = sheet.getRow(rowIndex);
                Cell cell0 = row.getCell(0);
                cell0.setCellValue(data.getPointId());
                Cell cell1 = row.getCell(1);
                cell1.setCellValue(data.getPointName());
                Cell cell2 = row.getCell(2);
                cell2.setCellValue(data.getTypeZl());
                Cell cell3 = row.getCell(3);
                cell3.setCellValue(data.getJxgCode());
                Cell cell4 = row.getCell(4);
                cell4.setCellValue(data.getCalibration());
                Cell cell5 = row.getCell(5);
                cell5.setCellValue(data.getLocation());
            }
            String fileName = "测点传感器信息表.xlsx";
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
    @Transactional
    public ResponseUtil importData(Long missionId, List<SensorZlVO> list) {
        // 删除原数据
        // 查询任务包含测点传感器列表
        List<SensorZlVO> sensorList = DzBeanUtils.listCopy(sensorZlService.getListByMissionId(missionId), SensorZlVO.class);
        if (sensorList.size() > 0){
            List<Long> collect = sensorList.stream().map(SensorZlVO::getId).collect(Collectors.toList());
            sensorZlService.removeByIds(collect);
        }
        boolean b = sensorZlService.saveBatch(DzBeanUtils.listCopy(list, SensorZl.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "数据导入失败");
    }

    @Override
    public List<SensorZlVO> getListInProject(Long projectId) {
        LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorZl::getProjectId, projectId).orderByDesc(SensorZl::getCreateTime);
        return DzBeanUtils.listCopy(sensorZlService.list(wrapper), SensorZlVO.class);
    }

    //region 私有方法
    //endregion

}
