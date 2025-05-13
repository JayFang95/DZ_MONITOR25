package com.dzkj.biz.data.vo;

import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/8/8 11:16
 * @description 数据上传对象(app)
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class DataApp {

    private Long pid;
    private String name;
    private String depth;
    private String x;
    private String y;
    private String z;
    private String pointStatus;
    private String note;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date getTime;
    private String groupName;
    private String jxgCode;
    private String location;
    private String temp;
    private String sensorStatus;
    /**
     * 0：非xyz; 1-水平xy ;2: 三维xyz
     */
    private int isXy;
    private boolean auto;
}
