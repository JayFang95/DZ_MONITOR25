package com.dzkj.biz.param_set;

import com.dzkj.biz.param_set.vo.SensorZlVO;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 传感器信息业务
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface ISensorZlBiz {

    /**
     * 列表查询
     *
     * @description 列表查询
     * @author jing.fang
     * @date 2022/3/29 14:39
     * @param pointId pointId
     * @return java.util.List<com.dzkj.biz.param_set.vo.SensorZlVO>
    **/
    List<SensorZlVO> getList(Long pointId);

    /**
     * 新增
     *
     * @description 新增
     * @author jing.fang
     * @date 2022/3/29 14:39
     * @param data data
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(SensorZlVO data);

    /**
     * 修改
     *
     * @description 修改
     * @author jing.fang
     * @date 2022/3/29 14:39
     * @param data data
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(SensorZlVO data);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2022/3/29 14:39
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 导出
     *
     * @description
     * @author jing.fang
     * @date 2022/3/29 14:40
     * @param missionId missionId
     * @param response response
     * @return void
    **/
    void exportData(Long missionId, HttpServletResponse response);

    /**
     * 导入
     *
     * @description 导入
     * @author jing.fang
     * @date 2022/3/29 14:40
     * @param missionId
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil importData(Long missionId, List<SensorZlVO> list);

    /**
     * 查询工程下列表数据
     *
     * @description 查询工程下列表数据
     * @author jing.fang
     * @date 2022/3/29 16:36
     * @param projectId projectId
     * @return java.util.List<com.dzkj.biz.param_set.vo.SensorZlVO>
    **/
    List<SensorZlVO> getListInProject(Long projectId);
}
