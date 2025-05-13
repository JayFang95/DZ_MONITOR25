package com.dzkj.biz.param_set;

import com.dzkj.biz.param_set.vo.TypeZlVO;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 支撑类型业务
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface ITypeZlBiz {

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2022/3/28 15:46
     * @param missionId missionId
     * @return java.util.List<com.dzkj.biz.param_set.vo.TypeZlVO>
    **/
    List<TypeZlVO> getList(Long missionId);

    /**
     * 新增
     *
     * @description 新增
     * @author jing.fang
     * @date 2022/3/28 15:46
     * @param data data
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(TypeZlVO data);

    /**
     * 修改
     *
     * @description 修改
     * @author jing.fang
     * @date 2022/3/28 15:46
     * @param data data
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(TypeZlVO data);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2022/3/28 15:46
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 导出支撑类型参数
     *
     * @description 导出支撑类型参数
     * @author jing.fang
     * @date 2022/3/29 9:56
     * @param missionId missionId
     * @return void
    **/
    void exportData(Long missionId, HttpServletResponse response);

    /**
     * 导入支撑类型参数
     *
     * @description 导入支撑类型参数
     * @author jing.fang
     * @date 2022/3/29 9:57
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil importData(List<TypeZlVO> list);

    void exportCalculateExcel(HttpServletResponse response);
}
