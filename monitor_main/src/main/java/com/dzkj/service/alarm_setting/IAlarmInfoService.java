package com.dzkj.service.alarm_setting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.alarm_setting.AlarmInfo;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IAlarmInfoService extends IService<AlarmInfo> {

    /**
     * 获取项目最大报警等级
     */
    List<AlarmInfo> getMaxLevel(List<Long> projectIds);

    /**
     * 根据任务id查询
     *
     * @description: 根据任务id查询
     * @author: jing.fang
     * @Date: 2023/2/16 10:57 
     * @param missionIds missionIds
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmInfo>
    **/
    List<AlarmInfo> listByMissionIds(List<Long> missionIds);

    /**
     * 删除临时加测数据
     * @param recycleNum recycleNum
     * @param missionId missionId
     * @return boolean
     */
    boolean removeOnceData(int recycleNum, long missionId);
}
