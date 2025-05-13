package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.system.IPresetBiz;
import com.dzkj.biz.system.vo.AlarmSettingVO;
import com.dzkj.biz.system.vo.MonitorTypeVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.AlarmSetting;
import com.dzkj.entity.system.MonitorType;
import com.dzkj.entity.system.ProjectType;
import com.dzkj.service.system.IAlarmSettingService;
import com.dzkj.service.system.IMonitorTypeService;
import com.dzkj.service.system.IProjectTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/17
 * @description 系统预置业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class PresetBizImpl implements IPresetBiz {

    @Autowired
    private IMonitorTypeService monitorTypeService;
    @Autowired
    private IProjectTypeService projectTypeService;
    @Autowired
    private IAlarmSettingService alarmSettingService;

    @Override
    public ResponseUtil updateMonitorType(MonitorType monitorType) {
        if(monitorTypeService.checkTypeName(monitorType)){
            return ResponseUtil.failure(500, "监测类型重复");
        }
        boolean b = monitorTypeService.updateById(monitorType);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil updateMonitorIndex(Long id, Long toId) {
        MonitorType monitor = monitorTypeService.getById(id);
        Integer index = monitor.getIndex();
        MonitorType toMonitor = monitorTypeService.getById(toId);
        Integer toIndex = toMonitor.getIndex();
        monitor.setIndex(toIndex);
        toMonitor.setIndex(index);
        monitorTypeService.updateById(monitor);
        monitorTypeService.updateById(toMonitor);
        return ResponseUtil.success();
    }

    @Override
    public ResponseUtil saveProject(ProjectType projectType) {
        if(projectTypeService.checkName(projectType)){
            return ResponseUtil.failure(500, "项目类型重复");
        }
        boolean b = projectTypeService.saveOrUpdate(projectType);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil deleteProject(Long id) {
        boolean b = projectTypeService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil updateAlarm(List<AlarmSetting> list) {
        boolean b = alarmSettingService.saveOrUpdateBatch(list);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public List<MonitorTypeVO> getMonitorDrop() {
        LambdaQueryWrapper<MonitorType> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(MonitorType::getPName)
                .orderByAsc(MonitorType::getIndex);
        return DzBeanUtils.listCopy(monitorTypeService.list(wrapper),MonitorTypeVO.class);
    }

    @Override
    public List<MonitorTypeVO> getMonitorList() {
        LambdaQueryWrapper<MonitorType> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MonitorType::getPName)
                .orderByAsc(MonitorType::getIndex);
        return DzBeanUtils.listCopy(monitorTypeService.list(wrapper),MonitorTypeVO.class);
    }

    @Override
    public List<AlarmSettingVO> getAlarmLevelList() {
        return DzBeanUtils.listCopy(alarmSettingService.list(),AlarmSettingVO.class);
    }

}
