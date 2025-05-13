package com.dzkj.biz.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.system.vo.CompanyCondition;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Company;

import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/4
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface ICompanyBiz {

    /**
     * 分页查询
     *
     * @description
     * @author jing.fang
     * @date 2021/8/4 18:26
     * @param pageIndex 索引页
     * @param pageSize 每页显示条目
     * @param condition 查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.system.Company>
    **/
    IPage<Company> getPage(int pageIndex, int pageSize, CompanyCondition condition);

    /**
     * 新增单位授权
     *
     * @description 新增单位授权
     * @author jing.fang
     * @date 2021/8/5 8:35
     * @param company company
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(Company company);

    /**
     * 更新单位授权
     *
     * @description 更新单位授权
     * @author jing.fang
     * @date 2021/8/5 8:35
     * @param company company
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(Company company);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2021/8/5 8:35
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 置为当前
     *
     * @description 置为当前
     * @author jing.fang
     * @date 2021/8/5 8:35
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateCurrent(Long id);

    /**
     * 激活/终止授权
     *
     * @description 激活/终止授权
     * @author jing.fang
     * @date 2021/8/5 8:36
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil updateStatus(Long id);

    /**
     * 查询公司集合
     *
     * @description 查询公司集合
     * @author jing.fang
     * @date 2022/3/2 10:15
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getCompanyDrop();

    /**
     * 公司配置查询
     *
     * @description 公司配置查询
     * @author jing.fang
     * @date 2023/8/3 14:42
     * @param id id
     * @return: java.util.Map
     **/
    Map<String, Object> getCompanyConfigTree(Long id);
}
