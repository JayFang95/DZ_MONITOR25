package com.dzkj.service.project.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.project.CustomDisplay;
import com.dzkj.mapper.project.CustomDisplayMapper;
import com.dzkj.service.project.ICustomDisplayService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/12
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class CustomDisplayServiceImpl extends ServiceImpl<CustomDisplayMapper, CustomDisplay> implements ICustomDisplayService {

    @Override
    public boolean removeByMissionId(Long missionId) {
        LambdaQueryWrapper<CustomDisplay> wrapper = new LambdaQueryWrapper<CustomDisplay>();
        wrapper.eq(CustomDisplay::getMissionId, missionId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeByMissionIds(List<Long> missionIds) {
        LambdaQueryWrapper<CustomDisplay> wrapper = new LambdaQueryWrapper<CustomDisplay>();
        wrapper.in(CustomDisplay::getMissionId, missionIds);
        return baseMapper.delete(wrapper) > 0;
    }

}
