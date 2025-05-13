package com.dzkj.biz.alarm_setting.vo;

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
 * @date 2022/3/30
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
public class AlarmDistributeVO {

    private Long id;

    /**
     * 所属工程id
     */
    private Long projectId;

    /**
     * 规则名称
     */
    private String name;


    /**
     * 平台告警等级
     */
    private String webAlarmLevel;

    /**
     * 短信告警等级
     */
    private String smsAlarmLevel;

    /**
     * 微信告警等级
     */
    private String wxAlarmLevel;

    /**
     * 创建时间
     */
    private Date createTime;

}
