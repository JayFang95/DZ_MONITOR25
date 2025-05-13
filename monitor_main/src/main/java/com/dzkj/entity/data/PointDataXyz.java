package com.dzkj.entity.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

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
public class PointDataXyz implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 测点id
     */
    private Long pid;

    /**
     * 测点名称
     */
    private String name;

    /**
     * 是否停测
     */
    private Boolean stop;

    /**
     * 测点状态
     */
    private String status;

    /**
     * x坐标
     */
    private Double x;

    /**
     * y坐标
     */
    private Double y;

    /**
     * z坐标
     */
    private Double z;

    /**
     * 观测时间
     */
    private Date getTime;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否自测
     */
    private Boolean auto;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
