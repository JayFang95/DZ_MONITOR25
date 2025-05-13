package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.param_set.vo.SensorZlVO;
import com.dzkj.entity.param_set.SensorZl;
import com.dzkj.mapper.param_set.SensorZlMapper;
import com.dzkj.service.param_set.ISensorZlService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class SensorZlServiceImpl extends ServiceImpl<SensorZlMapper, SensorZl> implements ISensorZlService {

    @Override
    public List<SensorZl> getListByMissionId(Long missionId) {
        return baseMapper.getListByMissionId(missionId);
    }

    @Override
    public boolean removeByProjectId(Long projectId) {
        LambdaQueryWrapper<SensorZl> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SensorZl::getProjectId, projectId);
        return baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeByPointIds(List<Long> ptIds) {
        LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SensorZl::getPointId, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean checkName(SensorZlVO data) {
        LambdaQueryWrapper<SensorZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensorZl::getJxgCode, data.getJxgCode())
                .eq(SensorZl::getProjectId, data.getProjectId())
                .ne(data.getId()!=null, SensorZl::getId, data.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }
}
