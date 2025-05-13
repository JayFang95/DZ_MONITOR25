package com.dzkj.robot.bean;

import com.dzkj.bean.RtCode;
import lombok.Data;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/16 11:22
 * @description 测量完成通用结果
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class OperateCompleteResult {

    private RtCode rtCode;

    private String commandName;

    private List<String> results;

    private boolean directCall = false;

    public OperateCompleteResult() {
    }

    public OperateCompleteResult(RtCode rtCode, String commandName, List<String> results) {
        this.rtCode = rtCode;
        this.commandName = commandName;
        this.results = results;
        this.directCall = false;
    }

    public OperateCompleteResult(RtCode rtCode, String commandName, List<String> results, boolean directCall) {
        this.rtCode = rtCode;
        this.commandName = commandName;
        this.results = results;
        this.directCall = directCall;
    }
}
