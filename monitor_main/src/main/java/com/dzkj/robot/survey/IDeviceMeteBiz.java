package com.dzkj.robot.survey;

import com.dzkj.robot.box.ControlBoxMete;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/26 17:47
 * @description 驱动仪器操作的接口_温度气压采集
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IDeviceMeteBiz {

    ControlBoxMete getControlBoxMete();

    /**
     * 刷新控制器状态
     * @param currentBoxMete 温度气压盒子
     */
    void refreshCurrentBoxMete(ControlBoxMete currentBoxMete);

    /**
     * 测量
     * @param directCall 是否直接调用
     */
    void survey(boolean directCall);
}
