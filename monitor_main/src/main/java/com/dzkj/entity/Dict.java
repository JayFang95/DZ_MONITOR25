package com.dzkj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典对象
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级id
     */
    private Long pid;

    /**
     * 标识
     */
    @TableField("`code`")
    private String code;

    /**
     * key值
     */
    @TableField("`key`")
    private String key;

    /**
     * value值
     */
    @TableField("`value`")
    private String value;

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 排序
     */
    private Integer seq;


}
