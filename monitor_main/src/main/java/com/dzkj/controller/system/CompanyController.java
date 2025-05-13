package com.dzkj.controller.system;


import com.dzkj.biz.system.ICompanyBiz;
import com.dzkj.biz.system.vo.CompanyCondition;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 单位授权controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt")
public class CompanyController {

    @Autowired
    private ICompanyBiz companyBiz;

    @RequestMapping(value = "company/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil page(@PathVariable("pageIndex") int pageIndex,
                             @PathVariable("pageSize") int pageSize,
                             @RequestBody CompanyCondition condition){
        return ResponseUtil.success(companyBiz.getPage(pageIndex, pageSize, condition));
    }

    @RequestMapping(value = "company/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil add(@RequestBody Company company){
        return companyBiz.add(company);
    }

    @RequestMapping(value = "company/update", method = RequestMethod.POST)
    @SysOperateLog(value = "编辑", type = LogConstant.UPDATE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil update(@RequestBody Company company){
        return companyBiz.update(company);
    }

    @RequestMapping(value = "company/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil update(@PathVariable("id") Long id){
        return companyBiz.delete(id);
    }

    @RequestMapping(value = "company/current/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "置为当前", type = LogConstant.UPDATE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil current(@PathVariable("id") Long id){
        return companyBiz.updateCurrent(id);
    }

    @RequestMapping(value = "company/status/{id}", method = RequestMethod.POST)
    @SysOperateLog(value = "启停变更", type = LogConstant.UPDATE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil status(@PathVariable("id") Long id){
        return companyBiz.updateStatus(id);
    }

    /**
     * 查询公司下拉集合
     */
    @RequestMapping(value = "common/company/drop", method = RequestMethod.GET)
    public ResponseUtil getCompany(){
        return ResponseUtil.success(companyBiz.getCompanyDrop());
    }

    @RequestMapping(value = "company/config/tree/{id}", method = RequestMethod.GET)
    @SysOperateLog(value = "功能配置查询", type = LogConstant.RETRIEVE, modelName = LogConstant.SYS_COMPANY)
    public ResponseUtil getCompanyConfigTree(@PathVariable("id") Long id){
        return ResponseUtil.success(companyBiz.getCompanyConfigTree(id));
    }
}
