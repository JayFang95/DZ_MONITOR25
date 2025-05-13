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
 * @description 常规非水平分层任务报告对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class NoDepthData {

    // 监测任务名称
    private String missionName;
    // 测点编组id
    private Long groupId;
    // 测点编组名称
    private String groupName;
    // 仪器名称
    private String equipName;
    // 仪器编号
    private String equipNo ;
    // 上次时间
    private Date preTime;
    // 本次时间
    private Date getTime;
    // 观测
    private String observe;
    // 计算
    private String calculate;
    // 复核
    private String review;
    // 说明
    private String note;
    // 是否是轴力类型
    private Boolean zl;
    // 表格计算数据
    private List<NoDepthCalData> calDataList;
    // EChart展示数据
    private List<NoDepthChartData> chartDataList;
    // 对应的eChart图片路径
    private String eChartPath;

}
