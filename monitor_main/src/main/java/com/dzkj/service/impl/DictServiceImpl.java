package com.dzkj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.Dict;
import com.dzkj.mapper.DictMapper;
import com.dzkj.service.IDictService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典service接口实现
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {
    @Override
    public List<Dict> queryList(List<String> typeList) {
        return baseMapper.queryList(typeList);
    }
}
