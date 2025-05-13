package com.dzkj.biz.dashoborad.vo;

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
 * @date 2022/5/14
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StatisticData {

    /**
    完成项目数
     */
    private int proFinishNum;
    /**
    未完成项目数
     */
    private int proUnFinishNum;
    /**
    在线设备数
     */
    private int equipOnlineNum;
    /**
    离线设备数
     */
    private int equipOutlineNum;
    /**
    监测数据数
     */
    private int monitorDataNum;
    /**
    监测类型数据信息
     */
    private List<ProTypeData> proTypeData;
}
