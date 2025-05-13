package com.dzkj.biz.system;

import com.dzkj.entity.system.Online;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description 在线情况业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IOnlineBiz {

   /**
    * 查询在线用户列表
    *
    * @description
    * @author jing.fang
    * @date 2022/7/7 11:47
    * @param companyId companyId
    * @return java.util.List<com.dzkj.entity.system.Online>
   **/
   List<Online> getOnLines(Long companyId);
}
