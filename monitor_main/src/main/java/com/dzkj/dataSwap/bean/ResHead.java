package com.dzkj.dataSwap.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/1 21:02
 * @description 响应对象头部信息对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class ResHead {

    /**
     * 响应结果码： 0 - 成功； 其余失败
     * 1 appId不存在；2 参数错误；3 签名错误；4 项目无访问权限；5 解密错误；6 访问地址错误；10	数据格式错误
     * 11 初始数据未上报；12 未携带原始数据；13 token错误；99	内部错误
     */
    private int result;
    /**
     * 对应响应结果原因描述
     */
    private String reason;

    public ResHead() {
    }

    public ResHead(int result, String reason) {
        this.result = result;
        this.reason = reason;
    }


}
