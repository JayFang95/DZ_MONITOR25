package com.dzkj.biz.vo;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/29 14:57
 * @description 文本消息
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class TextVO {

    private String content;

    public TextVO(String content){
        this.content = content;
    }

}
