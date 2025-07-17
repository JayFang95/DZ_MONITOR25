package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.data.PushPointCtce;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/9
 * @description 四局推送点mapper
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface PushPointCtceMapper extends BaseMapper<PushPointCtce> {
    /**
     * 根据推送任务查询
     *
     * @description 根据推送任务查询
     * @author jing.fang
     * @date 2025/3/10 上午10:32
     * @param taskId taskId
     * @return: java.util.List<com.dzkj.entity.data.PushPointJn>
     **/
    List<PushPointCtce> queryList(@Param("taskId") Long taskId);

    /**
     * 查询根据任务id
     *
     * @description 查询根据任务id
     * @author jing.fang
     * @date 2025/3/10 上午10:21
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.data.PushPointJn>
     **/
    List<PushPointCtce> queryByMissionId(@Param("missionId") Long missionId);;
}
