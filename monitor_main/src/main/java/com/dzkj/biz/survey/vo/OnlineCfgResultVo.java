package com.dzkj.biz.survey.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:25
 * @description 在线配置测量结果
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class OnlineCfgResultVo {

    private double stX;
    private double stY;
    private double stZ;
    private double ptX;
    private double ptY;
    private double ptZ;
    private double ha;
    private double va;
    private double sd;
    private double hi;
    private double ht;
    private boolean face1;

}
