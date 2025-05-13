package com.dzkj.biz.param_set;

import com.dzkj.biz.param_set.vo.SectionVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 断面业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface ISectionBiz {

    /**
     * 查询断面列表
     *
     * @description 查询断面列表
     * @author jing.fang
     * @date 2021/9/10 10:00
     * @param groupId groupId
     * @return java.util.List<com.dzkj.entity.param_set.Section>
    **/
    List<SectionVO> getList(Long groupId);

    /**
     * 新增断面
     *
     * @description 新增断面
     * @author jing.fang
     * @date 2021/9/10 10:00
     * @param section section
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(SectionVO section);

    /**
     * 修改断面
     *
     * @description 修改断面
     * @author jing.fang
     * @date 2021/9/10 10:00
     * @param section section
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(SectionVO section);

    /**
     * 删除断面
     *
     * @description 删除断面
     * @author jing.fang
     * @date 2021/9/10 10:00
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 计算方位角
     *
     * @description 计算方位角
     * @author jing.fang
     * @date 2022/7/18 10:04
     * @param section section
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil calculate(SectionVO section);
}
