package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.entity.data.PushPoint;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送测点服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushPointService extends IService<PushPoint> {

    /**
     * 查询推送点信息
     *
     * @description 查询推送点信息
     * @author jing.fang
     * @date 2023/6/7 9:20
     * @param taskId taskId
     * @return: java.util.List<com.dzkj.entity.data.PushPoint>
     **/
    List<PushPoint> queryList(Long taskId);

    /**
     * 新增推送点
     *
     * @description 新增推送点
     * @author jing.fang
     * @date 2023/6/7 9:31
     * @param data data
     * @return: boolean
     **/
    boolean add(PushPointVO data);

    /**
     * 修改推送点
     *
     * @description 修改推送点
     * @author jing.fang
     * @date 2023/6/7 9:31
     * @param data data
     * @return: boolean
     **/
    boolean edit(PushPointVO data);

    /**
     * 删除推送点
     *
     * @description 删除推送点
     * @author jing.fang
     * @date 2023/6/7 9:32
     * @param id id
     * @return: boolean
     **/
    boolean delete(Long id);

    /**
     * 查询不同任务中是否已经存在仪器编号
     *
     * @description 查询不同任务中是否已经存在仪器编号
     * @author jing.fang
     * @date 2023/6/7 16:17
     * @param data data
     * @return: boolean
     **/
    boolean findSameCodeNotInTask(PushPointVO data);

    /**
     * 查询设备编号和设备类型在任务中是否一致
     *
     * @description 查询设备编号和设备类型在任务中是否一致
     * @author jing.fang
     * @date 2023/6/7 16:28
     * @param data data
     * @return: boolean
     **/
    boolean findCodeAndDeviceCategoryNotSame(PushPointVO data);

    /**
     * 删除推送点详情
     *
     * @description 删除推送点详情
     * @author jing.fang
     * @date 2023/6/7 21:24
     * @param pushTaskId pushTaskId
     * @return: boolean
     **/
    boolean removeByTaskId(Long pushTaskId);

    /**
     * 根据任务id查询可推送测点集合
     *
     * @description 根据任务id查询可推送测点集合
     * @author jing.fang
     * @date 2023/6/9 14:15
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.data.PushPoint>
     **/
    List<PushPoint> queryByMissionId(Long missionId);
}
