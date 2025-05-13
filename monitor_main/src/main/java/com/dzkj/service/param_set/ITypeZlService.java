package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.param_set.vo.TypeZlVO;
import com.dzkj.entity.param_set.TypeZl;

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
public interface ITypeZlService extends IService<TypeZl> {

    /**
     * 根据项目id删除
     *
     * @description: 根据项目id删除
     * @author: jing.fang
     * @Date: 2023/2/16 9:40
     * @param projectId projectId
     * @return boolean
    **/
    boolean removeByProjectId(Long projectId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:03
     * @param missionId missionId
     * @return boolean
    **/
    boolean removeByMissionId(Long missionId);

    /**
     * 根据任务id删除
     *
     * @description: 根据任务id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:41
     * @param missionIds missionIds
     * @return boolean
    **/
    boolean removeByMissionIds(List<Long> missionIds);

    /**
     * 名称存在验证
     *
     * @description: 名称存在验证
     * @author: jing.fang
     * @Date: 2023/2/16 11:35
     * @param data data
     * @return boolean
    **/
    boolean checkName(TypeZlVO data);
}
