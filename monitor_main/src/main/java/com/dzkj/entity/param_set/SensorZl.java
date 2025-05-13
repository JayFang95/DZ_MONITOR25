package com.dzkj.entity.param_set;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class SensorZl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目id
     */
    private Long projectId;

    /**
     * 所属测点id
     */
    private Long pointId;
    @TableField(exist = false)
    private String pointName;

    /**
     * 钢筋计编号
     */
    private String jxgCode;

    /**
     * 支撑类型
     */
    private Long typeZlId;
    @TableField(exist = false)
    private String typeZl;

    /**
     * 标定系数
     */
    private Double calibration;

    /**
     * 位置
     */
    private String location;

    /**
     * 破坏
     */
    private Boolean broken;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
