package com.dzkj.biz.wx.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/5/26
 * @description 微信模板数据对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TemplateVO {

    /**
     * 发送用户openId
     */
    private String touser;
    /**
     * 调用模板id
     */
    private String template_id;
    /**
     * 模板数据
     */
    private Map<String, TemplateData> data;

    /**
     * 构造函数
     */
    public TemplateVO(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }
}
