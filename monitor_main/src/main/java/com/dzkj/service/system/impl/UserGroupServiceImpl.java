package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.mapper.system.UserGroupMapper;
import com.dzkj.service.system.IUserGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
  * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
  *
  * @author jing.fang
  * @date 2021/8/2
  * @description note
  * history
  * <author>    <time>    <version>    <desc>
  *  作者姓名     修改时间     版本号        描述
  */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements IUserGroupService {

    @Override
    public boolean removeByUserIds(List<Long> userIds) {
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserGroup::getUserId, userIds);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean removeByGroupId(Long groupId) {
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroup::getGroupId, groupId);
        return baseMapper.delete(wrapper) > 0;
    }
}
