package com.dzkj.entity.system;

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
 * @date 2021/8/2
 * @description 登录页page bean
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class LoginPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顶部图片id
     */
    private Long topId;

    /**
     * 轮播图片一id
     */
    private Long carouselOneId;

    /**
     * 轮播图片二id
     */
    private Long carouselTwoId;

    /**
     * 轮播图片三id
     */
    private Long carouselThreeId;

    /**
     * 开始年份
     */
    private Integer year;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 创建时间
     */
    private Date createTime;

}
