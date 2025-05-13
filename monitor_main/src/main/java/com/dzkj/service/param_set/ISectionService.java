package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.param_set.Section;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 断面服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ISectionService extends IService<Section> {

    /**
     * 根据名称查询
     *
     * @description 根据名称查询
     * @author jing.fang
     * @date 2021/9/10 10:07
     * @param section section
     * @return boolean
    **/
    boolean findByName(Section section);

    /**
     * 根据任务id删除断面信息
     *
     * @description 根据任务id删除断面信息
     * @author jing.fang
     * @date 2021/9/30 9:41
     * @param missionId missionId
     * @return int
    **/
    int deleteByMission(Long missionId);

    /**
     * 根据任务id删除断面信息
     *
     * @description: 根据任务id删除断面信息
     * @author: jing.fang
     * @Date: 2023/2/16 10:36
     * @param missionIds missionIds
     * @return int
    **/
    int deleteByMissions(List<Long> missionIds);

    /**
     * 根据编组id删除
     *
     * @description: 根据编组id删除
     * @author: jing.fang
     * @Date: 2023/2/16 11:27
     * @param groupId groupId
     * @return boolean
    **/
    boolean removeByGroupId(Long groupId);
}
