package com.dzkj.entity.system;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 人员bean
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 所属公司id
     */
    private Long companyId;
    @TableField(exist = false)
    private String companyName;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 姓名
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String name;

    /**
     * 职务
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String post;

    /**
     * 手机号
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String phone;

    /**
     * 公众号id
     */
    private String appId;

    /**
     * 备注
     */
    @TableField(insertStrategy = FieldStrategy.IGNORED)
    private String note;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否可以删除
     */
    private Boolean deleteFlg;

    /**
     * 属于编组
     */
    @TableField(exist = false)
    private Long groupId;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
