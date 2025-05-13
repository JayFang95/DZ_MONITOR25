package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.UserGroup;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 人员分组service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IUserGroupService extends IService<UserGroup> {

    /**
     * 根据userIds删除
     *
     * @description: 根据userIds删除
     * @author: jing.fang
     * @Date: 2023/2/15 16:55
     * @param userIds  userIds
     * @return boolean
    **/
    boolean removeByUserIds(List<Long> userIds);

    /**
     * 根据groupId删除
     *
     * @description: 根据groupId删除
     * @author: jing.fang
     * @Date: 2023/2/15 21:03
     * @param groupId
     * @return boolean
    **/
    boolean removeByGroupId(Long groupId);
}
