package com.dzkj.config.exception;


import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/5
 * @description 异常全局捕获
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionAdvice {


    /**
     * 通用异常处理
     *
     * @param e 通用异常
     * @return 通用异常返回
     */
    @ExceptionHandler(Exception.class)
    public ResponseUtil commonException(Exception e) {
        log.error("系统发生异常=============>{}", e.getMessage());
        e.printStackTrace();
        return new ResponseUtil(ResponseEnum.FAIL);
    }

    /**
     * 通用异常处理
     *
     * @param e 通用异常
     * @return 通用异常返回
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseUtil securityException(AccessDeniedException e) {
        log.error("系统发生异常=============>{}", e.getMessage());
        e.printStackTrace();
        return new ResponseUtil(ResponseEnum.ACCESS_DENIED_ERROR);
    }

    /**
     * 处理业务逻辑异常
     *
     * @param e 业务逻辑异常对象实例
     * @return 逻辑异常消息内容
     */
    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler(LogicException.class)
    public ResponseUtil logicException(LogicException e) {
        log.error("业务处理异常=============>{}", e.getErrMsg());
        return ResponseUtil.failure(e.getErrCode(), e.getErrMsg());
    }

    /**
     * 实体校验异常处理
     *
     * @param e 异常
     * @return 格式化后的异常信息
     */
    @ExceptionHandler(BindException.class)
    public ResponseUtil propertyException(BindException e) {
        List<ObjectError> errors = e.getAllErrors();
        List<String> messages = new ArrayList<>();
        errors.forEach(error -> {
            if (error instanceof FieldError) {
                messages.add(((FieldError) error).getField() + error.getDefaultMessage());
            } else {
                messages.add(error.getDefaultMessage());
            }
            log.error("参数检查异常=============>{}", messages.get(messages.size() - 1));
        });
        return new ResponseUtil(ResponseEnum.PARAMETER_ERROR.getCode(), messages.get(0), false);
    }

    /**
     * 参数校验异常处理
     *
     * @param e 异常
     * @return 参数异常信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseUtil illegalException(IllegalArgumentException e) {
        log.error("参数校验异常=============>{}", e.getMessage());
        return ResponseUtil.failure(ResponseEnum.PARAMETER_ERROR);
    }


}
