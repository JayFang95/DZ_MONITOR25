package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.biz.data.vo.PointDataZRealVO;
import com.dzkj.entity.data.PointDataZReal;
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
public interface PointDataZRealMapper extends BaseMapper<PointDataZReal> {

    List<PointDataZRealVO> getDataByPidList(@Param("list") List<Long> pidList);
}
