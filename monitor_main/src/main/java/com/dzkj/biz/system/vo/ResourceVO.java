package com.dzkj.biz.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 资源bean
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ResourceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 父级资源id
     */
    private Long pid;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 显示名称
     */
    private String text;

    /**
     * 路由url
     */
    private String link;

    /**
     * 资源图标
     */
    private String icon;

    /**
     * 资源类型
     */
    private String type;

    /**
     * 访问url
     */
    private String url;

    /**
     * 请求方式(GET,POST,PUT,DELETE)
     */
    private String method;

    /**
     * 是否白名单
     */
    private Boolean whiteUrl;

    /**
     * 排序
     */
    private Integer index;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 子节点集合
     */
    private List<ResourceVO> childrenMenu;

}
