package com.dzkj.service.equipment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.equipment.vo.ControlBoxVO;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.entity.equipment.ControlBox;
import com.dzkj.entity.survey.RobotSurveyRecord;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IControlBoxService extends IService<ControlBox> {

    /**
     * 分页查询设备列表
     *
     * @description: 分页查询设备列表
     * @author: jing.fang
     * @Date: 2023/2/14 14:21
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond  cond
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.dzkj.entity.equipment.ControlBox>
    **/
    Page getPage(Integer pageIndex, Integer pageSize, EquipCondition cond);

    /**
     * 列表查询
     * @param cond cond
     * @return List<ControlBox>
     */
    List<ControlBox> getList(EquipCondition cond);

    /**
     * 查询监测任务下是否存在指定序列号设备
     *
     * @description: 查询监测任务下是否存在指定序列号设备
     * @author: jing.fang
     * @Date: 2023/2/15 10:32
     * @param data data
     * @return boolean
     **/
    boolean findSerialNoByMission(ControlBoxVO data);

    /**
     * 设备绑定/解绑
     *
     * @description: 设备绑定/解绑
     * @author: jing.fang
     * @Date: 2023/2/14 14:06
     * @param id id
     * @param bindStatus  bindStatus
     * @return boolean
    **/
    boolean updateBindStatus(Long id, boolean bindStatus);

    /**
     * 统计绑定设备任务数
     *
     * @description: 统计绑定设备任务数
     * @author: jing.fang
     * @Date: 2023/2/14 14:15
     * @param id  id
     * @return int
    **/
    int countBindSerialNo(Long id);

    /**
     * 根据项目id删除
     *
     * @description: 根据项目id删除
     * @author: jing.fang
     * @Date: 2023/2/16 9:44
     * @param projectId projectId
     * @return boolean
    **/
    boolean removeByProjectId(Long projectId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:46
     * @param missionId missionId
     * @return boolean
    **/
    boolean removeByMissionId(Long missionId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:46
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);

    /**
     * 跟新控制仪器信息
     * @param data data
     * @return boolean
     */
    boolean updateDeviceInfo(ControlBoxVO data);

    /**
     * 获取任务关联控制器编号
     * @param missionId missionId
     * @return List<String>
     */
    List<String> getSerialNoList(Long missionId);

    /**
     * 查询测量记录信息
     * @param controlBoxId controlBoxId
     * @return RobotSurveyRecord
     */
    RobotSurveyRecord getSurveyRecordInfoByControlBoxId(Long controlBoxId);

    /**
     * 更新测量状态
     * @param id id
     * @param survey survey
     */
    void updateSurvey(Long id, int survey);

    /**
     * 查询任务绑定温度气压控制器集合
     * @param missionId missionId
     * @return list
     */
    List<ControlBox> getMeteBoxListByMissionId(Long missionId);

    /**
     * 查询控制器
     * @param missionId missionId
     * @param serialNo serialNo
     * @return List
     */
    List<ControlBox> getControlBoxInfo(Long missionId, String serialNo);

    /**
     * 获取任务下绑定的控制器集合
     * @param missionId missionId
     * @return List<ControlBox>
     */
    List<ControlBox> getBindListByMissionId(Long missionId);

    /**
     * 获取任务下绑定的声光控制器集合
     * @param missionId missionId
     * @return List<ControlBox>
     */
    List<ControlBox> getSoundControlBoxList(Long missionId);
}
