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
 * @date 2024/1/17
 * @description 测量控制组
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class RobotSurveyControlGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监测任务id
     */
    private Long missionId;

    /**
     * 组名
     */
    private String name;

    /**
     * 参数信息
     */
    private String params;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建人Id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 测量状态：0-停测；1-测量中
     */
    private int survey;


}
