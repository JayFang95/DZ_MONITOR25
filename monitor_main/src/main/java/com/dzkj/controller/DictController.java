package com.dzkj.controller;


import com.dzkj.biz.IDictBiz;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/dict")
public class DictController {
    @Autowired
    private IDictBiz dictBiz;

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public ResponseUtil queryDict(@RequestBody List<String> list){
        return ResponseUtil.success(dictBiz.queryDict(list));
    }
}
