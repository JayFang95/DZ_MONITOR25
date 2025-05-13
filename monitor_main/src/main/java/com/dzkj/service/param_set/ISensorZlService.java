package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.param_set.vo.SensorZlVO;
import com.dzkj.entity.param_set.SensorZl;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ISensorZlService extends IService<SensorZl> {

    /**
     * 获取编组下所有测点传感器信息
     *
     * @description 获取编组下所有测点传感器信息
     * @author jing.fang
     * @date 2022/3/29 15:50
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.param_set.SensorZl>
    **/
    List<SensorZl> getListByMissionId(Long missionId);

    /**
     * 根据项目id删除
     *
     * @description: 根据项目id删除
     * @author: jing.fang
     * @Date: 2023/2/16 9:41
     * @param projectId projectId
     * @return boolean
    **/
    boolean removeByProjectId(Long projectId);

    /**
     * 根据测点id删除
     *
     * @description: 根据测点id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:04
     * @param ptIds  ptIds
     * @return boolean
    **/
    boolean removeByPointIds(List<Long> ptIds);

    /**
     * 名称唯一验证
     *
     * @description: 名称唯一验证
     * @author: jing.fang
     * @Date: 2023/2/16 11:37
     * @param data data
     * @return boolean
    **/
    boolean checkName(SensorZlVO data);
}
