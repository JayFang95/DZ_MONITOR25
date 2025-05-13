package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.vo.OtherDataCondition;
import com.dzkj.biz.data.vo.PointDataXyzVO;
import com.dzkj.entity.data.PointDataXyz;
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
public interface PointDataXyzMapper extends BaseMapper<PointDataXyz> {

    List<PointDataXyzVO> getList(@Param("cond") OtherDataCondition condition);

    Page<PointDataXyzVO> getPage(Page<PointDataXyzVO> page, @Param("cond") OtherDataCondition condition);

    List<PointDataXyzVO> getDataByPidList(@Param("list") List<Long> pidList);
}
