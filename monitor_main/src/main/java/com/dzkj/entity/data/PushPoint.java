package com.dzkj.entity.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送测点
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属推送任务
     */
    private Long pushTaskId;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 监测设备分类，枚举代码见“监测设备分类”
     */
    private Integer deviceCatagory;

    /**
     * 设备唯一性代码，在本项目中要保持设备代码的唯一性
     */
    @TableField("`code`")
    private String code;

    /**
     * 测点代码,在一个项目中，测点代码必须唯一
     */
    private String ptCode;

    /**
     * 监测点高度(m)，单位0.01mm (初始Z值)
     */
    private double height;

    /**
     * 监测点监测目标的大分类
     */
    private Integer mainCatagory;

    /**
     * 监测点监测目标的小分类
     */
    private Integer subCatagory;

    /**
     * 监测点监测铁路线的类型
     */
    private Integer railwayType;

    /**
     * 监测点的功能类型
     */
    private Integer funcType;

    /**
     * 监测点业务分类
     */
    private Integer funcAttr;

    /**
     * 监测点铁路里程
     */
    private String kiloMark;

    /**
     * 监测点报警处理方法
     */
    private Integer alarmHandler;

    /**
     * 矢量坐标的距离(m)，单位0.01mm()(斜距)
     */
    private double distance;

    /**
     *  D.mmss 矢量坐标的水平角度(度)，单位0.01s
     */
    private String ha;

    /**
     * D.mmss 矢量坐标的垂直角度(度)，单位0.01s
     */
    private String va;

    /**
     * 水平横坐标(m)，单位0.01mm，垂直轨道方向
     */
    private double x;

    /**
     * 水平顺向坐标(m)，单位0.01mm，沿着轨道方向
     */
    private double y;

    /**
     * 高程(m)，单位0.01mm
     */
    private double z;

    /**
     * 监测数据产生时间
     */
    private Date dataTime;

    /**
     * 关联测点id
     */
    private Long pointId;


}
