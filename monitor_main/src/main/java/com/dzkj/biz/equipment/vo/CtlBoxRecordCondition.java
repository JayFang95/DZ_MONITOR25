package com.dzkj.biz.equipment.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/9
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class CtlBoxRecordCondition {

    private String serialNo;

    private Integer timeNum;

    private Date time;

}
