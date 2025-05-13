package com.dzkj.service.project;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.project.CustomDisplay;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/12
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ICustomDisplayService extends IService<CustomDisplay> {

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:08
     * @param missionId missionId
     * @return boolean
    **/
    boolean removeByMissionId(Long missionId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:33
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);
}
