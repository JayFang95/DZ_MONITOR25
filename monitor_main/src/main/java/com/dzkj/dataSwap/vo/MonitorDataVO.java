package com.dzkj.dataSwap.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/7 18:20
 * @description 采集数据展示
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class MonitorDataVO {

    private String name;
    private Integer recycleNum;
    private Double x;
    private Double totalX;
    private Double deltX;
    private Double y;
    private Double totalY;
    private Double deltY;
    private Double z;
    private Double totalZ;
    private Double deltZ;
    private Boolean overLimit;
    private Date getTime;

}
