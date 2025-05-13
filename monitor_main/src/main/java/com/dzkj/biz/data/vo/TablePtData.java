package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/2
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TablePtData {
    private Long id;
    /**
     * 测点id
     */
    private Long pid;
    /**
     * 测点名
     */
    private String name;
    /**
     * 测点深度
     */
    private String ptName;
    /**
     * 测点状态
     */
    private String status;
    /**
     * 传感器状态
     */
    private String sensorStatus;
    /**
     * x
     */
    private Double x;
    /**
     * y
     */
    private Double y;
    /**
     * 观测值
     */
    private Double z;
    /**
     * 观测时间
     */
    private Date getTime;
    /**
     * 备注
     */
    private String note;
    /**
     * 编号
     */
    private String jxgCode;
    /**
     * 位置
     */
    private String location;
    /**
     * 温度
     */
    private String temp;
}
