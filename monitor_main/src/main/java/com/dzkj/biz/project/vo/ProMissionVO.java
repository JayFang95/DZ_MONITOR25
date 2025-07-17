package com.dzkj.biz.project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/11
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProMissionVO {

    private Long id;

    /**
     * 所属项目id
     */
    private Long projectId;
    private String projectName;

    /**
     * 监测名称
     */
    private String name;

    /**
     * 监测类型id
     */
    private Long typeId;

    /**
     * 监测类型
     */
    private String ptype;
    private String type;

    /**
     * 监测类型名称
     */
    private String typeName;

    /**
     * 设计监测点数
     */
    private Integer designPtNum;

    /**
     * 任务进度
     */
    private BigDecimal progress;

    /**
     * 是否完成（默认未完成）
     */
    private Boolean finished;

    /**
     * 是否反转图表（默认反转）
     */
    private Boolean echartReverse;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建人(姓名)
     */
    private String creator;

    /**
     * 发起人id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 时间间隔计算方式：1-时间 2-日期 3-时间日期
     */
    private String calculateType;
    /**
     * 经理
     */
    private String empManager;
    /**
     * 观测
     */
    private String empObserve;
    /**
     * 计算
     */
    private String empCalculate;
    /**
     * 检核
     */
    private String empReview;
    /**
     * 仪器名称
     */
    private String equipName;
    /**
     * 仪器编号
     */
    private String equipNo;
    /**
     * 配置人员列表
     */
    private List<Long> groupsIds;
    /**
     * 漏测接收人员列表
     */
    private List<Long> noDataAlarmGroupsIds;
    private String noDataAlarmGroupIdStr;
   /**
    * 显示项列表
    */
    private List<CustomDisplayVO> displayList;

    /**
     * 排序
     */
    private Integer idx;
    /**
     * 测量值单位
     */
    private String valueUnit;
    /**
     * 变化量单位
     */
    private String deltUnit;
    /**
     * 换算比率
     */
    private Double ratio;

    /**
     * 漏测报警开启
     */
    private Boolean alarmSurvey;
    /**
     * 漏传报警开启
     */
    private Boolean alarmPush;
    /**
     * 实测信息开启
     */
    private Boolean surveyInfo;

    /**
     * 是否启用推送规则:启用后，测量数据超过阈值，会自动修正
     */
    private Boolean enableRule;

    /**
     * 漏测报警开启_短信
     */
    private Boolean alarmSurveySms;
    /**
     * 漏传报警开启_短信
     */
    private Boolean alarmPushSms;
    /**
     * 超限信息开启_短信
     */
    private Boolean overLimitSms;

}
