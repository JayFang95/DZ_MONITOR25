package com.dzkj.robot.bean;

import com.dzkj.bean.RtCode;
import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/16 11:22
 * @description 多站测量完成通用结果
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MultiOperateCompleteResult {

    private boolean actionComplete;
    private RtCode rtCode;

    private String commandName;

    public MultiOperateCompleteResult() {
    }

    public MultiOperateCompleteResult(boolean actionComplete, RtCode rtCode, String commandName) {
        this.actionComplete = actionComplete;
        this.rtCode = rtCode;
        this.commandName = commandName;
    }
}
