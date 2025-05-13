package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataZlVO;
import com.dzkj.entity.data.PointDataZl;
import org.apache.ibatis.annotations.Param;

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
public interface PointDataZlMapper extends BaseMapper<PointDataZl> {

    List<PointDataZlVO> getList(@Param("cond") OtherDataCondition condition);

    Page<PointDataZlVO> getPage(Page<PointDataZlVO> objectPage, @Param("cond")  OtherDataCondition condition);

    List<PointDataZlVO> getDataByPidList(@Param("list") List<Long> pidList);
}
