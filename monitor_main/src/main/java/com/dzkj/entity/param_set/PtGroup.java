package com.dzkj.entity.param_set;

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
 * @date 2021/9/7
 * @description 点组
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class PtGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属任务id
     */
    private Long missionId;
    @TableField(exist = false)
    private String missionName;
    @TableField(exist = false)
    private String projectName;

    /**
     * 点组名
     */
    private String name;

    /**
     * 报警组id
     * 2024/11/18 修改 允许为空
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long alarmGroupId;

    /**
     * 报警分发规则id集合
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String alarmDistributeIds;

    /**
     * 报警接收人员id集合
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String alarmReceiveIds;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private String creator;

    private Long createId;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;

}
