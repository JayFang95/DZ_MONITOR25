package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.entity.data.PushPoint;
import com.dzkj.service.data.IPushPointService;
import com.dzkj.mapper.data_swap.PushPointMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送测点服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushPointServiceImpl extends ServiceImpl<PushPointMapper, PushPoint> implements IPushPointService {

    @Override
    public List<PushPoint> queryList(Long taskId) {
        LambdaQueryWrapper<PushPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPoint::getPushTaskId, taskId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean add(PushPointVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushPoint.class)) > 0;
    }

    @Override
    public boolean edit(PushPointVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushPoint.class)) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean findSameCodeNotInTask(PushPointVO data) {
        LambdaQueryWrapper<PushPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPoint::getCode, data.getCode())
                .ne(PushPoint::getPushTaskId, data.getPushTaskId());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean findCodeAndDeviceCategoryNotSame(PushPointVO data) {
        return baseMapper.findCodeAndDeviceCategoryNotSame(data) > 0;
    }

    @Override
    public boolean removeByTaskId(Long pushTaskId) {
        LambdaQueryWrapper<PushPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPoint::getPushTaskId, pushTaskId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PushPoint> queryByMissionId(Long missionId) {
        return baseMapper.queryByMissionId(missionId) ;
    }

}
