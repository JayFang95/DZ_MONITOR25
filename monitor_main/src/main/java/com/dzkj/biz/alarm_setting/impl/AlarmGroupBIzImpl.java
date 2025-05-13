package com.dzkj.biz.alarm_setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.alarm_setting.IAlarmGroupBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmGroupVO;
import com.dzkj.biz.alarm_setting.vo.AlarmItemVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmGroup;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.param_set.PtGroup;
import com.dzkj.service.alarm_setting.IAlarmGroupService;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.param_set.IPtGroupService;
import com.dzkj.service.project.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2021-09-09 9:27
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

@Component
public class AlarmGroupBIzImpl implements IAlarmGroupBiz {
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IAlarmGroupService iAlarmGroupService;
    @Autowired
    private IAlarmItemService iAlarmItemService;
    @Autowired
    private IPtGroupService ptGroupService;

    @Override
    public IPage<AlarmGroupVO> list(Integer pi, Integer ps, List<Long> projectIds) {
        if (projectIds == null || projectIds.size() == 0){
            return new Page<>(pi, ps, 0);
        }
        if (ps == CommonConstant.SEARCH_ALL_NO){
            Page<AlarmGroupVO> page = new Page<>(pi, ps);
            LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(AlarmGroup::getProjectId, projectIds)
                    .orderByDesc(AlarmGroup::getCreateTime);
            List<AlarmGroupVO> list = DzBeanUtils.listCopy(iAlarmGroupService.list(wrapper), AlarmGroupVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(CommonConstant.SEARCH_ALL_NO, list.size()));
            return page;
        }else {
            LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(AlarmGroup::getProjectId, projectIds)
                    .orderByDesc(AlarmGroup::getCreateTime);
            return DzBeanUtils.pageCopy(iAlarmGroupService.page(new Page<>(pi, ps), wrapper), AlarmGroupVO.class);
        }
    }

    @Override
    public ResponseUtil add(AlarmGroupVO alarmGroup) {
        //验证名称
        if (checkName(alarmGroup)) {
            return ResponseUtil.failure(500, "报警组名工程内重复");
        }
        boolean b = iAlarmGroupService.save(DzBeanUtils.propertiesCopy(alarmGroup, AlarmGroup.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }


    @Override
    public ResponseUtil update(AlarmGroupVO alarmGroup) {
        //验证名称
        if (checkName(alarmGroup)) {
            return ResponseUtil.failure(500, "报警组名工程内重复");
        }
        boolean b = iAlarmGroupService.updateById(DzBeanUtils.propertiesCopy(alarmGroup, AlarmGroup.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        AlarmGroup alarmGroup = iAlarmGroupService.getById(id);
        if (alarmGroup == null) {
            return ResponseUtil.failure(500, "报警组不存在或已删除");
        }
        // 验证报警组是否已被配置
        LambdaQueryWrapper<PtGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PtGroup::getAlarmGroupId, id);
        if (ptGroupService.count(wrapper) > 0){
            return ResponseUtil.failure(500, "报警组配置使用中, 不能删除");
        }
        boolean b = iAlarmGroupService.removeById(id);
        if (b) {
            deleteAlarmItem(id);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil exportGroup(Long projectId) {
        // 查询所有报警组数据
        LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmGroup::getProjectId, projectId).orderByDesc(AlarmGroup::getCreateTime);
        List<AlarmGroupVO> groups = DzBeanUtils.listCopy(iAlarmGroupService.list(wrapper), AlarmGroupVO.class);
        for (AlarmGroupVO group : groups) {
            LambdaQueryWrapper<AlarmItem> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(AlarmItem::getAlarmGroupId, group.getId()).orderByDesc(AlarmItem::getCreateTime);
            List<AlarmItem> list = iAlarmItemService.list(wrapper1);
            group.setAlarmItemList(DzBeanUtils.listCopy(list, AlarmItemVO.class));
        }
        return ResponseUtil.success(groups);
    }

    @Override
    public ResponseUtil importGroup(List<AlarmItemVO> itemList) {
        boolean b = iAlarmItemService.saveBatch(DzBeanUtils.listCopy(itemList, AlarmItem.class));
        return  b ? ResponseUtil.success() : ResponseUtil.failure(500, "导入报警组异常");
    }

    @Override
    public List<DropVO> getAlarmGroupList(Long projectId) {
        LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmGroup::getProjectId, projectId)
                .orderByDesc(AlarmGroup::getCreateTime);
        return DzBeanUtils.listCopy(iAlarmGroupService.list(wrapper), DropVO.class);
    }

    //region 私有方法
    /**
     * 验证组名
     **/
    private boolean checkName(AlarmGroupVO alarmGroup) {
        LambdaQueryWrapper<AlarmGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmGroup::getProjectId, alarmGroup.getProjectId())
                .eq(AlarmGroup::getName, alarmGroup.getName())
                // 排除同一个对象修改的时候名称相同
                .ne(alarmGroup.getId() != null, AlarmGroup::getId, alarmGroup.getId());
        return iAlarmGroupService.count(wrapper) > 0;
    }

    /**
     * 删除报警组中的报警阈值对象
     **/
    private void deleteAlarmItem(Long id) {
        LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmItem::getAlarmGroupId, id);
        iAlarmItemService.remove(wrapper);
    }
    //endregion

}
