package com.dzkj.robot.box;

import com.dzkj.robot.survey.SurveyBiz;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/18
 * @description 控制器应用对象
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Getter
@Setter
public class ControlBoxAo {

    private Long id;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 所属任务id
     */
    private Long missionId;
    private String missionName;
    private String projectName;

    /**
     * 设备序列号
     */
    private String serialNo;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 在线状态
     */
    private String status;

    /**
     * 测量状态
     */
    private String surveyStatus;
    /**
     * 测量状态：0-停测；1-测量中
     */
    private int survey;

    /**
     * 是否任务绑定
     */
    private Boolean bindMission;

    /**
     * 上线时间
     */
    private Date onlineTime;

    /**
     * 仪器类型: 枚举对象DeviceType
     */
    private Integer deviceType;

    /**
     * 仪器信息(仪器型号、序列号、电源电量等)，如果连接仪器失败，显示：连接仪器失败!
     */
    private String deviceInfo;

    /**
     * 测量配置信息
     */
    private String surveyConfigInfo;

    /**
     * 联测编组信息
     */
    private Long groupId = -1L;
    private String groupInfo;

    /**
     * 关联的测站(包括多站联测)信息
     */
    private String stationInfo;

    /**
     * 控制器业务对象
     */
    private SurveyBiz surveyBiz;

}
