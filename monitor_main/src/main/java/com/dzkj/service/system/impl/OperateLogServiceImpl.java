package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.system.vo.OperateLogCondition;
import com.dzkj.entity.system.OperateLog;
import com.dzkj.mapper.system.OperateLogMapper;
import com.dzkj.service.system.IOperateLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 日志service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class OperateLogServiceImpl extends ServiceImpl<OperateLogMapper, OperateLog> implements IOperateLogService {

    @Override
    public IPage<OperateLog> getPage(int pageIndex, int pageSize, OperateLogCondition condition) {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        // update: 2022/6/23  查询所有公司日志
        wrapper.ge(condition.getStartDate()!=null, OperateLog::getCreateTime, condition.getStartDate());
        wrapper.le(condition.getEndDate()!=null, OperateLog::getCreateTime, condition.getEndDate());
        wrapper.like(StringUtils.isNotEmpty(condition.getContent()),OperateLog::getContent,condition.getContent());
        wrapper.orderByDesc(OperateLog::getCreateTime);
        return baseMapper.selectPage(new Page<>(pageIndex, pageSize), wrapper);
    }

    @Override
    public boolean removeByCompanyId(Long companyId) {
        LambdaQueryWrapper<OperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperateLog::getCompanyId, companyId);
        return baseMapper.delete(wrapper) > 0;
    }
}
