package com.dzkj.mapper.param_set;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.param_set.vo.GroupCondition;
import com.dzkj.entity.param_set.PtGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 点组mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface PtGroupMapper extends BaseMapper<PtGroup> {

    List<PtGroup> getList(@Param("cond")GroupCondition condition);

    Page<PtGroup> getPage(Page<PtGroup> ptGroupPage, @Param("cond")GroupCondition condition);

    List<PtGroup> findByProjectId(@Param("projectId") Long projectId, @Param("missionId") Long missionId);
}
