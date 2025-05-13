package com.dzkj.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.system.Online;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/23
 * @description 在线情况mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface OnlineMapper extends BaseMapper<Online> {

    List<Long> getOnlineUserIds(@Param("companyId") Long companyId);
}
