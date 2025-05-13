package com.dzkj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.file_store.vo.PaperclipCond;
import com.dzkj.entity.File;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface IFileService extends IService<File> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/8/1 15:26
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.File>
     **/
    IPage<File> getPage(Integer pageIndex, Integer pageSize, PaperclipCond cond);

    /**
     * 更新分类id值
     *
     * @description 更新分类id值
     * @author jing.fang
     * @date 2023/8/1 15:12
     * @param fileIds fileIds
     * @param categoryId categoryId
     * @return: void
     **/
    void updateCategoryId(List<Long> fileIds, Long categoryId);

    /**
     * 获取单位已上传文件的所有大小集合
     *
     * @description 获取单位已上传文件的所有大小集合
     * @author jing.fang
     * @date 2023/8/1 15:40
     * @param companyId companyId
     * @return: java.lang.Long
     **/
    Long getAllSizeInCompany(Long companyId);

    /**
     * 查询公司下所有文件
     *
     * @description 查询公司下所有文件
     * @author jing.fang
     * @date 2023/8/4 8:40
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.entity.File>
     **/
    List<File> getByCompanyId(Long companyId);
}
