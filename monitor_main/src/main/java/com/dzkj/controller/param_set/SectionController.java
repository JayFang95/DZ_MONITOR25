package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.ISectionBiz;
import com.dzkj.biz.param_set.vo.SectionVO;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 参数设置-断面controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/section")
public class SectionController {

    @Autowired
    private ISectionBiz sectionBiz;

    @RequestMapping(value = "list/{groupId}", method = RequestMethod.GET)
    public ResponseUtil list(@PathVariable("groupId") Long groupId){
        return ResponseUtil.success(sectionBiz.getList(groupId));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseUtil add(@RequestBody SectionVO section){
        return sectionBiz.add(section);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseUtil update(@RequestBody SectionVO section){
        return sectionBiz.update(section);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return sectionBiz.delete(id);
    }

    @RequestMapping(value = "calculate", method = RequestMethod.POST)
    public ResponseUtil calculate(@RequestBody SectionVO section){
        return sectionBiz.calculate(section);
    }

}
