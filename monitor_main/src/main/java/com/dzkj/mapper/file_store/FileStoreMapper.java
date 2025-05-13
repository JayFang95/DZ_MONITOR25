package com.dzkj.mapper.file_store;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.entity.file_store.FileStore;
import org.apache.ibatis.annotations.Param;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1
 * @description 文件管理mapper
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface FileStoreMapper extends BaseMapper<FileStore> {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/8/1 15:01
     * @param page page
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.file_store.FileStore>
     **/
    IPage<FileStore> getPage(Page<FileStore> page, @Param("cond") FileStoreCond cond);
}
