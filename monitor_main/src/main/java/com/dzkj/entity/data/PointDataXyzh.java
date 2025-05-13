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
public class PointDataXyzh implements Serializable {

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
     * 周期数
     */
    private Integer recycleNum;

    /**
     * s初始观测值
     */
    private Double s00;

    /**
     * t初始观测值
     */
    private Double t00;

    /**
     * x初始值
     */
    private Double x0;

    /**
     * y初始值
     */
    private Double y0;

    /**
     * z初始值
     */
    private Double z0;

    /**
     * p初始值
     */
    private Double p0;

    /**
     * s初始值
     */
    private Double s0;

    /**
     * t初始值
     */
    private Double t0;

    /**
     * x上次值
     */
    private Double xPrev;

    /**
     * y上次值
     */
    private Double yPrev;

    /**
     * z上次值
     */
    private Double zPrev;

    /**
     * p上次值
     */
    private Double pPrev;

    /**
     * s上次值
     */
    private Double sPrev;

    /**
     * t上次值
     */
    private Double tPrev;

    /**
     * x本次值
     */
    private Double x;

    /**
     * y本次值
     */
    private Double y;

    /**
     * z本次值
     */
    private Double z;

    /**
     * p本次值
     */
    private Double p;

    /**
     * s本次值
     */
    private Double s;

    /**
     * t本次值
     */
    private Double t;

    /**
     * x本次变化量
     */
    private Double deltX;

    /**
     * y本次变化量
     */
    private Double deltY;

    /**
     * z本次变化量
     */
    private Double deltZ;

    /**
     * p本次变化量
     */
    private Double deltP;

    /**
     * s本次变化量
     */
    private Double deltS;

    /**
     * t本次变化量
     */
    private Double deltT;

    /**
     * x变化速率
     */
    private Double vDeltX;

    /**
     * y变化速率
     */
    private Double vDeltY;

    /**
     * z变化速率
     */
    private Double vDeltZ;

    /**
     * p变化速率
     */
    private Double vDeltP;

    /**
     * s变化速率
     */
    private Double vDeltS;

    /**
     * t变化速率
     */
    private Double vDeltT;

    /**
     * x上次累计变化量
     */
    private Double totalXPrev;

    /**
     * y上次累计变化量
     */
    private Double totalYPrev;

    /**
     * z上次累计变化量
     */
    private Double totalZPrev;

    /**
     * p上次累计变化量
     */
    private Double totalPPrev;

    /**
     * s上次累计变化量
     */
    private Double totalSPrev;

    /**
     * t上次累计变化量
     */
    private Double totalTPrev;

    /**
     * x累计变化量
     */
    private Double totalX;

    /**
     * y累计变化量
     */
    private Double totalY;

    /**
     * z累计变化量
     */
    private Double totalZ;

    /**
     * p累计变化量
     */
    private Double totalP;

    /**
     * s累计变化量
     */
    private Double totalS;

    /**
     * t累计变化量
     */
    private Double totalT;

    /**
     * 水平角
     */
    private Double ha;

    /**
     * 竖直角
     */
    private Double va;

    /**
     * 斜距
     */
    private Double sd;

    /**
     * 初始观测时间
     */
    private Date getTime0;

    /**
     * 上次观测时间
     */
    private Date getTimePrev;

    /**
     * 观测时间
     */
    private Date getTime;

    /**
     * 两次观测时间间隔
     */
    private Double deltTime;

    /**
     * 是否超限
     */
    private Boolean overLimit;

    /**
     * 超限信息
     */
    private String overLimitInfo;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建人
     */
    private String creator;

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
