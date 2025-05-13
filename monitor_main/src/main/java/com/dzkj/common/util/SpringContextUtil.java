package com.dzkj.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获取指定容器对象
     *
     * @description: 获取指定容器对象
     * @author: jing.fang
     * @Date: 2023/2/16 16:48
     * @param beanName beanName
     * @return T
    **/
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName){
        return (T) context.getBean(beanName);
    }

    /**
     * 获取指定容器对象
     *
     * @description: 获取指定容器对象
     * @author: jing.fang
     * @Date: 2023/2/16 16:48
     * @param clz clz
     * @return T
     **/
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return (T)context.getBean(clz);
    }

}
