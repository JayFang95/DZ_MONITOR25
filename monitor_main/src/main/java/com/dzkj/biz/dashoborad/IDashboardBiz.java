package com.dzkj.biz.dashoborad;

import com.dzkj.biz.dashoborad.vo.InfoData;
import com.dzkj.biz.dashoborad.vo.ScreenData;
import com.dzkj.biz.dashoborad.vo.StatisticInfoData;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/14
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDashboardBiz {

    /**
     * 查询首页统计信息（新）
     *
     * @description 查询首页统计信息（新）
     * @author jing.fang
     * @date 2022/7/18 11:31
     * @param infoData InfoData
     * @param userId userId
     * @return com.dzkj.biz.dashoborad.vo.StatisticInfoData
     **/
    StatisticInfoData getDashboardInfo(InfoData infoData, Long userId);

    /**
     * 获取大屏显示信息
     *
     * @description 获取大屏显示信息
     * @author jing.fang
     * @date 2022/5/17 9:05
     * @param companyId companyId
     * @return com.dzkj.biz.dashoborad.vo.ScreenData
    **/
    ScreenData getDashboardScreenInfo(Long companyId);

}
