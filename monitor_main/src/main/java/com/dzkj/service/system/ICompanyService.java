package com.dzkj.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.system.vo.CompanyCondition;
import com.dzkj.entity.system.Company;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 公司service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ICompanyService extends IService<Company> {

    /**
     * 分页查看列表
     *
     * @description: 分页查看列表
     * @author: jing.fang
     * @Date: 2023/2/15 16:47
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.system.Company>
    **/
    IPage<Company> getPage(int pageIndex, int pageSize, CompanyCondition condition);

    /**
     * 查询公司code是否存在
     *
     * @description: 查询公司code是否存在
     * @author: jing.fang
     * @Date: 2023/2/15 16:51
     * @param company  company
     * @return boolean
     **/
    boolean findCodeExist(Company company);

    /**
     * 查询当前单位id
     *
     * @description 查询当前单位id
     * @author jing.fang
     * @date 2021/9/3 15:16
     * @param
     * @return java.lang.Long
    **/
    Long getCurrentCompany();

    /**
     * 更新当前公司
     *
     * @description: 更新当前公司
     * @author: jing.fang
     * @Date: 2023/2/15 17:23
     * @param id  id
     * @return boolean
    **/
    boolean updateCurrent(Long id);

    /**
     * 更新公司激活状态
     *
     * @description: 更新公司激活状态
     * @author: jing.fang
     * @Date: 2023/2/15 17:27
     * @param id  id
     * @return boolean
    **/
    boolean updateStatus(Long id);
}
