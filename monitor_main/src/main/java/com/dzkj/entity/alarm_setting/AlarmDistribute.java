package com.dzkj.entity.alarm_setting;

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
 * @date 2021/8/26
 * @description 报警分发
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class AlarmDistribute implements Serializable {

    private static final long serialersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属工程id
     */
    private Long projectId;

    /**
     * 规则名称
     */
    private String name;


    /**
     * 平台告警等级
     */
    private String webAlarmLevel;

    /**
     * 短信告警等级
     */
    private String smsAlarmLevel;

    /**
     * 微信告警等级
     */
    private String wxAlarmLevel;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
