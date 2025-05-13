package com.dzkj.dataSwap.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18
 * @description 数据推送其他vo对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushTaskOtherVO {

    private Long id;

    /**
     * 所属单位id
     */
    private Long companyId;

    /**
     * 关联任务id
     */
    private Long missionId;

    private String missionName;

    /**
     * 推送任务名称
     */
    private String name;

    /**
     * 推送状态：1-推送中；0-未推送
     */
    private Integer status;

    /**
     * 推送数据：1-推送当前；0-推送上期
     */
    private Integer pushCurrentData;

    /**
     * 当前推送延迟时间(分钟)
     */
    private Integer delayUploadTime;

    /**
     * 测量时间：1-本期测量时间；0-上期测量时间
     */
    private Integer useNextTime;

    /**
     * 上期测量延时时间(分钟)
     */
    private Integer lastDelayTime;

    private Date createTime;

    /**
     * 推送报警信息：1-推送中；0-未推送
     */
    private Boolean pushAlarmInfo;


}
