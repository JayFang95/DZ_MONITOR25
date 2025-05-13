package com.dzkj.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
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
public class CustomDisplay implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属任务
     */
    private Long missionId;

    /**
     * 显示类型：1-三维位移 2-其他
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

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
