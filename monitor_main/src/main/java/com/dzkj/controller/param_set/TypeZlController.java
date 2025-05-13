package com.dzkj.controller.param_set;


import com.dzkj.biz.param_set.ITypeZlBiz;
import com.dzkj.biz.param_set.vo.TypeZlVO;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/22
 * @description 支撑类型业务controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/type_zl")
public class TypeZlController {

    @Autowired
    private ITypeZlBiz typeZlBiz;

    @RequestMapping(value = "list/{missionId}", method = RequestMethod.GET)
    public ResponseUtil getList(@PathVariable("missionId") Long missionId){
        return ResponseUtil.success(typeZlBiz.getList(missionId));
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public ResponseUtil add(@RequestBody TypeZlVO data){
        return typeZlBiz.add(data);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public ResponseUtil update(@RequestBody TypeZlVO data){
        return typeZlBiz.update(data);
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return typeZlBiz.delete(id);
    }

    @RequestMapping(value = "export/{missionId}", method = RequestMethod.GET)
    public void exportData(@PathVariable("missionId") Long missionId, HttpServletResponse response){
        typeZlBiz.exportData(missionId, response);
    }

    @RequestMapping(value = "import", method = RequestMethod.POST)
    public ResponseUtil importData(@RequestBody List<TypeZlVO> list){
        return typeZlBiz.importData(list);
    }

    /**
     * 导出计算公式
     */
    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void exportCalculateExcel(HttpServletResponse response){
        typeZlBiz.exportCalculateExcel(response);
    }
}
