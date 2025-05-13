package com.dzkj.service.file_store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.entity.file_store.FileStore;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1
 * @description 文件管理服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IFileStoreService extends IService<FileStore> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/8/1 14:58
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.file_store.FileStore>
     **/
    IPage<FileStore> getPage(Integer pageIndex, Integer pageSize, FileStoreCond cond);
}
