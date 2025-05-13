package com.dzkj.mapper.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.project.vo.ProMissionCondition;
import com.dzkj.entity.project.ProMission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/3
 * @description 监测任务mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ProMissionMapper extends BaseMapper<ProMission> {

    IPage<ProMission> getPage(Page<ProMission> page,  @Param("cond") ProMissionCondition cond);

    List<ProMission> queryList(@Param("cond") ProMissionCondition cond);

    List<ProMission> getList(@Param("projectId") Long projectId);

    ProMission findById(@Param("missionId") Long missionId);
    List<ProMission> getList2(@Param("list") List<Long> missionIds);

    List<ProMission> getMissionInCompany(@Param("companyId") Long companyId, @Param("missionId") Long missionId);
    List<ProMission> getMissionOtherInCompany(@Param("companyId") Long companyId, @Param("missionId") Long missionId);

    List<ProMission> getListInProjIds(@Param("list")List<Long> projIds);
}
