package com.dzkj.biz.alarm_setting.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class AlarmInfoCondition {

    // 人员id
    private Long userId;
    // 所属任务
    private List<Long> missionIds;
    //是否处理
    private Boolean handle;
    // 报警时间数值
    private Integer alarmTimeNum;
    // 报警时间 (前端不传递)
    private Date alarmTime;
    // 测量周期
    private Integer recycleNum;
    // 报警源
    private String alarmOrigin;
    // 报警信息
    private String info;

    // 2024/11/20 数据库表来源: 1-原表  2-同步表
    private Integer dbSource;

}
