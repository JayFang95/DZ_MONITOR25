package com.dzkj.biz.vo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/29 14:31
 * @description 创建通讯录人员
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@Accessors(chain = true)
public class CreateMemberVO {

    private String userid;
    private String name;
    private String alias;
    private String mobile;
    private Integer[] department;
    private String email;
    /**
     * 1： 男性， 2： 女性
     */
    private String gender;
    /**
     * 是否自动发送邀请通知
     */
    private Boolean to_invite;

    public CreateMemberVO(){
        this.department = new Integer[]{1};
        this.to_invite = false;
    }

}
