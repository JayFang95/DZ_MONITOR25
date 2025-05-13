package com.dzkj.entity.equipment;

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
 * @date 2023/2/14
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class ControlBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 所属任务id
     */
    private Long projectId;
    @TableField(exist = false)
    private String projectName;

    /**
     * 所属任务id
     */
    private Long missionId;
    @TableField(exist = false)
    private String missionName;

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
     * 是否任务绑定
     */
    private Boolean bindMission;

    /**
     * 仪器类型
     */
    private Integer deviceType;

    /**
     * 仪器信息
     */
    private String deviceInfo;

    /**
     * 测量状态：0-停测；1-测量中
     */
    private int survey;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建人Id
     */
    private Long createId;

    @TableField(exist = false)
    private String creator;

    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;

    @TableField(exist = false)
    private String params;


}
