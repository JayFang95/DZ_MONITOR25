package com.dzkj.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.biz.vo.*;
import com.dzkj.common.constant.AuthConstant;
import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.common.util.*;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.entity.system.Company;
import com.dzkj.entity.system.Online;
import com.dzkj.entity.system.Resource;
import com.dzkj.entity.system.User;
import com.dzkj.security.SmsCodeAuthenticationToken;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IOnlineService;
import com.dzkj.service.system.IResourceService;
import com.dzkj.service.system.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 认证授权接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
@Slf4j
public class AuthBizImpl implements IAuthBiz {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IOnlineService onlineService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseUtil login(LoginInfo info) {
        String type = info.getType();
        if(StringUtils.isEmpty(type)){
            return ResponseUtil.failure(ResponseEnum.PARAMETER_ERROR);
        }
        if(AuthConstant.USERNAME_PASSWORD.equals(type)){
            String username = info.getUsername();
            String password = info.getPassword();
            if (StringUtils.isAnyEmpty(username, password)) {
                return ResponseUtil.failure(ResponseEnum.LOGIN_PARAM_EMPTY);
            }
            try {
                UsernamePasswordAuthenticationToken upToken =
                        new UsernamePasswordAuthenticationToken(username, password);
                Authentication authentication = authenticationManager.authenticate(upToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InternalAuthenticationServiceException | BadCredentialsException e) {
                return ResponseUtil.failure(ResponseEnum.LOGIN_PARAM_ERROR);
            }
        }else {
            String phone = info.getPhone();
            String captcha = info.getCaptcha();
            String captchaRedis = (String) redisTemplate.opsForValue().get(RedisConstant.PREFIX + phone);
            if(StringUtils.isEmpty(captchaRedis)){
                return ResponseUtil.failure(ResponseEnum.CAPTCHA_PARAM_EXPIRE);
            }
            if(!captchaRedis.equals(captcha)){
                return ResponseUtil.failure(ResponseEnum.CAPTCHA_PARAM_ERROR);
            }
            User user = userService.findByPhone(phone);
            if(user==null){
                return ResponseUtil.failure(ResponseEnum.PHONE_PARAM_ERROR);
            }
            info.setUsername(user.getUsername());
            try {
                SmsCodeAuthenticationToken token = new SmsCodeAuthenticationToken(user.getUsername());
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                e.printStackTrace();
                return ResponseUtil.failure(ResponseEnum.LOGIN_PARAM_ERROR);
            }
        }
        //查询用户信息
        User user = userService.findByUsername(info.getUsername());
        //验证公司是否已终止服务
        if(!isActive(user.getCompanyId())){
            return ResponseUtil.failure(ResponseEnum.COMPANY_DENIED_ERROR);
        }
        //验证账好是否已经登录，如果已经登录账户接收提示
        listenSameUser(user);
        //登录成功获取响应数据
        LoginResponse response = createLoginResponse(user);
        return ResponseUtil.success(response);
    }

    private boolean isActive(Long companyId) {
        if(companyId==0){
            return true;
        }
        Company company = companyService.getById(companyId);
        return company != null && company.getActive();
    }

