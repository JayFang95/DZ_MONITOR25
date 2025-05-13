package com.dzkj.constant;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/12/9
 * @description 通信常量
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class ControlBoxConstant {

    /**
     * DTU 注册信息前缀规则
     */
    public static final String REGISTER_PREFIX = "*DZKJ_";
    public static final String REGISTER_PREFIX_HEX = "2A445A4B4A5F";
    /**
     * DTU 心跳信息前缀规则
     */
    public static final String PING_PREFIX = "*ping_";
    public static final String PING_PREFIX_HEX = "2A70696E675F";
    /**
     * DTU 心跳间隔时间
     */
    public static final Integer SOCKET_HEART_BEAT_MILLIONS = 5000;
    /**
     * redis 缓存超时
     */
    public static final Integer SOCKET_REDIS_TIMEOUT_MILLIONS = 5500;
    /**
     * 心跳保活次数
     */
    public static final Integer SOCKET_KEEP_LIVE_COUNT = 3;
    /**
     * DTU 客户端读取超时：3次心跳保活有效
     */
    public static final Integer READ_TIMEOUT_MILLIONS = SOCKET_HEART_BEAT_MILLIONS * SOCKET_KEEP_LIVE_COUNT;
    /**
     * DTU 客户端缓冲区大小: 4K, 每次最多读取 1k
     */
    public static final Integer SOCKET_READ_BUGGER_SIZE = 4 * 1024;


}
