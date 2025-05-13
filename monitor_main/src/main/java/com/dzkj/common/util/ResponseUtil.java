package com.dzkj.common.util;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dzkj.common.enums.ResponseEnum;
import lombok.Data;

import java.text.MessageFormat;

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
@Data
public class ResponseUtil<T> {

    // "响应码：200-成功"
    private Integer code;

    // "响应消息"
    private String message;

    // "返回对象"
    private T data;

    // "请求是否成功"
    private Boolean success = true;

    public ResponseUtil() {}

    public ResponseUtil(Integer code, String message, Boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    public ResponseUtil(ResponseEnum enums){
        this.code = enums.getCode();
        this.message = enums.getMessage();
        if(enums.getCode()!=200){
            this.success = false;
        }
    }

    public ResponseUtil(ResponseEnum enums, T data) {
        this.code = enums.getCode();
        this.message = enums.getMessage();
        this.data = data;
        if (enums.getCode() != 200) {
            this.success = false;
        }
    }

    /**
     * 返回成功，无数据
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> success(){
        return new ResponseUtil<>(ResponseEnum.SUCCESS);
    }

    /**
     * 返回成功数据
     * @param data 数据
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> success(T data){
        return new ResponseUtil<>(ResponseEnum.SUCCESS,data);
    }

    /**
     * 返回失败，无数据
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> failure(){
        return new ResponseUtil<>(ResponseEnum.FAIL);
    }


    /**
     * 返回失败，有数据
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> failure(T data){
        return new ResponseUtil<>(ResponseEnum.FAIL,data);
    }

    /**
     * 返回失败，指定错误类型
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> failure(ResponseEnum responseEnum){
        return new ResponseUtil<>(responseEnum);
    }

    /**
     * 返回失败，指定code 和错误信息
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseUtil<T> failure(Integer code,String message){
        return new ResponseUtil<>(code,message,false);
    }

    /**
     * 返回失败，指定错误类型和数据
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> failure(ResponseEnum responseEnum, T data){
        return new ResponseUtil<>(responseEnum,data);
    }

    /**
     * 自定义失败返回结果，用于参数异常失败时返回
     * @param messages 信息
     * @return 指定格式数据
     */
    public static <T> ResponseUtil<T> parameterError(String messages){
        String errorMassage = ResponseEnum.PARAMETER_ERROR.getMessage();
        String errMsg = ObjectUtils.isEmpty(messages) ? errorMassage : MessageFormat.format(errorMassage, messages);
        return new ResponseUtil<>(ResponseEnum.PARAMETER_ERROR.getCode(), errMsg, false);
    }


}
