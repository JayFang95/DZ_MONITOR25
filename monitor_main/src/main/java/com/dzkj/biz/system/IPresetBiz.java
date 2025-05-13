package com.dzkj.biz.system;

import com.dzkj.biz.system.vo.AlarmSettingVO;
import com.dzkj.biz.system.vo.MonitorTypeVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.AlarmSetting;
import com.dzkj.entity.system.MonitorType;
import com.dzkj.entity.system.ProjectType;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/17
 * @description 系统预置业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IPresetBiz {

    /**
     * 修改监测类型
     *
     * @description 修改监测类型
     * @author jing.fang
     * @date 2021/8/17 9:37
     * @param monitorType monitorType
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateMonitorType(MonitorType monitorType);

    /**
     * 上移/下移监测类型
     *
     * @description 上移监测类型
     * @author jing.fang
     * @date 2021/8/17 9:44
     * @param id id
     * @param toId toId
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateMonitorIndex(Long id, Long toId);

    /**
     * 保存项目类型
     *
     * @description 保存项目类型
     * @author jing.fang
     * @date 2021/8/17 17:33
     * @param projectType projectType
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil saveProject(ProjectType projectType);

    /**
     * 删除项目类型
     *
     * @description 删除项目类型
     * @author jing.fang
     * @date 2021/8/17 17:33
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil deleteProject(Long id);

    /**
     * 更新告警信息
     *
     * @description 更新告警信息
     * @author jing.fang
     * @date 2021/8/17 17:35
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateAlarm(List<AlarmSetting> list);

    /**
     * 查询监测类型一级下拉
     *
     * @description 查询监测类型一级下拉
     * @author jing.fang
     * @date 2022/3/1 10:56
     * @return java.util.List<com.dzkj.biz.system.vo.MonitorTypeVO>
    **/
    List<MonitorTypeVO> getMonitorDrop();

    /**
     * 查询监测类型二级下拉
     *
     * @description 查询监测类型二级下拉
     * @author jing.fang
     * @date 2022/3/1 10:56
     * @param
     * @return java.util.List<com.dzkj.biz.system.vo.MonitorTypeVO>
    **/
    List<MonitorTypeVO> getMonitorList();

    /**
     * 查询报警等级列表
     *
     * @description 查询报警等级列表
     * @author jing.fang
     * @date 2022/3/1 11:02
     * @return java.util.List<com.dzkj.biz.system.vo.AlarmSettingVO>
    **/
    List<AlarmSettingVO> getAlarmLevelList();
}
