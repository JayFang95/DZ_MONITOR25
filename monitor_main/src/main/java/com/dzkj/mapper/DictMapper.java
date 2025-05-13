package com.dzkj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 字典查询
     *
     * @description: 字典查询
     * @author: jing.fang
     * @Date: 2023/2/14 14:32
     * @param typeList
     * @return java.util.List<com.dzkj.entity.Dict>
    **/
    List<Dict> queryList(@Param("list") List<String> typeList);
}
