package com.dzkj.dataSwap.bean.ctce_api;

import lombok.Data;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/8 下午6:27
 * @description CtceParameter
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class CtceParameter {

    private Boolean success;
    private String errorCode;
    private String message;
    private String args;
}
