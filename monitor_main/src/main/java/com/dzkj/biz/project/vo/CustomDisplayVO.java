package com.dzkj.biz.project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/12
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CustomDisplayVO  {

    private Long id;

    /**
     * 所属任务
     */
    private Long missionId;

    /**
     * 显示类型
     */
    private String type;

    /**
     * 系统标题
     */
    private String systemName;

    /**
     * 显示标题
     */
    private String displayName;

    /**
     * 标识字段
     */
    private String tagName;

    /**
     * 是否显示
     */
    private Boolean enableDisplay;

    /**
     * 小数位
     */
    private Integer decimalNum;

    /**
     * 换算比值
     */
    private Integer conversion;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 排序
     */
    private Integer seq;

    /**
     * 映射字段
     */
    private String indexName;


}
