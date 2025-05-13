package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.PointDataZRealVO;
import com.dzkj.entity.data.PointDataZReal;
import com.dzkj.mapper.data.PointDataZRealMapper;
import com.dzkj.service.data.IPointDataZRealService;
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
public class PointDataZRealServiceImpl extends ServiceImpl<PointDataZRealMapper, PointDataZReal> implements IPointDataZRealService {

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataZReal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataZReal::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataZRealVO> getDataByPidList(List<Long> pidList) {
        return baseMapper.getDataByPidList(pidList);
    }

}
