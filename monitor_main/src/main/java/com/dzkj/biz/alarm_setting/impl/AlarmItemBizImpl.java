package com.dzkj.biz.alarm_setting.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.alarm_setting.IAlarmItemBiz;
import com.dzkj.biz.alarm_setting.vo.AlarmItemVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2021-09-09 9:26
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

@Component
public class AlarmItemBizImpl implements IAlarmItemBiz {

    @Autowired
    private IAlarmItemService iAlarmItemService;

    @Override
    public List<AlarmItemVO> list(Long groupId) {
        LambdaQueryWrapper<AlarmItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmItem::getAlarmGroupId, groupId)
                .orderByDesc(AlarmItem::getCreateTime);
        return DzBeanUtils.listCopy(iAlarmItemService.list(wrapper), AlarmItemVO.class);
    }

    @Override
    public ResponseUtil add(List<AlarmItemVO> alarmItems) {
        boolean b = iAlarmItemService.saveBatch(DzBeanUtils.listCopy(alarmItems, AlarmItem.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(AlarmItemVO alarmItem) {
        boolean b = iAlarmItemService.updateById(DzBeanUtils.propertiesCopy(alarmItem, AlarmItem.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = iAlarmItemService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }
}
