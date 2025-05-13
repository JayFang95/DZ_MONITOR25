package com.dzkj.service.equipment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.equipment.vo.CtlBoxRecordCondition;
import com.dzkj.entity.equipment.ControlBoxRecord;
import com.dzkj.mapper.equipment.ControlBoxRecordMapper;
import com.dzkj.service.equipment.IControlBoxRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13
 * @description 控制器记录服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class ControlBoxRecordServiceImpl extends ServiceImpl<ControlBoxRecordMapper, ControlBoxRecord> implements IControlBoxRecordService {

    @Override
    public IPage<ControlBoxRecord> getPage(Integer pageIndex, Integer pageSize, CtlBoxRecordCondition cond) {
        Page<ControlBoxRecord> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<ControlBoxRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotEmpty(cond.getSerialNo()), ControlBoxRecord::getSerialNo, cond.getSerialNo())
                .ge(cond.getTime()!=null, ControlBoxRecord::getCreateTime, cond.getTime())
                .orderByDesc(ControlBoxRecord::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

}
