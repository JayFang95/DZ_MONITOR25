package com.dzkj.entity.system;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 公司bean
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 公司代码(英文，全大写)
     */
    private String code;

    /**
     * 备注
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String note;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建人id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否当前
     */
    private Boolean current;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 激活终止时间
     */
    private Date statusTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;

    private String functionConfig;

}
