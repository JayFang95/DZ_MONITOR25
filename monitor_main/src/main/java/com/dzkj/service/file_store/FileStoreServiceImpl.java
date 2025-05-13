package com.dzkj.service.file_store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.entity.file_store.FileStore;
import com.dzkj.mapper.file_store.FileStoreMapper;
import org.springframework.stereotype.Service;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1
 * @description 文件管理服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class FileStoreServiceImpl extends ServiceImpl<FileStoreMapper, FileStore> implements IFileStoreService {

    @Override
    public IPage<FileStore> getPage(Integer pageIndex, Integer pageSize, FileStoreCond cond) {
        Page<FileStore> page = new Page<>(pageIndex, pageSize);
        return baseMapper.getPage(page, cond);
    }
}
