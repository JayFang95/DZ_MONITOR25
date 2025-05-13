package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.Role;
import com.dzkj.mapper.system.RoleMapper;
import com.dzkj.service.system.IRoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Override
    public Long getSuperRoleId() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getName, "超级管理员");
        List<Role> roles = baseMapper.selectList(wrapper);
        return roles.size() > 0 ? roles.get(0).getId() : 0;
    }

    @Override
    public boolean findByName(Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(role.getId()!=null, Role::getId, role.getId())
                .eq(Role::getName, role.getName());
        return baseMapper.selectCount(wrapper) > 0;
    }
}
