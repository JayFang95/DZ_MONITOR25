package com.dzkj.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/9
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MenuVO {

    /**
     * id
     */
    private Long id;
    /**
     * 父id
     */
    private Long pid;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 显示名称
     */
    private String text;
    /**
     * 路由地址
     */
    private String link;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 按钮是否可用
     */
    private Boolean visible;
    /**
     * 子菜单
     */
    private List<MenuVO> children;

}
