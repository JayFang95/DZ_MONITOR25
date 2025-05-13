package com.dzkj.bean;

import lombok.Data;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/8
 * @description 临时加测计算结果
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
public class SurveyOnceResult {

    private long id;
    private long missionId;
    private int recycleNum;
    private String name;
    private double deltX;
    private double deltY;
    private double deltZ;

}
