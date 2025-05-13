package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzVO;
import com.dzkj.entity.data.PointDataXyz;
import com.dzkj.mapper.data.PointDataXyzMapper;
import com.dzkj.service.data.IPointDataXyzService;
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
public class PointDataXyzServiceImpl extends ServiceImpl<PointDataXyzMapper, PointDataXyz> implements IPointDataXyzService {

    @Override
    public List<PointDataXyzVO> getList(OtherDataCondition condition) {
        return baseMapper.getList(condition);
    }

    @Override
    public Page<PointDataXyzVO> getPage(Integer pi, Integer ps, OtherDataCondition condition) {
        return baseMapper.getPage(new Page<>(pi, ps), condition);
    }

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataXyz> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataXyz::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataXyzVO> getDataByPidList(List<Long> pidList) {
        return baseMapper.getDataByPidList(pidList);
    }

}
