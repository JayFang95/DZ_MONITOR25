package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.dashoborad.vo.StatisticTempData;
import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.entity.param_set.Point;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点服务接口
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IPointService extends IService<Point> {

    /**
     * 名称验证
     *
     * @description 名称验证
     * @author jing.fang
     * @date 2023/7/31 11:58
     * @param point point
     * @param groupId groupId
     * @return: boolean
     **/
    boolean findByName(PointVO point, Long groupId);

    /**
     * 验证名称
     *
     * @description
     * @author jing.fang
     * @date 2021/9/7 17:28
     * @param point point
     * @param groupIds groupIds
     * @return boolean
    **/
    boolean findByName(PointVO point, List<Long> groupIds);

    /**
     * 查询编组下测点信息
     *
     * @description 查询编组下测点信息
     * @author jing.fang
     * @date 2023/7/25 15:10
     * @param groupId groupId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> queryByGroupId(Long groupId);

    /**
     * 查询测点集合
     *
     * @description 查询测点集合
     * @author jing.fang
     * @date 2022/3/16 10:43
     * @param missionId missionId
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<Point> queryByMissionId(Long missionId);

    /**
     * 查询测点集合
     *
     * @description 查询测点集合
     * @author jing.fang
     * @date 2022/7/18 13:48
     * @param missionIds missionIds
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<Point> queryByMissionIds(List<Long> missionIds);

    /**
     * 查询项目下测点集合
     *
     * @description 查询项目下测点集合
     * @author jing.fang
     * @date 2022/5/14 14:23
     * @param projectIds projectIds
     * @return java.util.List<java.lang.Long>
    **/
    List<StatisticTempData> getIdInProject(List<Long> projectIds);

    /**
     * 根据测点组id查询
     *
     * @description: 根据测点组id查询
     * @author: jing.fang
     * @Date: 2023/2/16 10:16
     * @param ptGroupIds  ptGroupIds
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<Point> listByPtGroupIds(List<Long> ptGroupIds);

    /**
     * 根据测点组id删除
     *
     * @description: 根据测点组id删除
     * @author: jing.fang
     * @Date: 2023/2/16 10:17
     * @param ptGroupIds ptGroupIds
     * @return boolean
    **/
    boolean removeByPtGroupIds(List<Long> ptGroupIds);

    /**
     * 查询推送测点下拉集合
     *
     * @description 查询推送测点下拉集合
     * @author jing.fang
     * @date 2023/6/7 9:41
     * @param missionId missionId
     * @param pushTaskId pushTaskId
     * @param pointId pointId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> listPoint(Long missionId, Long pushTaskId, Long pointId);

    /**
     * 查询推送测点下拉集合-济南局
     *
     * @description 查询推送测点下拉集合-济南局
     * @author jing.fang
     * @date 2025/3/11 下午4:00
     * @param missionId missionId
     * @param pushTaskId pushTaskId
     * @param pointId pointId
     * @return: java.util.List<com.dzkj.entity.param_set.Point>
     **/
    List<Point> listPointJn(Long missionId, Long pushTaskId, Long pointId);
}
