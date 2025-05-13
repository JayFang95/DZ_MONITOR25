package com.dzkj.enums;


/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description TMC测量模式
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public enum TmcMeasurePrg {

    /**
     * STOP-测量编程
     */
    TMC_STOP(0),
    /**
     * Default-DIST-测量编程
     */
    TMC_DEF_DIST(1),
    /**
     * TMC_STOP并清空数据
     */
    TMC_CLEAR(3),
    /**
     * 信号测量 (测试函数)
     */
    TMC_SIGNAL(4),
    /**
     * 开始测量任务或重新开始测量任务
     */
    TMC_DO_MEASURE(6),
    /**
     * 使Distance-TRK 测量(距离跟踪)
     */
    TMC_RTRK_DIST(8),
    /**
     * 无棱镜跟踪
     */
    TMC_RED_TRK_DIST(10),
    /**
     * 频率测量 (测试)
     */
    TMC_FREQUENCY(11);

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    TmcMeasurePrg(int code) {
        this.code = code;
    }

}
