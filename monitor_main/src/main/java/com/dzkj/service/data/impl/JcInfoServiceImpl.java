package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.data.JcInfo;
import com.dzkj.mapper.data.JcInfoMapper;
import com.dzkj.service.data.IJcInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class JcInfoServiceImpl extends ServiceImpl<JcInfoMapper, JcInfo> implements IJcInfoService {

    @Override
    public boolean removeByProjectId(Long projectId) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JcInfo::getProjectId, projectId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeByMissionId(Long missionId) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JcInfo::getMissionId, missionId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(JcInfo::getMissionId, missionIds);
        return baseMapper.delete(wrapper) > 0;
    }

}
