package com.dzkj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.biz.file_store.vo.PaperclipCond;
import com.dzkj.entity.File;
import com.dzkj.mapper.FileMapper;
import com.dzkj.service.IFileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 附件service
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    @Override
    public IPage<File> getPage(Integer pageIndex, Integer pageSize, PaperclipCond cond) {
        Page<File> page = new Page<>(pageIndex, pageSize);
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getCategoryName, cond.getCategoryName())
                .eq(File::getCategoryId, cond.getCategoryId())
                .eq(File::getScopeName, cond.getScopeName())
                .orderByDesc(File::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateCategoryId(List<Long> fileIds, Long categoryId) {
        LambdaUpdateWrapper<File> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(File::getId, fileIds)
                .set(File::getCategoryId, categoryId).set(File::getTemp, false);
        baseMapper.update(null, wrapper);
    }

    @Override
    public Long getAllSizeInCompany(Long companyId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getCompanyId, companyId).select(File::getSize);
        List<File> list = baseMapper.selectList(wrapper);
        return list.stream().mapToLong(File::getSize)
                .filter(Objects::nonNull).sum();
    }

    @Override
    public List<File> getByCompanyId(Long companyId) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getCompanyId, companyId);
        return baseMapper.selectList(wrapper);
    }
}
