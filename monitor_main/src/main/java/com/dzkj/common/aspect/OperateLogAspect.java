package com.dzkj.common.aspect;

import com.dzkj.biz.vo.LoginInfo;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.util.IPUtil;
import com.dzkj.common.util.TokenUtil;
import com.dzkj.entity.system.OperateLog;
import com.dzkj.entity.system.User;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IOperateLogService;
import com.dzkj.service.system.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 日志操作切面
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Aspect
@Component
@Slf4j
public class OperateLogAspect {

    @Autowired
    private IOperateLogService logService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICompanyService companyService;

    @Pointcut("@annotation(com.dzkj.common.annotation.SysOperateLog)")
    public void operateLog(){

    }

    /**
     * doOperationAroundLog获取注解参数，记录日志
     */
    @Before("operateLog()")
    public void doOperateLog(JoinPoint pjp) throws Throwable {
        // 用来获取用户信息
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())
        ).getRequest();

        // IP
        String clientIp = IPUtil.getIpAdd(request);
        String clientName = "匿名操作";
        if(getUsername(request)!=null){
            clientName = getUsername(request);
        }
        // 获取注解对象
        String methodName = pjp.getSignature().getName();
        Class<?> classTarget = pjp.getTarget().getClass();
        Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        try {
            Method objMethod = classTarget.getMethod(methodName, par);
            String value = objMethod.getAnnotation(SysOperateLog.class).value();
            String type = objMethod.getAnnotation(SysOperateLog.class).type();
            String modelName = objMethod.getAnnotation(SysOperateLog.class).modelName();
            if("login".equals(methodName)){
                Object[] args = pjp.getArgs();
                LoginInfo loginInfoVO = (LoginInfo)args[0];
                clientName = loginInfoVO.getUsername();
            }
            //查询用户所属公司
            User user = userService.findByUsername(clientName);
            Long companyId = -1L;
            if(user!=null && user.getCompanyId()!=null){
                if (0==user.getCompanyId()){
                    companyId = companyService.getCurrentCompany();
                }else {
                    companyId = user.getCompanyId();
                }
                clientName = user.getName();
            }
            //记录日志
            OperateLog operateLog = new OperateLog();
            operateLog.setIp(clientIp);
            operateLog.setCompanyId(companyId);
            operateLog.setOperator(clientName);
            operateLog.setContent(modelName + "/"+value);
            logService.save(operateLog);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            log.info("遇到错误："+e.getMessage());
            throw e;
        }
    }

    private String getUsername(HttpServletRequest request){
        String token = request.getHeader(TokenUtil.getHeader());
        if(StringUtils.isNotEmpty(token) && StringUtils.countMatches(token, ".")==2){
            String authorization2 = StringUtils.substringBetween(token, ".");
            String decoded = new String(Base64.decodeBase64(authorization2));
            Map properties;
            try {
                properties = new ObjectMapper().readValue(decoded, Map.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            //获取用户名
            return (String) properties.get("sub");
        }
        return null;
    }

}
