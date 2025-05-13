package com.dzkj.mapper.param_set;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.vo.PtDataStopCondition;
import com.dzkj.biz.data.vo.PtStopVO;
import com.dzkj.entity.param_set.PtStop;
import org.apache.ibatis.annotations.Param;

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
public interface PtStopMapper extends BaseMapper<PtStop> {

    IPage<PtStopVO> getPage(Page<PtStopVO> page, @Param("cond") PtDataStopCondition condition);

    List<PtStopVO> getList(@Param("cond") PtDataStopCondition condition);
}
