package com.dzkj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.Dict;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IDictService extends IService<Dict> {
    /**
     * 查询指定字典
     * @author liao
     * @date 2021-09-27 15:21
     * @param typeList typeList
     * @return java.util.List<com.dzkj.entity.Dict>
     **/
    List<Dict> queryList(List<String> typeList);
}
