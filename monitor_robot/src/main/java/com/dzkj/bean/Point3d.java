package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/27 9:35
 * @description Point3d
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class Point3d {

    private double _x0;
    private long id;
    private String name;
    private boolean asFixed;
    private double x0;
    private double y0;
    private double z0;
    private double x;
    private double y;
    private double z;
    private double mx;
    public double my;
    public double mz;

    public Point3d() {
    }

    public Point3d(long pid, String name) {
        this.id = pid;
        this.name = name;
        this.x0 = 9999999999.0;//10个9，代表没有进行概略坐标计算，无概略坐标
    }

    public Point3d(long id, String name, boolean asFixed, double x0, double y0, double z0) {
        this._x0=x0;
        this.id = id;
        this.name = name;
        this.asFixed = asFixed;
        this.x0 = x0;
        this.x = x0;
        this.y0 = y0;
        this.y = y0;
        this.z0 = z0;
        this.z = z0;
    }

    /**
     * 计算平面点位中误差
     * @return 平面点位中误差
     */
    public double getMs()
    {
        return Math.sqrt(this.getMx() * this.getMx() + this.getMy() * this.getMy());
    }

    /**
     * 计算三维点位中误差
     * @return 三维点位中误差
     */
    public double getMp()
    {
        return Math.sqrt(this.getMx() * this.getMx() + this.getMy() * this.getMy()+ this.getMz() * this.getMz());
    }
}
