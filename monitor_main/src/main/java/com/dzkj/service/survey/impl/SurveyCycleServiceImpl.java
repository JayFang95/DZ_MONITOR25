package com.dzkj.service.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.survey.vo.SurveyCycleVo;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.survey.SurveyCycle;
import com.dzkj.mapper.survey.SurveyCycleMapper;
import com.dzkj.service.survey.ISurveyCycleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 采集周期服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class SurveyCycleServiceImpl extends ServiceImpl<SurveyCycleMapper, SurveyCycle> implements ISurveyCycleService {

    @Override
    public List<SurveyCycle> listByMissionId(Long missionId) {
        LambdaQueryWrapper<SurveyCycle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SurveyCycle::getMissionId, missionId)
                .orderByDesc(SurveyCycle::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean add(SurveyCycleVo data) {
        SurveyCycle copy = DzBeanUtils.propertiesCopy(data, SurveyCycle.class);
        return baseMapper.insert(copy) > 0;
    }

    @Override
    public boolean edit(SurveyCycleVo data) {
        SurveyCycle copy = DzBeanUtils.propertiesCopy(data, SurveyCycle.class);
        return baseMapper.updateById(copy) > 0;
    }
}
