package com.dzkj.entity.alarm_setting;

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
 * @date 2021/8/26
 * @description 报警细项
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class AlarmItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    private Long ptId;

    /**
     * 所属报警组id
     */
    private Long alarmGroupId;

    /**
     * 同时创建得报警项id
     */
    private Long teamId;

    /**
     * 检测类型
     */
    private String monitorType;

    /**
     * 成果类型
     */
    private String resultType;

    /**
     * 成果类型细项
     */
    private String resultItemType;

    /**
     * 告警等级
     */
    private String alarmLevel;

    /**
     * 告警阈值
     */
    private String alarmThreshold;

    /**
     * 告警提示信息
     */
    private String alarmInfo;

    /**
     * 单位
     */
    private String unit;

    /**
     * 报警阈值类型：1-单项 2-双项
     */
    private Integer alarmType;

    /**
     * 是否是绝对值
     */
    private Boolean absValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
