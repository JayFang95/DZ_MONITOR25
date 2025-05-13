package com.dzkj.biz.param_set.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11
 * @description 测站配置点信息vo
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class PointDataStationVO {

    private Long id;

    private Long pid;

    private Double x;

    private Double y;

    private Double z;

    private Double h;

    /**
     * 是否固定
     */
    private Boolean fixed;

}
