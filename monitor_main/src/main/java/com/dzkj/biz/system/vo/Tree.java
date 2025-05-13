package com.dzkj.biz.system.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/9
 * @description 菜单树对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Tree {

    /**
     * 主键
     */
    private Long key;
    /**
     * 标题
     */
    private String title;
    /**
     * 图标
     */
    private String icon;
    /**
     * 叶子
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isLeaf;
    /**
     * 子节点集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Tree> children;
    /**
     * 父节点主键
     */
    @JsonIgnore
    private Long pkey;

    /**
     * 资源排序序号
     */
    private Integer index;

    /**
     * 选中
     */
    private Boolean checked;

    public Tree(Long key, String title, Long pkey, String icon, Integer index) {
        this.key = key;
        this.title = title;
        this.icon = icon;
        this.pkey = pkey;
        this.index = index;
    }
}
