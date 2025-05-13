package com.dzkj.robot.survey;

import com.dzkj.robot.box.ControlBoxBo;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/15 14:46
 * @description 驱动仪器操作的接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IDeviceBiz {

    /**
     * 取消操作
     */
    void cancelOperate();

    /**
     * 控制器掉线
     */
    void offLineOperate();

    /**
     * 刷新当前控制器
     * @param controlBoxBo 当前控制器
     */
    void refreshCurrentBoxBo(ControlBoxBo controlBoxBo);

    /**
     * 开机
     * @param directInvoke 是否外部直接调用
     */
    void openDevice(boolean directInvoke);

    /**
     * 关机
     */
    void closeDevice();

    /**
     * 打开激光
     */
    void openLaser();

    /**
     * 关闭激光
     */
    void closeLaser();

    /**
     * 正/倒镜
     */
    void changeFace();

    /**
     * 打开双轴补偿
     */
    void openComp();

    /**
     * 测量测试
     */
    void surveyTest();

    /**
     * 获取仪器基本信息：仪器名,仪器号,电源类型(内置/外置),电量
     */
    void getDeviceInfo();

    /**
     * 获取仪器电量信息：电源类型(内置/外置),电量
     * @param directInvoke 是否值直接调用
     */
    void getDevicePowerInfo(boolean directInvoke);

    /**
     * 获取详细测量信息
     */
    void getFullMeas();

    /**
     * 验证测站信息(坐标、仪器高、目标高)
     */
    void checkStation();

    /**
     * 激活测量并获得详细测量信息
     * @param isFace1 是否正镜
     * @param checkFace 是否需要监测正倒镜
     * @param searchHa  自动搜索范围Ha
     * @param searchVa 自动搜索范围Va
     */
    void doMeasureAndGetFullResult(boolean isFace1, boolean checkFace, double searchHa, double searchVa);

    /**
     * 激活测量并获得详细测量信息
     * @param searchHa 自动搜索范围Ha
     * @param searchVa 自动搜索范围Va
     * @param ha 方位角
     * @param va 竖直角
     * @param ht 目标高
     * @param pauseTime 暂停后开始测量 默认0
     * @param directInvoke 是否外部直接调用
     */
    void checkMeasureAndGetFullResult(double searchHa, double searchVa, double ha, double va,double ht, int pauseTime, boolean directInvoke);

    /**
     * 照准指定目标方向
     * @param ha 水平角(弧度)
     * @param va 竖直角(弧度)
     * @param closeDevice 是否需要关机
     * @param directInvoke 是否值直接调用
     */
    void prepareStop(double ha, double va, boolean closeDevice, boolean directInvoke);

    /**
     * 使用预测量数据
     * @param chResultsCount 当前测回测量记录数
     * @param chIndex 当前测回序号
     * @param directInvoke 是否值直接调用
     */
    void usePreSurveyResult(int chResultsCount, int chIndex, boolean directInvoke);
}
