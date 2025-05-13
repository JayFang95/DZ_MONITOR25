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
 * @date 2022/4/12
 * @description 汇总信息对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ReportData {

    // 监测任务id
    private Long missionId;
    // 监测任务名称
    private String missionName;
    // 报警组组id
    private Long alarmGroupId;
    // 测点编组名称
    private String groupName;
    // 点id
    private Long ptId;
    // 点名
    private String ptName;
    // 状态
    private String status;
    // 初始值
    private double num0;
    // 累计变化量
    private double totalNum;
    // 上次累计变化量
    private double totalPreNum;
    // 本次变化量
    private double deltNum;
    // 变化速率
    private double vDeltNum;
    // 本次观测时间
    private Date getTime;
    // 上次观测时间
    private Date getTimePre;

}
