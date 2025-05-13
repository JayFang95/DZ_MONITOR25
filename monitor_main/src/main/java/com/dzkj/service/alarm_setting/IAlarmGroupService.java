package com.dzkj.service.alarm_setting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.alarm_setting.AlarmGroup;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 报警组服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IAlarmGroupService extends IService<AlarmGroup> {

    /**
     * 查询告警阈值分组信息
     *
     * @description 查询告警阈值分组信息
     * @author jing.fang
     * @date 2021/10/14 11:22
     * @param projectId projectId
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmGroup>
    **/
    List<AlarmGroup> getAlarmList(Long projectId, Long missionId);
}
