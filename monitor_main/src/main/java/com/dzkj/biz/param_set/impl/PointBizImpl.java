package com.dzkj.biz.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.param_set.IPointBiz;
import com.dzkj.biz.param_set.vo.ExportPoint;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.common.constant.MissionTypeConst;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.entity.param_set.PtStop;
import com.dzkj.entity.project.ProMission;
import com.dzkj.entity.project.Project;
import com.dzkj.service.data.*;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.param_set.IPtStopService;
import com.dzkj.service.param_set.ISensorZlService;
import com.dzkj.service.project.IProMissionService;
import com.dzkj.service.project.IProjectService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class PointBizImpl implements IPointBiz {

    @Autowired
    private IPointService pointService;
    @Autowired
    private IPtStopService stopService;
    @Autowired
    private IPtGroupService ptGroupService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private ISensorZlService sensorZlService;
    @Autowired
    private IPointDataZService dataZService;
    @Autowired
    private IPointDataZRealService dataZRealService;
    @Autowired
    private IPointDataXyzhService dataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IPointDataXyzService dataXyzService;
    @Autowired
    private IPointDataZlService dataZlService;

    @Override
    public List<PointVO> getList(Long groupId) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Point::getPtGroupId, groupId)
                .orderByAsc(Point::getSeq);
        return DzBeanUtils.listCopy(pointService.list(wrapper),PointVO.class);
    }

    @Override
    public ResponseUtil add(PointVO point) {
        if(MissionTypeConst.AUTO_XYZ_OFFSET.equals(point.getMissionType())){
            List<Long> groupIds = ptGroupService.getGroupIdsInMission(point.getPtGroupId());
            if(pointService.findByName(point, groupIds)){
                return ResponseUtil.failure(500, "当前任务存在相同测点名["+point.getName()+"]");
            }
        }else {
            if(pointService.findByName(point, point.getPtGroupId())){
                return ResponseUtil.failure(500, "当前编组存在相同测点名["+point.getName()+"]");
            }
        }
        boolean b = pointService.save(DzBeanUtils.propertiesCopy(point, Point.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(PointVO point) {
        if(MissionTypeConst.AUTO_XYZ_OFFSET.equals(point.getMissionType())){
            List<Long> groupIds = ptGroupService.getGroupIdsInMission(point.getPtGroupId());
            if(pointService.findByName(point, groupIds)){
                return ResponseUtil.failure(500, "当前任务存在相同测点名["+point.getName()+"]");
            }
        }else {
            if(pointService.findByName(point, point.getPtGroupId())){
                return ResponseUtil.failure(500, "当前编组存在相同测点名["+point.getName()+"]");
            }
        }
        boolean b = pointService.updateById(DzBeanUtils.propertiesCopy(point, Point.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = pointService.removeById(id);
        if (b){
            ThreadPoolUtil.getPool().execute(() -> {
                List<Long> ptIds = Collections.singletonList(id);
                // 删除传感器
                sensorZlService.removeByPointIds(ptIds);
                // 删除监测数据
                dataZService.removeByPtIds(ptIds);
                dataZRealService.removeByPtIds(ptIds);
                dataXyzhService.removeByPtIds(ptIds);
                dataXyzhRealService.removeByPtIds(ptIds);
                dataXyzService.removeByPtIds(ptIds);
                dataZlService.removeByPtIds(ptIds);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil saveBatch(List<PointVO> list) {
        List<PointVO> points = new ArrayList<>();
        int successNum = 0;
        int failNum = 0;
        for (PointVO point : list) {
            if(MissionTypeConst.AUTO_XYZ_OFFSET.equals(point.getMissionType())){
                List<Long> groupIds = ptGroupService.getGroupIdsInMission(point.getPtGroupId());
                if(pointService.findByName(point, groupIds)){
                    failNum++;
                }else {
                    successNum++;
                    points.add(point);
                }
            }else {
                if(pointService.findByName(point, point.getPtGroupId())){
                    failNum++;
                }else {
                    successNum++;
                    points.add(point);
                }
            }
        }
        if (points.size() > 0){
           pointService.saveBatch(DzBeanUtils.listCopy(points, Point.class));
        }
        String msg = failNum==0 ? "批量添加成功: 导入点数["+successNum + "]"
                : "批量添加成功: 重复点数["+failNum+"],导入点数["+successNum+ "]";
        return  ResponseUtil.success(msg) ;
    }

    @Override
    public ResponseUtil importBatch(List<PointVO> list){
        List<PointVO> points = new ArrayList<>();
        int successNum = 0;
        int failNum = 0;
        for (PointVO point : list) {
            if(MissionTypeConst.AUTO_XYZ_OFFSET.equals(point.getMissionType())){
                List<Long> groupIds = ptGroupService.getGroupIdsInMission(point.getPtGroupId());
                if(pointService.findByName(point, groupIds)){
                    failNum++;
                }else {
                    successNum++;
                    points.add(point);
                }
            }else {
                if(pointService.findByName(point, point.getPtGroupId())){
                    failNum++;
                }else {
                    successNum++;
                    points.add(point);
                }
            }
        }
        if (points.size() > 0){
            setPointSeq(points);
            pointService.saveBatch(DzBeanUtils.listCopy(points, Point.class));
        }
        String msg = failNum==0 ? "批量导入完成: 导入点数["+successNum + "]"
                : "批量导入完成: 重复点数["+failNum+"],导入点数["+successNum+ "]";
        return  ResponseUtil.success(msg) ;
    }

    /**
     * 设置测点排序
     */
    private void setPointSeq(List<PointVO> points) {
        List<Long> ptGroupIds = points.stream().map(PointVO::getPtGroupId).collect(Collectors.toList());
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId, ptGroupIds);
        List<Point> list = pointService.list(wrapper);
        for (Long groupId : ptGroupIds) {
            List<PointVO> filterList = points.stream().filter(item -> item.getPtGroupId().equals(groupId)).collect(Collectors.toList());
            int seq = list.stream().map(Point::getSeq).max(Comparator.comparing(a -> a)).orElse(0);
            for (PointVO pointVO : filterList) {
                seq++;
                pointVO.setSeq(seq);
            }
        }
    }

    @Override
    public void exportBatch(Long projectId, HttpServletResponse response){
        try {
            // 查询当前工程下所有测点数据
            List<ProMission> missions = missionService.getMissionByProjectId(projectId);
            if (missions.size() == 0){
                return;
            }
            List<ExportPoint> list = new ArrayList<>();
            missions.forEach(mission -> {
                LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(PtGroup::getMissionId, mission.getId()).orderByDesc(PtGroup::getCreateTime);
                List<PtGroup> groups = ptGroupService.list(wrapper);
                groups.forEach(group -> {
                    LambdaQueryWrapper<Point> wrapper1 = new LambdaQueryWrapper<>();
                    wrapper1.eq(Point::getPtGroupId, group.getId()).orderByDesc(Point::getCreateTime);
                    List<Point> points = pointService.list(wrapper1);
                    if (points.size() > 0){
                        points.forEach(point -> {
                            ExportPoint exportPoint = new ExportPoint().setItemName(mission.getName())
                                    .setGroupName(group.getName()).setPtName(point.getName())
                                    .setPtType(point.getType()).setPtStop(point.getStop() ? "是" : "否");
                            list.add(exportPoint);
                        });
                    }else {
                        ExportPoint exportPoint = new ExportPoint().setItemName(mission.getName())
                                .setGroupName(group.getName());
                        list.add(exportPoint);
                    }
                });
            });
            // 获取工作簿
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("static/测点属性信息(样例).xlsx");
            Resource resource = resources[0];
            Workbook wb = new XSSFWorkbook(resource.getInputStream());
            Sheet sheet = wb.getSheetAt(0);
            int rowIndex = 0;
            // 填充数据
            for (ExportPoint data : list) {
                rowIndex++;
                Row row = sheet.getRow(rowIndex);
                Cell cell0 = row.getCell(0);
                cell0.setCellValue(data.getItemName());
                Cell cell1 = row.getCell(1);
                cell1.setCellValue(data.getGroupName());
                Cell cell2 = row.getCell(2);
                cell2.setCellValue(data.getPtName());
                Cell cell3 = row.getCell(3);
                cell3.setCellValue(data.getPtType());
                Cell cell4 = row.getCell(4);
                cell4.setCellValue(data.getPtStop());
            }
            Project project = projectService.getById(projectId);
            String fileName = project==null ? "未知工程_测点信息表.xlsx" : project.getName() + "_测点信息表.xlsx";
            response.reset();
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1));
            wb.write(response.getOutputStream());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseUtil changeStatus(PointVO point) {
        LambdaUpdateWrapper<Point> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Point::getStop, point.getStop());
        if (point.getStop()){
            // 保存停测记录
            PtStop ptStop = new PtStop();
            ptStop.setPointId(point.getId());
            ptStop.setReason(point.getReason());
            stopService.save(ptStop);
        }
        updateWrapper.eq(Point::getId, point.getId());
        boolean b = pointService.update(updateWrapper);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    public ResponseUtil changePointSeq(List<PointVO> points){
        boolean b = pointService.updateBatchById(DzBeanUtils.listCopy(points, Point.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public List<PointVO> queryByMissionId(Long missionId) {
        return DzBeanUtils.listCopy(pointService.queryByMissionId(missionId), PointVO.class);
    }

    //region
    //endregion

}
