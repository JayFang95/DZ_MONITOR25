package com.dzkj.biz.alarm_setting.vo;

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
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AlarmInfoVO {

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
     * 处理照片信息( categoryName: 'alarmHandle')
     */
    private String handlePic;

    /**
     * 创建时间
     */
    private Date createTime;

    private Double val;

}
