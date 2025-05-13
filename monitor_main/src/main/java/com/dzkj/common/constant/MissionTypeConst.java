package com.dzkj.common.constant;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/27 14:00
 * @description 任务类型枚举
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface MissionTypeConst {

    String AUTO_XYZ_OFFSET = "全站仪自动监测(XYZ)";
    String HAND_XYZ_OFFSET = "手动三维位移(XYZ)";
    String SX_H_OFFSET = "竖向位移(H)";
    String SP_XY_OFFSET = "水平位移(XY->S)";
    String SP_HD_OFFSET = "水平位移(HD)";
    String SP_DEEP_OFFSET = "水平位移(分层)";
    String QX_OFFSET = "倾斜位移(%)";
    String ZC_FORCE = "支撑轴力";
    String MANUAL_PATROL = "人工巡视";

}
