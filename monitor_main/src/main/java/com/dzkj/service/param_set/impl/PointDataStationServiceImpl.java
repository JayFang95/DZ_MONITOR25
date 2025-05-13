package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.entity.param_set.PointDataStation;
import com.dzkj.mapper.param_set.PointDataStationMapper;
import com.dzkj.service.param_set.IPointDataStationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11
 * @description 测站配置点服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PointDataStationServiceImpl extends ServiceImpl<PointDataStationMapper, PointDataStation> implements IPointDataStationService {

    @Override
    public List<PointDataStation> getListInPid(List<Long> pidList) {
        LambdaQueryWrapper<PointDataStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PointDataStation::getPid, pidList);
        return baseMapper.selectList(wrapper);
    }
}
