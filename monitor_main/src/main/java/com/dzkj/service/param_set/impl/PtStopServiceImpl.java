package com.dzkj.service.param_set.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.data.vo.PtDataStopCondition;
import com.dzkj.biz.data.vo.PtStopVO;
import com.dzkj.entity.param_set.PtStop;
import com.dzkj.mapper.param_set.PtStopMapper;
import com.dzkj.service.param_set.IPtStopService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/23
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class PtStopServiceImpl extends ServiceImpl<PtStopMapper, PtStop> implements IPtStopService {

    @Override
    public IPage<PtStopVO> getPage(Integer pi, Integer ps, PtDataStopCondition condition) {
        return baseMapper.getPage(new Page<>(pi, ps), condition);
    }

    @Override
    public List<PtStopVO> getList(PtDataStopCondition condition) {
        return baseMapper.getList(condition);
    }
}
