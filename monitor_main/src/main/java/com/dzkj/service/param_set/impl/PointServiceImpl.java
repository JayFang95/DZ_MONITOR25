package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.dashoborad.vo.StatisticTempData;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.entity.param_set.Point;
import com.dzkj.mapper.param_set.PointMapper;
import com.dzkj.service.param_set.IPointService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点服务实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements IPointService {

    @Override
    public boolean findByName(PointVO point, Long groupId) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Point::getPtGroupId, groupId)
                .eq(Point::getName, point.getName())
                .ne(point.getId()!=null, Point::getId, point.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean findByName(PointVO point, List<Long> groupIds) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId,groupIds)
                .eq(Point::getName, point.getName())
                .ne(point.getId()!=null, Point::getId, point.getId());
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Point> queryByGroupId(Long groupId) {
        return baseMapper.queryByGroupId(groupId);
    }

    @Override
    public List<Point> queryByMissionId(Long missionId) {
        return baseMapper.queryByMissionId(missionId);
    }

    @Override
    public List<Point> queryByMissionIds(List<Long> missionIds){
        return baseMapper.queryByMissionIds(missionIds);
    }

    @Override
    public List<StatisticTempData> getIdInProject(List<Long> projectIds) {
        return baseMapper.getIdInProject(projectIds);
    }

    @Override
    public List<Point> listByPtGroupIds(List<Long> ptGroupIds) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId, ptGroupIds).select(Point::getId)
                .orderByAsc(Point::getSeq);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean removeByPtGroupIds(List<Long> ptGroupIds) {
        LambdaQueryWrapper<Point> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Point::getPtGroupId, ptGroupIds).select(Point::getId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<Point> listPoint(Long missionId, Long pushTaskId, Long pointId) {
        return baseMapper.listPoint(missionId, pushTaskId, pointId);
    }

    @Override
    public List<Point> listPointJn(Long missionId, Long pushTaskId, Long pointId) {
        return baseMapper.listPointJn(missionId, pushTaskId, pointId);
    }

}
