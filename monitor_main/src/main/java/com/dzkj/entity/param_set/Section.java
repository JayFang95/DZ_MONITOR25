package com.dzkj.entity.param_set;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 断面
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属任务id
     */
    private Long missionId;

    /**
     * 所属编组id
     */
    private Long groupId;

    /**
     * 断面名称
     */
    private String name;

    /**
     * 方位角
     */
    private Double azimuth;

    /**
     * 起点x
     */
    private Double startX;

    /**
     * 起点y
     */
    private Double startY;

    /**
     * 终点x
     */
    private Double endX;

    /**
     * 终点y
     */
    private Double endY;

    /**
     * 包含测点集合
     */
    private String pidStr;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;


}
