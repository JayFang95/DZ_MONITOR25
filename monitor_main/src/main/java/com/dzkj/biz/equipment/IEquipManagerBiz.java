package com.dzkj.biz.equipment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.equipment.vo.ControlBoxVO;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IEquipManagerBiz {

    /**
     * 分页查询设备信息
     *
     * @description: 分页查询设备信息
     * @author: jing.fang
     * @Date: 2023/2/14 13:50
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond  cond
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.equipment.vo.ControlBoxVO>
    **/
    IPage<ControlBoxVO> getPage(Integer pageIndex, Integer pageSize, EquipCondition cond);

    /**
     * 新增设备
     *
     * @description: 新增设备
     * @author: jing.fang
     * @Date: 2023/2/14 13:50
     * @param data  data
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(ControlBoxVO data);

    /**
     * 修改设备
     *
     * @description: 修改设备
     * @author: jing.fang
     * @Date: 2023/2/14 13:50
     * @param data data
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(ControlBoxVO data);

    /**
     * 删除设备
     *
     * @description: 删除设备
     * @author: jing.fang
     * @Date: 2023/2/14 13:51
     * @param id  id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 绑定设备
     *
     * @description: 绑定设备
     * @author: jing.fang
     * @Date: 2023/2/14 13:51
     * @param id  id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil bind(Long id);

    /**
     * 解绑设备
     *
     * @description: 解绑设备
     * @author: jing.fang
     * @Date: 2023/2/14 13:51
     * @param id  id
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil unbind(Long id);

    /**
     * 查询控制器显示信息
     *
     * @description: 查询控制器显示信息
     * @author: jing.fang
     * @Date: 2023/2/20 21:24
     * @param companyId  companyId
     * @return java.lang.String
    **/
    String getControlBoxTotalInfo(Long companyId);

    /**
     * 查询控制器信息
     *
     * @description 查询控制器信息
     * @author jing.fang
     * @date 2023/3/20 15:18
     * @param id id
     * @return: com.dzkj.biz.equipment.vo.ControlBoxVO
     **/
    ControlBoxVO getControlBox(Long id);

    /**
     * 保存控制器设备信息
     *
     * @description 保存控制器设备信息
     * @author jing.fang
     * @date 2023/3/21 11:27
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil updateDeviceInfo(ControlBoxVO data);

    /**
     * 查询在线温度气压控制器列表
     *
     * @description 查询在线温度气压控制器列表
     * @author jing.fang
     * @date 2024/2/21 8:59
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.biz.equipment.vo.ControlBoxVO>
     **/
    List<ControlBoxVO> getOnlineMeteControlBoxList(Long missionId);

    /**
     * 查询控制器信息
     * @param missionId missionId
     * @param serialNo serialNo
     * @return ControlBoxVO
     */
    ControlBoxVO getControlBoxInfo(Long missionId, String serialNo);

    /**
     * 查询任务绑定控制器列表
     * @param missionId missionId
     * @return List<ControlBoxVO>
     */
    List<ControlBoxVO> getListApp(Long missionId);

    /**
     * 查询任务绑定声光报警器列表
     * @param missionId missionId
     * @return List<ControlBoxVO>
     */
    List<ControlBoxVO> getSoundControlBoxList(Long missionId);
}
