package com.dzkj.biz.data.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:25
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class RobotSurveyDataVO {

    private Long id;

    /**
     * 任务id
     */
    private Long missionId;

    /**
     * 测回数
     */
    private Integer recycleNum;

    private String fileName;

    /**
     * 原始测量数据
     */
    private String rawData;

    /**
     * 计算数据
     */
    private String calcData;
    /**
     * 平差报告
     */
    private String adjReport;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否自动采集上传
    **/
    private Boolean auto;


}
