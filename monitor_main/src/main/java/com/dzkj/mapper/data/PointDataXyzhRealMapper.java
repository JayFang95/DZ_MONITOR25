package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzhRealVO;
import com.dzkj.entity.data.PointDataXyzhReal;
import org.apache.ibatis.annotations.Param;

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
public interface PointDataXyzhRealMapper extends BaseMapper<PointDataXyzhReal> {

    /**
     * 查询列表
     * @param condition condition
     * @return List<PointDataXyzhRealVO>
     */
    List<PointDataXyzhRealVO> getList(@Param("cond") OtherDataCondition condition);

    /**
     * 分页查询
     * @param page page
     * @param condition condition
     * @return Page<PointDataXyzhRealVO>
     */
    Page<PointDataXyzhRealVO> getPage(Page<Object> page, @Param("cond") OtherDataCondition condition);

    /**
     * 根据PID列表查询最新一期数据
     * @param pidList pidList
     * @return List<PointDataXyzhRealVO>
     */
    List<PointDataXyzhRealVO> getDataByPidList(@Param("list") List<Long> pidList);
}
