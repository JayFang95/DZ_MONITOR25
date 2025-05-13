package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.data.JcInfo;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IJcInfoService extends IService<JcInfo> {

    /**
     * 根据项目id删除
     *
     * @description: 根据项目id删除
     * @author: jing.fang
     * @Date: 2023/2/16 9:46
     * @param projectId projectId
     * @return boolean
    **/
    boolean removeByProjectId(Long projectId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:06
     * @param missionId missionId
     * @return boolean
    **/
    boolean removeByMissionId(Long missionId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:42
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);
}
