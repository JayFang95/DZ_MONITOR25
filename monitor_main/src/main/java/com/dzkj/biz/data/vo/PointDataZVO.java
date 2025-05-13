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
public class PointDataZVO implements Serializable {

    private Long id;

    /**
     * 测点id
     */
    private Long pid;

    /**
     * 测点名称
     */
    private String name;
    private String gpName;

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
     * z观测初始值
     */
    private Double z00;

    /**
     * 初始值
     */
    private Double z0;

    /**
     * 当前观测值
     */
    private Double z;

    /**
     * 上次观测值
     */
    private Double zPrev;

    /**
     * 两次变化量
     */
    private Double deltZ;

    /**
     * 两次变化速率
     */
    private Double vDeltZ;

    /**
     * 上次累计变化量
     */
    private Double totalZPrev;

    /**
     * 累计变化量
     */
    private Double totalZ;

    /**
     * 初始观测时间
     */
    private Date getTime0;

    /**
     * 观测时间
     */
    private Date getTime;
    private String getTimeStr;

    /**
     * 上次观测时间
     */
    private Date prevGetTime;

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


}
