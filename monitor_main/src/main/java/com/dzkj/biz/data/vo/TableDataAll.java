package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

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
public class TableDataAll {

    // region 汇总信息
    // 表格数据
    private List<DataAll> dataList;
    // 报警信息
    private String alarmInfo;
    // 工作概述
    private String workDesc;
    // 工况概述
    private String statusDesc;
    // 结果和结论
    private String result;
    // 项目人员信息（后端返回）
    private String empInfo;
    // endregion

    // region 以下为分项数据
    // 人工巡视单项
    private List<XsData> xsDataList;
    // 水平分层数据
    private List<DepthData> depthDataList;
    // 非水平分层数据
    private List<NoDepthData> noDepthDataList;
    // endregion

    // region 封面信息
    // 项目工程名称（后端返回）
    private String projectName;
    // 监测报表类型(选择“日”为：监测日报表，选择“周”为：监测周报表，选择“月”为：监测月报表，选择“自选”和“全部”为：监测？报表)
    private String reportType;
    // 报表期数(不足三位的补0：如第1期为001)
    private String reportNo;
    // 报表编号：编号前缀+期数+(截止日期)，如BXHJC100(20211011)；（后端会返回编号前缀）
    private String reportCode;
    // 监测日期：开始日期-截止日期（如2021年10月26日-2021年10月28日），当日期选项为“日”时：截止日期；
    private String reportDate;
    // 报警：根据汇总表中是否有超限项，填“是”或“否” ("是☑    否□" : "是□    否☑")；（后端返回）
    private String alarmStr;
    // 监测单位：直接赋值；（后端返回）
    private String jcCompany;
    // endregion

    // 报表类型 日 0； 周 1； 月 2； 自选 3； 全部 4
    private int reportTypeIndex;
    // 工程id
    private Long projectId;
}
