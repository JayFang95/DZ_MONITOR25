package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.PtDataChartCondition;
import com.dzkj.biz.data.vo.ReportData;
import com.dzkj.biz.data.vo.TableDataCondition;
import com.dzkj.entity.data.PointDataZ;
import com.dzkj.mapper.data.PointDataZMapper;
import com.dzkj.service.data.IPointDataZService;
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
public class PointDataZServiceImpl extends ServiceImpl<PointDataZMapper, PointDataZ> implements IPointDataZService {

    @Override
    public List<ReportData> getDataByCond(TableDataCondition condition, List<Long> pidList) {
        return baseMapper.getDataByCond(condition, pidList);
    }

    @Override
    public boolean removeByPtIds(List<Long> ptIds) {
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataZ::getPid, ptIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PointDataZ> getDateLimit(PtDataChartCondition condition) {
        return baseMapper.getDateLimit(condition.getSelectIds());
    }

}
