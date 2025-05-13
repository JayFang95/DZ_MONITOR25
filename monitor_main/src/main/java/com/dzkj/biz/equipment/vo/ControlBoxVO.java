package com.dzkj.biz.equipment.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class ControlBoxVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 所属任务id
     */
    private Long projectId;

    private String projectName;

    /**
     * 所属任务id
     */
    private Long missionId;
    private String missionName;

    /**
     * 联测编组信息
     */
    private Long groupId;
    private String groupInfo;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备序列号
     */
    private String serialNo;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 在线状态
     */
    private String status;

    /**
     * 测量状态
     */
    private String surveyStatus;

    /**
     * 上线时间
     */
    private Date onlineTime;

    /**
     * 是否任务绑定
     */
    private Boolean bindMission;

    /**
     * 仪器类型: 枚举对象DeviceType
     */
    private Integer deviceType;

    /**
     * 仪器信息
     */
    private String deviceInfo;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建人Id
     */
    private Long createId;
    private String creator;

    private Date createTime;

    /**
     * 测量状态信息
     */
    private String statusInfo;

    /**
     * 关联的测站(包括多站联测)信息
     */
    private String stationInfo;

}
