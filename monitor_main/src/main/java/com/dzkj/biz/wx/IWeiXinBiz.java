package com.dzkj.biz.wx;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/5/26
 * @description 微信业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IWeiXinBiz {

    /**
     * 微信token验证
     *
     * @description 微信token验证
     * @author jing.fang
     * @date 2021/5/26 9:00
     * @param signature signature
     * @param timestamp timestamp
     * @param nonce nonce
     * @param echostr echostr
     * @return boolean
    **/
    boolean check(String signature, String timestamp, String nonce, String echostr);
}
