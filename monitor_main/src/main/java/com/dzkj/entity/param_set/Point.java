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
 * @date 2021/9/7
 * @description 测点
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class Point implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属编组id
     */
    private Long ptGroupId;
    @TableField(exist = false)
    private String ptGroupName;

    /**
     * 测点名
     */
    private String name;

    /**
     * 测点类型
     */
    private String type;

    /**
     * 是否停测
     */
    private Boolean stop;

    /**
     * 方位角
     */
    private double azimuth;

    /**
     * 创建人
     */
    @TableField(exist = false)
    private String creator;

    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;

    /**
     * 排序序号
     */
    private int seq;

    /**
     * 是否启用推送规则:启用后，测量数据超过阈值，会自动修正
     */
    private Boolean enableRule;

    /**
     * 报警修正区间值：
     * 单次负值小；单次负值大；单次正值小；单次正值大
     * 累计负值小；累计负值大；累计正值小；累计正值大
     */
    private double minNegDelt;
    private double maxNegDelt;
    private double minPosDelt;
    private double maxPosDelt;
    private double minNegTotal;
    private double maxNegTotal;
    private double minPosTotal;
    private double maxPosTotal;

}
