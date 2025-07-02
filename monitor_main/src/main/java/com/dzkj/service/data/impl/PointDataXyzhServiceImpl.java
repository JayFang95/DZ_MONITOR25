package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.PtDataChartCondition;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataXyzh;
import com.dzkj.mapper.data.PointDataXyzhMapper;
import com.dzkj.service.data.IPointDataXyzhService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class PointDataXyzhServiceImpl extends ServiceImpl<PointDataXyzhMapper, PointDataXyzh> implements IPointDataXyzhService {

    @Override
    public List<ReportData> getDataByCond(TableDataCondition condition, List<Long> pidList) {
        return baseMapper.getDataByCond(condition, pidList);
    }

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeOnceData(List<Long> pidList, int recycleNum) {
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzh::getPid, pidList).eq(PointDataXyzh::getRecycleNum, recycleNum);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataXyzh> queryLatestData(List<Long> pointIds) {
        if (pointIds == null || pointIds.isEmpty()){
            return new ArrayList<>();
        }
        return baseMapper.queryLatestData(pointIds);
    }

    @Override
    public PointDataXyzh getLastNoAlarmData(Long pointId) {
        return baseMapper.getLastNoAlarmData(pointId);
    }

    @Override
    public List<PointDataXyzh> getDateLimit(PtDataChartCondition condition) {
        return baseMapper.getDateLimit(condition.getSelectIds());
    }

    @Override
    public List<PointDataXyzh> getEarliestRecycleData(List<Long> pidList) {
        return baseMapper.getEarliestRecycleData(pidList);
    }

    @Override
    public List<PointDataXyzh> getEarliestResetRecycleData(List<Long> pidList) {
        return baseMapper.getEarliestResetRecycleData(pidList);
    }

}
