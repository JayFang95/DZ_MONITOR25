package com.dzkj.common.annotation;

import java.lang.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysOperateLog {

    // 请求名称
    String value() default "";
    // 请求类型
    String type() default "";
    // 模块名称
    String modelName() default "";

}
