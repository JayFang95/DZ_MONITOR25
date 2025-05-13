package com.dzkj.dataSwap.vo;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/8 17:56
 * @description 初始位移数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class OriOffsetInfoVO {

    private String ptCode;
    /**
     * 水平角(D.mmss)
    **/
    private String ha;
    /**
     * 竖直角(D.mmss)
     **/
    private String va;
    /**
     * 斜距(m)
     **/
    private Double sd;
    /**
     * 测回数
     **/
    private int loop;
    /**
     * 1-左盘；2-右盘
     **/
    private int dir;

}
