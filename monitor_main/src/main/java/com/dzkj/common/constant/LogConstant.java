package com.dzkj.common.constant;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/7/21
 * @description 日志相关常量
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface LogConstant {

    /**
     * 日志类型:1:增,2:删,3:改,4:查
     */
    String CREATE = "1";
    String DELETE = "2";
    String UPDATE = "3";
    String RETRIEVE = "4";

    /**
     * 模块名称
     */
    String ALARM_DISTRIBUTE = "报警配置/报警分发配置";
    String ALARM_LEVEL = "报警配置/报警阈值配置";
    String ALARM_INFO = "报警配置/报警信息";


    String SYS_COMPANY = "系统管理/单位授权";
    String SYS_USER = "系统管理/用户管理";
    String SYS_ROLE = "系统管理/角色管理";
    String SYS_RESOURCE = "系统管理/资源管理";
    String SYS_PAGE = "系统管理/页面配置";
    String SYS_LOG = "系统管理/日志管理";
    String SYS_ONLINE = "系统管理/在线情况";
    String SYS_DATA = "系统管理/数据库管理";

    /**
     * 权限认证
     */
    String AUTH_LOGIN = "权限控制/认证管理";
    String SYS_DATA_SWAP = "系统对接/数据交换";

    /**
     * 项目管理
     */
    String PROJECT_INFO = "工程管理/基本信息";
    String PROJECT_MISSION = "工程管理/任务信息";
    String PROJECT_POINT = "工程管理/测点信息_测点";
    String PROJECT_PTGROUP = "工程管理/测点信息_编组";
    String PROJECT_SECTION = "工程管理/测点信息_断面";
    String PROJECT_TYPE_ZL = "工程管理/测点信息_支撑类型";
    String PROJECT_SENSOR_ZL = "工程管理/测点信息_传感器信息";

    /**
     * 设备管理
     */
    String EQUIP_INFO = "设备管理/设备信息";
    String EQUIP_CYCLE = "设备管理/周期策略";
    String EQUIP_MULTI = "设备管理/多站联测";
    String EQUIP_CONTROL = "设备管理/设备控制";
    String EQUIP_RECORD = "设备管理/上下线记录";

    /**
     * 数据管理
     */
    String DATA_UPLOAD = "数据管理/手工上传";
    String DATA_POINT = "数据管理/测点数据";
    String DATA_MISSION = "数据管理/任务数据";
    String DATA_TABLE = "数据管理/监测报表";
    String DATA_OTHER = "数据管理/其他数据";
    String DATA_PUSH = "数据管理/数据推送-上海局";
    String DATA_PUSH_CD = "数据管理/数据推送-成都局";
    String DATA_PUSH_JN = "数据管理/数据推送-济南局";
    String DATA_PUSH_OTHER = "数据管理/数据推送-其他";

    /**
     * 首页展示
     */
    String DASHBOARD_INFO = "信息总览/统计信息";
    String DASHBOARD_PROJECT = "信息总览/项目分布";

    /**
     * 文件管理
     */
    String FILE_STORE = "文件管理/文件管理";

}
