package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/31
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class JcInfoVO implements Serializable {

    private Long id;

    /**
     * 工程id
     */
    private Long projectId;

    /**
     * 任务id
     */
    private Long missionId;

    /**
     * 观测日期
     */
    private Date jcDate;

    /**
     * 观测信息
     */
    private String info;

    /**
     * 创建时间
     */
    private Date createTime;

    // 天气状况
    private String tqStatus;
    // 土质状况
    private String tzStatus;
    // 管道状况
    private String gdStatus;
    // 测点状况
    private String ptStatus;

}
