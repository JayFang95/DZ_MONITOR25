package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/16 14:46
 * @description 消息反馈类，用于记录函数执行状态
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class BackMessage {

    /**
     * 是否成功：默认值：false
    **/
    private boolean ok = false;

    /**
     * 执行消息：默认值：失败
     **/
    private String msg = "失败";

}
