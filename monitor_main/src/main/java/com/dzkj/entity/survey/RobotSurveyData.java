package com.dzkj.entity.survey;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
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
public class RobotSurveyData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    private Long missionId;

    /**
     * 测回数
     */
    private Integer recycleNum;

    /**
     * 测站名
     */
    private String stationName;

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
     * 创建人Id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：0-未删除；1-已删除
     */
    private Integer deleted;

    /**
     * 是否自动采集上传
    **/
    private Boolean auto;

    /**
     * 是否有联测组
     **/
    private Boolean hasGroup;


}
