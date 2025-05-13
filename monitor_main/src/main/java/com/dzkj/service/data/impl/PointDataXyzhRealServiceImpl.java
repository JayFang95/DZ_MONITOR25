package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzhRealVO;
import com.dzkj.entity.data.PointDataXyzhReal;
import com.dzkj.mapper.data.PointDataXyzhRealMapper;
import com.dzkj.service.data.IPointDataXyzhRealService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/2
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class PointDataXyzhRealServiceImpl extends ServiceImpl<PointDataXyzhRealMapper, PointDataXyzhReal> implements IPointDataXyzhRealService {

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataXyzhReal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzhReal::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeOnceData(List<Long> pidList, int recycleNum) {
        LambdaQueryWrapper<PointDataXyzhReal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyzhReal::getPid, pidList).eq(PointDataXyzhReal::getRecycleNum, recycleNum);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataXyzhRealVO> getList(OtherDataCondition condition) {
        return baseMapper.getList(condition);
    }

    @Override
    public Page<PointDataXyzhRealVO> getPage(Integer pi, Integer ps, OtherDataCondition condition) {
        return baseMapper.getPage(new Page<>(pi, ps), condition);
    }

    @Override
    public List<PointDataXyzhRealVO> getDataByPidList(List<Long> pidList) {
        return baseMapper.getDataByPidList(pidList);
    }

}
