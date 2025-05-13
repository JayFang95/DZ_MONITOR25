package com.dzkj.entity.system;

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
 * @date 2021/8/2
 * @description 首页page bean
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class HomePage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * logo展开id
     */
    private Long leftFullId;

    /**
     * logo收缩id
     */
    private Long leftRollId;

    /**
     * 应用名称(标签)
     */
    private String labelName;

    /**
     * 应用名称(标题)
     */
    private String titleName;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
