package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/1/13
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class PtGroupVO {

    private Long id;

    /**
     * 所属任务id
     */
    private Long missionId;

    private String missionName;
    private String projectName;

    /**
     * 点组名
     */
    private String name;

    /**
     * 人员配置id集合
     */
    private List<Long> groupIds;

    /**
     * 报警组id
     */
    private Long alarmGroupId;

    /**
     * 报警分发规则id集合
     */
    private String alarmDistributeIds;
    private List<Long> distributeIds;

    /**
     * 报警接收人员id集合
     */
    private String alarmReceiveIds;
    private List<Long> receiveIds;

    /**
     * 创建时间
     */
    private Date createTime;

    private String creator;
    private Long createId;

}
