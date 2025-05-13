package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/12
 * @description 单项信息对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TableDataCommon {

    // 表格数据
    private List<TableDataCalculate> dataCalculateList;
    // 三维图表数据（导出时不用回传，已生成eChart图）
    private List<PointDataXyzhVO> dataXyzList;
    // 常规图表数据（导出时不用回传，已生成eChart图）
    private List<PointDataZVO> dataZList;

    // 导出额外数据
    // 项目工程名称（后端返回）
    private String projectName;
    // 任务名称（后端返回）
    private String missionName;
    // 监测单位：直接赋值；（后端返回）
    private String jcCompany;
    // 上次时间（后端返回）
    private Date preTime;
    // 本次时间（后端返回）
    private Date getTime;
    // 仪器名称（后端返回）
    private String equipName;
    // 仪器编号（后端返回）
    private String equipNo ;
    // 观测（后端返回）
    private String observe;
    // 计算（后端返回）
    private String calculate;
    // 复核（后端返回）
    private String review;
    // 是否是轴力类型（后端返回）
    private Boolean zl;

    // 报表期数(不足三位的补0：如第1期为001)
    private String reportNo;
    // 报表编号：编号前缀+期数+(截止日期)，如BXHJC100(20211011)；（后端会返回编号前缀）
    private String reportCode;
    // eChart图表编码数据
    private String dataUrl;
    // 说明
    private String note;

}
