package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushTaskVO;
import com.dzkj.entity.data.PushTask;
import com.dzkj.mapper.data_swap.PushTaskMapper;
import com.dzkj.service.data.IPushTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送任务服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushTaskServiceImpl extends ServiceImpl<PushTaskMapper, PushTask> implements IPushTaskService {

    @Override
    public List<PushTask> queryList(Long companyId) {
        return baseMapper.queryList(companyId);
    }

    @Override
    public boolean add(PushTaskVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushTask.class)) > 0;
    }

    @Override
    public boolean edit(PushTaskVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushTask.class)) > 0;
    }

    @Override
    public boolean updateStatus(Long id, int status) {
        LambdaUpdateWrapper<PushTask> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PushTask::getId, id).set(PushTask::getStatus, status);
        return baseMapper.update(null, wrapper) > 0;
    }

    @Override
    public PushTask getByCode(String projectCode) {
        LambdaQueryWrapper<PushTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTask::getCode, projectCode);
        List<PushTask> list = baseMapper.selectList(wrapper);
        return !list.isEmpty() ? list.get(0) : null;
    }
}
