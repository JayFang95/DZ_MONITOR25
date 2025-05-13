package com.dzkj.dataSwap.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 14:15
 * @description 数据交换线下提供对接属性
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@Component
@ConfigurationProperties(prefix = "data-swap")
public class DataSwapConst {

//    /**
//     * 访问方标识id
//     */
//    private String appId;
//    /**
//     * 访问方标识key
//     */
//    private String appKey;
//    /**
//     * RSA公钥： base64字符串的格式给出
//     */
//    private String publicKey;
//    /**
//     * 上海铁路系统对接请求地址
//     */
//    private String stdUrl;
    /**
     * 我们系统对接请求地址
     */
    private String dzUrl;

}
