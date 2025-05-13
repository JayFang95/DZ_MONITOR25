package com.dzkj.biz.param_set;

import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 测点业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IPointBiz {

    /**
     * 查询测点列表
     *
     * @description 查询测点列表
     * @author jing.fang
     * @date 2021/9/7 16:55
     * @param groupId groupId
     * @return java.util.List<com.dzkj.entity.param_set.Point>
    **/
    List<PointVO> getList(Long groupId);

    /**
     * 新增测点
     *
     * @description 新增测点
     * @author jing.fang
     * @date 2021/9/7 16:58
     * @param point point
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(PointVO point);

    /**
     * 修改测点
     *
     * @description 修改测点
     * @author jing.fang
     * @date 2021/9/7 16:58
     * @param point point
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(PointVO point);

    /**
     * 删除测点
     *
     * @description 删除测点
     * @author jing.fang
     * @date 2021/9/7 17:00
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 批量导入
     *
     * @description 批量导入
     * @author jing.fang
     * @date 2021/9/8 11:16
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil saveBatch(List<PointVO> list);

    /**
     * 批量导入测点
     *
     * @description 批量导入测点
     * @author jing.fang
     * @date 2022/3/21 16:39
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil importBatch(List<PointVO> list);

    /**
     * 导出测点数据
     *
     * @description 导出测点数据
     * @author jing.fang
     * @date 2022/3/21 14:36
     * @param projectId projectId
     * @param response response
     * @return void
    **/
    void exportBatch(Long projectId, HttpServletResponse response);

    /**
     * 测点启停
     *
     * @description 测点启停
     * @author jing.fang
     * @date 2022/1/14 17:58
     * @param point point
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil changeStatus(PointVO point);

    /**
     * 更新测点排序
     *
     * @description 更新测点排序
     * @author jing.fang
     * @date 2023/7/25 16:10
     * @param points points
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil changePointSeq(List<PointVO> points);

    /**
     * 查询测点集合
     *
     * @description 查询测点集合
     * @author jing.fang
     * @date 2022/3/16 10:42
     * @param missionId missionId
     * @return java.util.List<com.dzkj.biz.param_set.vo.PointVO>
    **/
    List<PointVO> queryByMissionId(Long missionId);
}
