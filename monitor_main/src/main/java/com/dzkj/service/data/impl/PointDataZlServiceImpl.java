package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataZlVO;
import com.dzkj.entity.data.PointDataZl;
import com.dzkj.mapper.data.PointDataZlMapper;
import com.dzkj.service.data.IPointDataZlService;
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
public class PointDataZlServiceImpl extends ServiceImpl<PointDataZlMapper, PointDataZl> implements IPointDataZlService {

    @Override
    public List<PointDataZlVO> getList(OtherDataCondition condition) {
        return baseMapper.getList(condition);
    }

    @Override
    public Page<PointDataZlVO> getPage(Integer pi, Integer ps, OtherDataCondition condition) {
        return baseMapper.getPage(new Page<>(pi, ps), condition);
    }

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataZl> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataZl::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataZlVO> getDataByPidList(List<Long> pidList) {
        return baseMapper.getDataByPidList(pidList);
    }

}
