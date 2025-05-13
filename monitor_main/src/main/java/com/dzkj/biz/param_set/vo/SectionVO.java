package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class SectionVO {

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


}
