package com.dzkj.service.survey.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.survey.vo.RobotSurveyControlGroupVo;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.entity.survey.RobotSurveyControlGroup;
import com.dzkj.mapper.survey.RobotSurveyControlGroupMapper;
import com.dzkj.service.survey.IRobotSurveyControlGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/1/17
 * @description 监测控制组服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class RobotSurveyControlGroupServiceImpl extends ServiceImpl<RobotSurveyControlGroupMapper, RobotSurveyControlGroup> implements IRobotSurveyControlGroupService {

    @Override
    public List<RobotSurveyControlGroup> listByMissionId(Long missionId) {
        LambdaQueryWrapper<RobotSurveyControlGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RobotSurveyControlGroup::getMissionId, missionId)
                .orderByDesc(RobotSurveyControlGroup::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean add(RobotSurveyControlGroupVo data) {
        RobotSurveyControlGroup copy = DzBeanUtils.propertiesCopy(data, RobotSurveyControlGroup.class);
        int insert = baseMapper.insert(copy);
        data.setId(copy.getId());
        return insert > 0;
    }

    @Override
    public boolean edit(RobotSurveyControlGroupVo data) {
        RobotSurveyControlGroup copy = DzBeanUtils.propertiesCopy(data, RobotSurveyControlGroup.class);
        return baseMapper.updateById(copy) > 0;
    }

    @Override
    public void updateSurvey(Long id, int survey) {
        LambdaUpdateWrapper<RobotSurveyControlGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(RobotSurveyControlGroup::getId, id)
                .set(RobotSurveyControlGroup::getSurvey, survey);
        baseMapper.update(null, wrapper);
    }

}
