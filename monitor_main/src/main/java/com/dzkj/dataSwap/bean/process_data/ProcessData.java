package com.dzkj.dataSwap.bean.process_data;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 19:02
 * @description 过程数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class ProcessData {

    /**
     * 建设项目编号
     */
    private String projectCode;
    /**
     * 监测点号，必须在上传的点信息中存在该测点
     */
    private String pCode;
    /**
     * 测量流水号，一次任务测量开始时从1开始，依次增加直到本次任务测量完成。
     */
    private int seq;
    /**
     * 点位监测行为时间
     */
    private Date dataTime;
    /**
     * 监测结果 0代表成功 99代表失败
     */
    private int result;
    /*
     * 目前只有位移监测支持原始数据上报，把ORI_OFFSET_INFO中的项目填充到此处即可。
     */
    /**
     * 矢量坐标的距离，单位0.01mm
     */
    private int distance;
    /**
     * 矢量坐标的水平角度，单位0.01s
     */
    private int hAngle;
    /**
     * 矢量坐标的垂直角度，单位0.01s
     */
    private int vAngle;
    /**
     * 测回数，base1
     */
    private int loop;
    /**
     * 1左盘 2右盘
     */
    private int dir;

}
