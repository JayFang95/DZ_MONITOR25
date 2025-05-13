package com.dzkj.biz.alarm_setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.alarm_setting.IDistributeBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmDistributeVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmDistribute;
import com.dzkj.service.alarm_setting.IAlarmDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/6
 * @description 角色业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class DistributeBizImpl implements IDistributeBiz {

    @Autowired
    private IAlarmDistributeService iAlarmDistributeService;

    @Override
    public IPage<AlarmDistributeVO> list(Integer pi, Integer ps, List<Long> projectIds) {
        if (projectIds == null || projectIds.size() == 0){
            return new Page<>(pi, ps, 0);
        }
        if (ps == CommonConstant.SEARCH_ALL_NO){
            Page<AlarmDistributeVO> page = new Page<>(pi, ps);
            LambdaQueryWrapper<AlarmDistribute> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(AlarmDistribute::getProjectId, projectIds)
                    .orderByDesc(AlarmDistribute::getCreateTime);
            List<AlarmDistributeVO> list = DzBeanUtils.listCopy(iAlarmDistributeService.list(wrapper), AlarmDistributeVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(CommonConstant.SEARCH_ALL_NO, list.size()));
            return page;
        }else {
            LambdaQueryWrapper<AlarmDistribute> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(AlarmDistribute::getProjectId, projectIds)
                    .orderByDesc(AlarmDistribute::getCreateTime);
            return DzBeanUtils.pageCopy(iAlarmDistributeService.page(new Page<>(pi, ps), wrapper), AlarmDistributeVO.class);
        }
    }

    @Override
    public ResponseUtil add(AlarmDistributeVO alarmDistribute) {
        //验证名称
        if(checkName(alarmDistribute)){
            return ResponseUtil.failure(2001, "报警规则名重复");
        }
        AlarmDistribute copy = DzBeanUtils.propertiesCopy(alarmDistribute, AlarmDistribute.class);
        boolean b = iAlarmDistributeService.save(copy);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(AlarmDistributeVO alarmDistribute) {
        //验证名称
        if(checkName(alarmDistribute)){
            return ResponseUtil.failure(2001, "报警规则名重复");
        }
        boolean b = iAlarmDistributeService.updateById(DzBeanUtils.propertiesCopy(alarmDistribute, AlarmDistribute.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = iAlarmDistributeService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<DropVO> getAlarmDistributeList(Long projectId) {
        LambdaQueryWrapper<AlarmDistribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmDistribute::getProjectId, projectId).orderByDesc(AlarmDistribute::getCreateTime);
        return DzBeanUtils.listCopy(iAlarmDistributeService.list(wrapper), DropVO.class);
    }

    @Override
    public ResponseUtil importDistribute(List<AlarmDistributeVO> list) {
        boolean b = iAlarmDistributeService.saveBatch(DzBeanUtils.listCopy(list, AlarmDistribute.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "导入规则失败");
    }

    //region 私有方法
    /**
     * 验证名称
     * @author liao
     * @date 2021-09-07 08:46
     * @param alarmDistribute alarmDistribute
     * @return boolean
    **/
    private boolean checkName(AlarmDistributeVO alarmDistribute) {
        LambdaQueryWrapper<AlarmDistribute> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(AlarmDistribute::getProjectId, alarmDistribute.getProjectId())
                .eq(AlarmDistribute::getName, alarmDistribute.getName())
                // 排除同一个对象修改的时候名称相同
                .ne(alarmDistribute.getId() !=null, AlarmDistribute::getId, alarmDistribute.getId());
        return iAlarmDistributeService.count(wrapper) > 0;
    }
    //endregion
}
