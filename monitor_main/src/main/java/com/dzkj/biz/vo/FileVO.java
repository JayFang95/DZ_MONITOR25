package com.dzkj.biz.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 附件 beanVO
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class FileVO {

    private Long id;

    /**
     * 类别名,如项目
     */
    private String categoryName;

    /**
     * 类别id,如项目id
     */
    private Long categoryId;

    /**
     * 种类,如重要文件,非重要文件
     */
    private String scopeName;

    /**
     * 文件全名
     */
    private String realName;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件类型(正常为字典值：file_type)
     */
    private String type;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建者id
     */
    private Long creatorId;
    private String creator;

    /**
     * 是否临时文件
     */
    private Boolean temp;

    /**
     * 创建时间
     */
    private Date createTime;

    private Long companyId;


}
