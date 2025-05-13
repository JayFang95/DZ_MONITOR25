package com.dzkj.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.system.Resource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 资源mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    List<Resource> findByRoleId(@Param("roleId") Long roleId);
}
