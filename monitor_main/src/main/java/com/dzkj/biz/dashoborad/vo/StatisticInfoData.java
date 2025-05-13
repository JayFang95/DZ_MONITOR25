package com.dzkj.biz.dashoborad.vo;

import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
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
 * @date 2022/7/18 10:46
 * @description 首页统计信息
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StatisticInfoData {

    /**
     * 测试信息字符串
     */
    private String ptNumInfo;
    /**
     * 测点信息数据
     */
    private List<Integer> ptNumList;
    /**
     * 报警信息列表
     */
    private List<AlarmInfoVO> alarmInfoList;

}
