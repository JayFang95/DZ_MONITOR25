package com.dzkj.entity.alarm_setting;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/20
 * @description 报警信息同步表
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class AlarmInfoCorrect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属工程id
     */
    private Long projectId;

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 报警点id
     */
    private Long ptId;

    /**
     * 周期
     */
    private Integer recycleNum;

    /**
     * 报警源
     */
    private String alarmOrigin;

    /**
     * 报警信息
     */
    private String info;

    /**
     * 报警等级
     */
    private String alarmLevel;

    /**
     * 报警阈值
     */
    private String threshold;

    /**
     * 是否是绝对值
     */
    @TableField(exist = false)
    private Boolean abs;

    /**
     * 报警时间
     */
    private Date alarmTime;

    /**
     * 是否处理
     */
    private Boolean handle;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 处理结果
     */
    private String result;

    /**
     * 处理照片信息
     */
    private String handlePic;

    private Date createTime;

    /**
     * 逻辑删除标识：0-未删除；1-已删除
     */
    private Integer deleted;


}
