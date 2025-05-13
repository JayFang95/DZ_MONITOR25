package com.dzkj.biz.vo;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/29 14:57
 * @description 文本卡片消息
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class TextCardVO {

    /**
     * 标题，不超过128个字节
    **/
    private String title;
    /**
     * 描述，不超过512个字节
    **/
    private String description;
    /**
     * 点击后跳转的链接。最长2048字节，请确保包含了协议头(http/https)
    **/
    private String url;
    /**
     * 按钮文字。 默认为“详情”， 不超过4个文字，超过自动截断。
    **/
    private String btntxt;


}
