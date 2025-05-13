package com.dzkj.mapper.param_set;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.biz.dashoborad.vo.StatisticTempData;
import com.dzkj.entity.param_set.Point;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface PointMapper extends BaseMapper<Point> {

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2023/7/25 15:01
     * @param groupId groupId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> queryByGroupId(@Param("groupId") Long groupId);

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2022/3/16 10:44
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<Point> queryByMissionId(@Param("missionId") Long missionId);

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2022/7/18 13:48
     * @param list missionIds
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<Point> queryByMissionIds(@Param("list")List<Long> list);

    /**
     * 获取测点id集合
     *
     * @description 获取测点id集合
     * @author jing.fang
     * @date 2022/5/14 14:24
     * @param list projectIds
     * @return java.util.List<java.lang.Long>
    **/
    List<StatisticTempData> getIdInProject(@Param("list") List<Long> list);

    /**
     * 查询推送测点信息
     *
     * @description 查询推送测点信息
     * @author jing.fang
     * @date 2023/6/7 9:42
     * @param missionId missionId
     * @param pushTaskId pushTaskId
     * @param pointId pointId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> listPoint(@Param("missionId") Long missionId,
                          @Param("pushTaskId") Long pushTaskId,
                          @Param("pointId") Long pointId);

    /**
     * 查询推送测点信息-济南局
     *
     * @description 查询推送测点信息-济南局
     * @author jing.fang
     * @date 2025/3/11 下午4:01
     * @param missionId missionId
     * @param pushTaskId pushTaskId
     * @param pointId pointId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> listPointJn(@Param("missionId") Long missionId,
                          @Param("pushTaskId") Long pushTaskId,
                          @Param("pointId") Long pointId);
}
