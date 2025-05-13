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
public class RobotSurveyControl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 单位id
     */
    private Long companyId;

    /**
     * 监测任务id
     */
    private Long missionId;

    /**
     * 系列号
     */
    private String serialNo;

    /**
     * 测站配置
    **/
    private String stationConfig;

    /**
     * 参数信息
     */
    private String params;

    /**
     * 联测组id
     */
    private Long groupId;

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


}