    private void listenSameUser(User user){
        LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Online::getUsername, user.getUsername());
        List<Online> list = onlineService.list(wrapper);
        List<Long> onlineIds = new ArrayList<>();
        int count = 0 ;
        for (Online online : list) {
            if (StringUtils.isNotEmpty(online.getToken()) && !TokenUtil.isTokenExpired(online.getToken())){
                ++count;
            }else {
                onlineIds.add(online.getId());
            }
        }
        if (onlineIds.size() > 0){
            onlineService.removeByIds(onlineIds);
        }
        if(count > 0){
            MessageVO msg = new MessageVO();
            msg.setMsg(SocketMsgConst.LOGIN.getMessage()).setCode(SocketMsgConst.LOGIN.getCode());
            try {
                WebSocketServer.sendInfoTo(msg, user.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LoginResponse createLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        String username =  user.getUsername();
        String token = TokenUtil.generateToken(username);
        String refreshToken = TokenUtil.generateRefreshToken(username);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        redisTemplate.opsForSet().add(RedisConstant.PREFIX + username, token);
        redisTemplate.opsForValue().set(RedisConstant.PREFIX + username + "_" + token + "_time", new Date(), TokenUtil.getSessionTime(), TimeUnit.SECONDS);
        //设置用户信息
        user.setPassword(null);
        response.setUser(DzBeanUtils.propertiesCopy(user, UserBO.class));
        Company company = companyService.getById(user.getCompanyId());
        response.getUser().setFunctionConfig(company == null ? "" : company.getFunctionConfig());
        //获取用户所有资源
        List<Resource> resources = resourceService.findByRoleId(user.getRoleId());
        //保存用户访问权限
        String menuKey = RedisConstant.PREFIX + username + "_menu";
        redisTemplate.opsForList().getOperations().expire(menuKey, 0, TimeUnit.MILLISECONDS);
        List<Resource> list = resources.stream()
                .filter(resource -> StringUtils.isNotEmpty(resource.getUrl())
                        && StringUtils.isNotEmpty(resource.getMethod()))
                .collect(Collectors.toList());
        List<Permission> permissions = DzBeanUtils.listCopy(list, Permission.class);
        permissions.forEach(permission -> redisTemplate.opsForList().leftPush(menuKey, permission));
        //获取用户菜单信息
        List<MenuVO> menus = getMenus(resources);
        for (MenuVO menu : menus) {
            if("项目管理".equals(menu.getName())){
                List<MenuVO> children = menu.getChildren();
                if(children!=null && children.size()>0){
                    List<MenuVO> collect = children.stream().filter(menuVO -> "项目信息".equals(menuVO.getName())).collect(Collectors.toList());
                    menu.setChildren(collect);
                }
            }
        }
        response.setMenus(menus);
        response.setSysMenus(getSysMenus(resources));
        //新增用户在线信息
        addOnline(user, token);
        return response;
    }

    private void addOnline(User user, String token) {
        Online online = new Online();
        Long companyId = user.getCompanyId();
        if(companyId==0){
            companyId = companyService.getCurrentCompany();
        }
        online.setUsername(user.getUsername())
                .setToken(token)
                .setCompanyId(companyId)
                .setIp(IPUtil.getIpAdd());
        onlineService.save(online);
        List<Long> userIds = onlineService.getOnlineUserIds(companyId);
        userIds = userIds.stream().filter(id -> !user.getId().equals(id)).distinct().collect(Collectors.toList());
        if (userIds.size() == 0){return;}
        try {
            String message = "用户:" + online.getUsername() +"-ip:" +online.getIp()+"上线";
            MessageVO vo = new MessageVO(SocketMsgConst.ONLINE.getCode(), message);
            for (Long userId : userIds) {
                WebSocketServer.sendInfoTo(vo, userId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<MenuVO> getSysMenus(List<Resource> resources) {
        List<Resource> sysResources = resourceService.getMenus(new ResourceCondition());
        List<MenuVO> menus = DzBeanUtils.listCopy(sysResources, MenuVO.class);
        List<Long> ids = resources.stream().map(Resource::getId).collect(Collectors.toList());
        ArrayList<MenuVO> list = new ArrayList<>();
        // 获取所有一级目录
        menus.forEach(menuVO -> {
            if (menuVO.getPid() == null || menuVO.getPid() == 0) {
                list.add(menuVO);
            }
        });
        findChildrenMenu(menus, list, ids);
        return list;
    }

    private void findChildrenMenu(List<MenuVO> menus, List<MenuVO> userMenus, List<Long> ids){
        for (MenuVO menuVO : userMenus) {
            ArrayList<MenuVO> children = new ArrayList<>();
            menus.forEach(menu -> {
                if (menuVO.getId() != null && menuVO.getId().equals(menu.getPid())) {
                    if (!exists(children, menu)) {
                        menu.setVisible(ids.contains(menu.getId()));
                        children.add(menu);
                    }
                }
            });
            menuVO.setChildren(children);
            findChildrenMenu(menus, children, ids);
        }
    }

    private boolean exists(List<MenuVO> list, MenuVO menuVo) {
        boolean exist = false;
        for (MenuVO menu : list) {
            if (menu.getId().equals(menuVo.getId())) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    private List<MenuVO> getMenus(List<Resource> resources) {
        List<String> strings = Arrays.asList("普通菜单", "消息菜单");
        List<Resource> collect = resources.stream()
                .filter(resource -> strings.contains(resource.getType()))
                .collect(Collectors.toList());
        List<MenuVO> menus = DzBeanUtils.listCopy(collect, MenuVO.class);
        List<MenuVO> list = menus.stream()
                .filter(menuVO -> menuVO.getPid() == null || menuVO.getPid() == 0)
                .collect(Collectors.toList());
        for (MenuVO vo : list) {
            ArrayList<MenuVO> children = new ArrayList<>();
            for (MenuVO menu : menus) {
                if(vo.getId().equals(menu.getPid())){
                    children.add(menu);
                }
            }
            if(children.size()>0){
                vo.setChildren(children);
            }
        }
        for (MenuVO vo : list) {
            if (vo.getChildren() != null && vo.getChildren().size() > 0 ){
                for (MenuVO child : vo.getChildren()) {
                    ArrayList<MenuVO> children2 = new ArrayList<>();
                    for (MenuVO menu : menus) {
                        if(child.getId().equals(menu.getPid())){
                            children2.add(menu);
                        }
                    }
                    if(children2.size()>0){
                        child.setChildren(children2);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public ResponseUtil getCaptcha(String phone) {
        // 验证手机号是否注册
        User user = userService.findByPhone(phone);
        if(user==null){
            return ResponseUtil.failure(500, "手机号未注册，请重试");
        }
        String redisCode = (String) redisTemplate.opsForValue().get(RedisConstant.PREFIX + phone);
        if(redisCode!=null){
            return ResponseUtil.failure(500, "验证码已经发送，请稍后重试");
        }
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int nextInt = random.nextInt(10);
            builder.append(nextInt);
        }
        String captchaCode = builder.toString();
        // 送短信通知
        SmsUtil.sendCode(captchaCode, phone);
        // 保存到redis 时效一分钟
        redisTemplate.opsForValue().set(RedisConstant.PREFIX + phone, captchaCode, 60, TimeUnit.SECONDS);
        return ResponseUtil.success();
    }

    @Override
    public ResponseUtil refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldToken = request.getHeader(TokenUtil.getHeader());
        log.info("oldToken: " + oldToken);
        String refreshToken = request.getHeader("refresh_token");
        String token = TokenUtil.refreshToken(refreshToken);
        if(token==null){
            return ResponseUtil.success(null);
        }
        if(StringUtils.isNoneEmpty(refreshToken) && StringUtils.countMatches(refreshToken, ".") == 2){
            try {
                String authorization = StringUtils.substringBetween(refreshToken, ".");
                String decoded = new String(Base64.decodeBase64(authorization));
                Map properties  = new ObjectMapper().readValue(decoded, Map.class);
                //获取用户信息,重置权限缓存
                String username = (String) properties.get("sub");
                redisTemplate.delete(RedisConstant.PREFIX + username + "_" + oldToken + "_time");
                redisTemplate.opsForValue().set(RedisConstant.PREFIX + username + "_" + token + "_time", new Date(), TokenUtil.getSessionTime(), TimeUnit.SECONDS);
                redisTemplate.opsForSet().remove(RedisConstant.PREFIX + username, oldToken);
                redisTemplate.opsForSet().add(RedisConstant.PREFIX + username, token);
                LambdaUpdateWrapper<Online> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(Online::getUsername, username).eq(Online::getToken, oldToken)
                        .set(Online::getToken, token);
                onlineService.update(wrapper);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("refreshToken",refreshToken);
        return ResponseUtil.success(map);
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        String token = request.getHeader(TokenUtil.getHeader());
        if(StringUtils.isNotEmpty(token) && (StringUtils.countMatches(token, ".") == 2)) {
            // 用户下线，更新在线表
            LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Online::getToken, token);
            Online online = onlineService.getOne(wrapper);
            onlineService.remove(wrapper);
            try {
                String authorization = StringUtils.substringBetween(token, ".");
                String decoded = new String(Base64.decodeBase64(authorization));
                Map properties  = new ObjectMapper().readValue(decoded, Map.class);
                String username = (String) properties.get("sub");
                // 清除redis记录
                LambdaQueryWrapper<Online> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Online::getUsername, username).ne(Online::getToken, token);
                if (onlineService.count(queryWrapper) == 0){
                    String menuKey = RedisConstant.PREFIX + username + "_menu";
                    redisTemplate.opsForList().getOperations().expire(menuKey, 0, TimeUnit.MILLISECONDS);
                }
                redisTemplate.delete(RedisConstant.PREFIX + username + "_" + token + "_time");
                redisTemplate.opsForSet().remove(RedisConstant.PREFIX + username, token);
                // 下线通知
                ThreadPoolUtil.getPool().execute(() -> listenLogout(online));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void listenLogout(Online online) {
        if (online == null){
            return;
        }
        String message = "用户:" + online.getUsername() +"-ip:" +online.getIp()+"下线";
        // 发送下线通知
        MessageVO vo = new MessageVO(SocketMsgConst.OUTLINE.getCode(), message);
        try {
            Long companyId = online.getCompanyId();
            if (companyId == 0){
                companyId = companyService.getCurrentCompany();
            }
            List<Long> userIds = onlineService.getOnlineUserIds(companyId);
            Thread.sleep(1000);
            for (Long userId : userIds) {
                WebSocketServer.sendInfoTo(vo, userId);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        User user = userService.findByUsername(online.getUsername());
        if (user == null){
            return;
        }
        LambdaQueryWrapper<Online> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Online::getUsername, user.getUsername());
        int count = onlineService.count(wrapper);
        if(count>0){
            MessageVO msg = new MessageVO();
            msg.setMsg(SocketMsgConst.LOGOUT.getMessage()).setCode(SocketMsgConst.LOGOUT.getCode());
            try {
                WebSocketServer.sendInfoTo(msg, user.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
