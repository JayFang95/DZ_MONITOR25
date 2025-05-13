package com.dzkj.biz.equipment.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13
 * @description 控制器记录VO
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class ControlBoxRecordVO  {

    private Long id;

    /**
     * 设备序列号
     */
    private String serialNo;

    /**
     * 在线状态：0-离线；1-在线
     */
    private int status;

    private Date createTime;

}
