package com.dzkj.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/4
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
public class UserBO {

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 所属公司id
     */
    private Long companyId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职务
     */
    private String post;

    private String functionConfig;

}
