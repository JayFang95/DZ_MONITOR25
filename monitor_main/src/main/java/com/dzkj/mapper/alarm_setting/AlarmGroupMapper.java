package com.dzkj.mapper.alarm_setting;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.alarm_setting.AlarmGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 报警组
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface AlarmGroupMapper extends BaseMapper<AlarmGroup> {
    List<AlarmGroup> exportGroup(@Param("projectId") Long projectId);

    /**
     * 查询告警阈值列表
     *
     * @description 查询告警阈值列表
     * @author jing.fang
     * @date 2021/10/14 11:26
     * @param projectId projectId
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmGroup>
    **/
    List<AlarmGroup> getAlarmList(@Param("projectId")Long projectId, @Param("missionId")Long missionId);
}
