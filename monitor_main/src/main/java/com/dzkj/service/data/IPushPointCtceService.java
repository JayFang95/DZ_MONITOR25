package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.dataSwap.vo.PushPointCtceVO;
import com.dzkj.entity.data.PushPointCtce;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/9
 * @description 推送测点服务-中铁四局局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushPointCtceService extends IService<PushPointCtce> {

    /**
     * 查询推送点信息
     *
     * @description 查询推送点信息
     * @author jing.fang
     * @date 2023/6/7 9:20
     * @param taskId taskId
     * @return: java.util.List<com.dzkj.entity.data.PushPoint>
     **/
    List<PushPointCtce> queryList(Long taskId);

    /**
     * 新增推送点
     *
     * @description 新增推送点
     * @author jing.fang
     * @date 2023/6/7 9:31
     * @param data data
     * @return: boolean
     **/
    boolean add(PushPointCtceVO data);

    /**
     * 修改推送点
     *
     * @description 修改推送点
     * @author jing.fang
     * @date 2023/6/7 9:31
     * @param data data
     * @return: boolean
     **/
    boolean edit(PushPointCtceVO data);

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
    List<PushPointCtce> queryByMissionId(Long missionId);

}
