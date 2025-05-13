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
public class PtStopVO implements Serializable {

    private Long id;

    /**
     * 测点id
     */
    private Long pid;

    /**
     * 工程名称
     */
    private String projectName;

    /**
     * 任务名称
     */
    private String missionName;

    /**
     * 编组名称
     */
    private String groupName;

    /**
     * 测点名称
     */
    private String pointName;

    /**
     * 停测原因
     */
    private String reason;

    /**
     * 停测时间
     */
    private Date createTime;


}
