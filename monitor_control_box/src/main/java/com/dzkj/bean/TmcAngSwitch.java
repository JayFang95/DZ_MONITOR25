package com.dzkj.bean;

import com.dzkj.enums.OnOffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TmcAngSwitch {

    /**
     * 倾斜校正
     */
    private OnOffType eInclineCorr;
    /**
     * 竖轴校正
     */
    private OnOffType eStandAxisCorr;
    /**
     * 准直误差校正
     */
    private OnOffType eCollimationCorr;
    /**
     * 斜轴校正
     */
    private OnOffType eTiltAxisCorr;

}
