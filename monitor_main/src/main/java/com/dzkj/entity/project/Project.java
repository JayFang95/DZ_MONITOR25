package com.dzkj.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 项目信息
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目类型
     */
    private Long typeId;
    @TableField(exist = false)
    private String type;

    /**
     * 经度
     */
    private String lng;

    /**
     * 纬度
     */
    private String lat;

    /**
     * 省份
     */
    private String province;

    /**
     * 项目进度
     */
    private Double progress;

    /**
     * 是否完成（默认未完成）
     */
    private Boolean finished;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建人(姓名)
     */
    @TableField(exist = false)
    private String creator;

    /**
     * 发起人id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 报告编号前缀
     */
    private String reportNoPre;

    /**
     * 建设单位名称
     */
    private String nameJs;
    /**
     * 设计单位名称
     */
    private String nameSj;
    /**
     * 勘察单位名称
     */
    private String nameKc;
    /**
     * 施工单位名称
     */
    private String nameSg;
    /**
     * 监理单位名称
     */
    private String nameJl;
    /**
     * 监测单位名称
     */
    private String nameJc;
    /**
     * 第三方监测单位名称
     */
    private String nameJcThird;
    /**
     * 额外信息
     */
    private String extraInfo;

    /**
     * 逻辑删除标识：1-已删除；0-未删除
     */
    private int deleted;

}
