package com.dzkj.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.system.Online;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IOnlineService extends IService<Online> {
   List<Online> getOnlines(Long companyId);

   /**
    * 查询指定公司在线清况列表
    *
    * @description 查询指定公司在线清况列表
    * @author jing.fang
    * @date 2021/9/22 16:14
    * @param companyId companyId
    * @return java.util.List<java.lang.Long>
   **/
    List<Long> getOnlineUserIds(Long companyId);
}
