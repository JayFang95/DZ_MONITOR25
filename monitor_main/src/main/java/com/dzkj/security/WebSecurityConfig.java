package com.dzkj.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description security安全配置
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailService userDetailService;
    @Autowired
    private CustomLogoutHandler logoutHandler;
    @Autowired
    private CustomAuthenticationTokenFilter authenticationTokenFilter;

    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring容器没有管理AuthenticationManager,需要注入
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //设置自定义认证服务
        auth.userDetailsService(userDetailService).passwordEncoder(getBCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //禁止跨站防御攻击
        http.csrf().disable();
        //禁止session 每次请求都需要认证，因为springContext上下文不会存储认证信息
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //访问设置
        http.authorizeRequests()
                //配置不拦截请求
                .antMatchers("/mt/auth/**","/mt/common/**","/mt/dict/**","/mt/wx/**","/websocket/**")
                .permitAll()
                //自定义认证授权服务
                .anyRequest().access("@authAccessService.hasPermission(request,authentication)");
        //退出
//        http.logout().logoutUrl("/mt/auth/logout").logoutSuccessHandler(logoutHandler);
        //设置认证过滤器(需要在用户名密码验证之前)
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 获取验证码提供者
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailService);
        // 将短信验证码校验器注册到 HttpSecurity
        http.authenticationProvider(smsCodeAuthenticationProvider);
    }
}
