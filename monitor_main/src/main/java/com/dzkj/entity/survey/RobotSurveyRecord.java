package com.dzkj.entity.survey;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/19
 * @description 测量记录
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class RobotSurveyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属任务id
     */
    private Long missionId;
    /**
     * 所属任务
     */
    private String missionName;

    /**
     * 设备序列号
     */
    private String serialNo;

    private int recycleNum;

    /**
     * 漏测报警时间
     */
    private Date surveyAlarmTime;

    /**
     * 漏传报警时间
     */
    private Date uploadAlarmTime;

    /**
     * 数据测量标识：0-未测量；1-已测量
     */
    private Integer surveyFinish;

    /**
     * 数据上传标识：0-未上传；1-已上传
     */
    private Integer uploadFinish;

    private Date createTime;
    /**
     * 是否测量中控制器掉线标识
     */
    private Boolean offlineFlg;


}
