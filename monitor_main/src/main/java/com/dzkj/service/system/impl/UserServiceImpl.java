package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.entity.system.User;
import com.dzkj.mapper.system.UserMapper;
import com.dzkj.service.system.IUserService;
import org.apache.commons.lang3.StringUtils;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public User findByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<UserVO> getList(Long companyId) {
        return baseMapper.getList(companyId);
    }

    @Override
    public List<UserVO> getListUser(Long companyId, Long groupId) {
        return baseMapper.getListUser(companyId, groupId);
    }

    @Override
    public boolean checkNameOrPhone(Long id, String username, String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(id!=null, User::getId, id)
                .eq(StringUtils.isNotEmpty(username), User::getUsername, username)
                .eq(StringUtils.isNotEmpty(phone), User::getPhone, phone);
        return baseMapper.selectCount(wrapper)>0;
    }

    @Override
    public List<UserVO> getSuperList() {
        return baseMapper.getSuperList();
    }

    @Override
    public List<User> listByGroupIds(List<Long> groupIds) {
        return baseMapper.listByGroupIds(groupIds);
    }

    @Override
    public List<User> findByCompanyId(Long companyId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getCompanyId, companyId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public int countSuper() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getCompanyId, 0);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    public List<User> findByIdAndPhone(User user) {
        return baseMapper.findByIdAndPhone(user);
    }
}
