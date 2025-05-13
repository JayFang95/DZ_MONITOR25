package com.dzkj.service.alarm_setting;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.alarm_setting.AlarmItem;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 报警项服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IAlarmItemService extends IService<AlarmItem> {

    /**
     * 查询包含测点的报警项
     *
     * @description 查询包含测点的报警项
     * @author jing.fang
     * @date 2022/4/2 16:34
     * @param pid pid
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmItem>
    **/
    List<AlarmItem> getByPid(Long pid);
    /**
     * 查询包含测点的报警项
     *
     * @description 查询包含测点的报警项
     * @author jing.fang
     * @date 2022/5/12 10:38
     * @param pidList pidList
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmItem>
    **/
    List<AlarmItem> getByPidList(List<Long> pidList);
}
