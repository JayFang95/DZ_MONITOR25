package com.dzkj.biz.param_set;

import com.dzkj.biz.param_set.vo.PointDataStationVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11 13:48
 * @description 测站配置点biz接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPointDataStationBiz {

    /**
     * 查询测站配置点集合
     *
     * @description  查询测站配置点集合
     * @author jing.fang
     * @date 2024/9/11 13:56
     * @param pidList pidList
     * @return: java.util.List<com.dzkj.biz.param_set.vo.PointDataStationVO>
     **/
    List<PointDataStationVO> list(List<Long> pidList);

    /**
     * 更新测站配置点信息
     *
     * @description 更新测站配置点信息
     * @author jing.fang
     * @date 2024/9/11 14:07
     * @param list list
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil saveOrUpdate(List<PointDataStationVO> list);
}
