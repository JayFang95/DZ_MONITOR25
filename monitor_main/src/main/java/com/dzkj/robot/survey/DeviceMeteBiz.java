package com.dzkj.robot.survey;

import com.dzkj.bean.RtCode;
import com.dzkj.common.CommonUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.robot.bean.OperateCompleteResult;
import com.dzkj.robot.box.ControlBoxMete;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 8:18
 * @description 温度气压采集
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@Slf4j
public class DeviceMeteBiz implements IDeviceMeteBiz {

    private Consumer<OperateCompleteResult> surveyOperateCompleted;

    private ControlBoxMete currentBoxMete;
    private String commandName = "";
    private String logInfo = "";

    public ControlBoxMete getControlBoxMete() {
        return currentBoxMete;
    }

    public DeviceMeteBiz(Consumer<OperateCompleteResult> surveyCompleted) {
        this.surveyOperateCompleted = surveyCompleted;
    }

    @Override
    public void refreshCurrentBoxMete(ControlBoxMete currentBoxMete) {
        this.currentBoxMete = currentBoxMete;
    }

    @Override
    public void survey(boolean directCall) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                RtCode rtCode;
                List<String> list = new ArrayList<>();

                //1.测量温度
                commandName = "Survey";
                logInfo = "***测量 温度湿度 ***";
                log.info(logInfo);
                String commandCode = "010300000002C40B";
                rtCode = currentBoxMete.processCommand(commandCode, list);
                if (CommonUtil.SYS_GRC_OK != rtCode) {
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directCall));
                    return;
                }

                //2.测量气压
                logInfo = "***测量 气压 ***";
                log.info(logInfo);
                commandCode = "01030010000185CF";
                rtCode = currentBoxMete.processCommand(commandCode, list);
                if (CommonUtil.SYS_GRC_OK != rtCode) {
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directCall));
                    return;
                }

                // 正常执行
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, list, directCall));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, directCall));
            }
        });
    }
}
