package com.dzkj.config.exception;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.dzkj.common.enums.ResponseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/3/5
 * @description 业务异常
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public class LogicException extends RuntimeException{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 日志对象
     */
    private final Logger logger = LoggerFactory.getLogger(LogicException.class);

    /**
     * 错误消息内容
     */
    protected String errMsg;
    /**
     * 错误码
     */
    protected int errCode;
    /**
     * 格式化错误码时所需参数列表
     */
    protected String[] params;

    /**
     * 记录错误返回信息
     */
    protected ResponseEnum responseEnum;

    /**
     * 获取错误消息内容 根据errCode从redis内获取未被格式化的错误消息内容 并通过String.format()方法格式化错误消息以及参数
     *
     * @return
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * 获取错误码
     *
     * @return
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     * 获取异常参数列表
     *
     * @return
     */
    public String[] getParams() {
        return params;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public void setResponseEnum(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
    }

    /**
     * 构造函数设置错误码以及错误参数列表
     *
     * @param responseEnum
     *            错误信息
     * @param params
     *            错误参数列表
     */
    public LogicException(ResponseEnum responseEnum, String... params) {
        this.responseEnum = responseEnum;
        this.errCode = responseEnum.getCode();
        this.params = params;
        String errorMassage = responseEnum.getMessage();
        // 获取格式化后的异常消息内容
        this.errMsg = ObjectUtils.isEmpty(params) ? errorMassage : MessageFormat.format(errorMassage, params);
        // 错误信息
        logger.error("系统遇到如下异常，异常码：{}>>>异常信息：{}", errCode, errMsg);
    }

    public LogicException(ResponseEnum responseEnum) {
        this.responseEnum = responseEnum;
        this.errCode = responseEnum.getCode();
        // 获取格式化后的异常消息内容
        this.errMsg = responseEnum.getMessage();
        // 错误信息
        logger.error("系统遇到如下异常，异常码：{}>>>异常信息：{}", errCode, errMsg);
    }

}
