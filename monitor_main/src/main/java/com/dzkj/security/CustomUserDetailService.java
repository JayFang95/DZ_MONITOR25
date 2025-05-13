package com.dzkj.security;

import com.dzkj.entity.system.User;
import com.dzkj.service.system.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/5
 * @description 登录认证服务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userDetail = new CustomUserDetails();
        //查询数据库
        User user = userService.findByUsername(username);
        if(user == null){
            return userDetail;
        }
        List<String> roles = new ArrayList<>();
        //添加activiti访问角色和分组(按原有角色分组，添加activiti分组标识)
        roles.add("ROLE_ACTIVITI_USER");
        userDetail.setUserId(user.getId());
        userDetail.setUsername(user.getUsername());
        userDetail.setPassword(user.getPassword());
        userDetail.setRoles(roles);
        userDetail.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", roles)));
        return userDetail;
    }
}
