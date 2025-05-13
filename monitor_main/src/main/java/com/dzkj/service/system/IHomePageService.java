package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.HomePage;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IHomePageService extends IService<HomePage> {

    /**
     * 根据companyId查询
     *
     * @description: 根据companyId查询
     * @author: jing.fang
     * @Date: 2023/2/15 17:16
     * @param companyId  companyId
     * @return java.util.List<com.dzkj.entity.system.HomePage>
    **/
    List<HomePage> listByCompanyId(Long companyId);

    /**
     * 根据companyId删除
     *
     * @description: 根据companyId删除
     * @author: jing.fang
     * @Date: 2023/2/15 17:16
     * @param companyId  companyId
    **/
    boolean removeByCompanyId(Long companyId);
}
