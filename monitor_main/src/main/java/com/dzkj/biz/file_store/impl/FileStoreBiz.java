package com.dzkj.biz.file_store.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.file_store.IFileStoreBiz;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.biz.file_store.vo.FileStoreVO;
import com.dzkj.biz.file_store.vo.PaperclipCond;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.biz.vo.FileVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.FileUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.File;
import com.dzkj.entity.file_store.FileStore;
import com.dzkj.service.IFileService;
import com.dzkj.service.file_store.IFileStoreService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1 9:00
 * @description 文件存储业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class FileStoreBiz implements IFileStoreBiz {

    @Autowired
    private IFileStoreService fileStoreService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private ICommonBiz commonBiz;

    @Override
    public IPage<FileStoreVO> getPage(Integer pageIndex, Integer pageSize, FileStoreCond cond) {
        if (cond.getProjectIds() == null || cond.getProjectIds().size() == 0) {
            return new Page<>(pageIndex, pageIndex, 0);
        }
        if (cond.getTimeNum() != null){
            cond.setCreateTime(DateUtil.getDateOfMonth(new Date(), -cond.getTimeNum()));
        }
        return DzBeanUtils.pageCopy(fileStoreService.getPage(pageIndex, pageSize, cond), FileStoreVO.class);
    }

    @Override
    public ResponseUtil add(FileStoreVO data) {
        FileStore copy = DzBeanUtils.propertiesCopy(data, FileStore.class);
        boolean b = fileStoreService.save(copy);
        if (b){
            String fileId = data.getFileIds();
            if (StringUtils.isNotEmpty(fileId)){
                List<Long> fileIds = Arrays.stream(fileId.split(",")).map(Long::valueOf).collect(Collectors.toList());
                //变换临时文件目录
                List<File> files = fileService.listByIds(fileIds);
                for (File file : files) {
                    File copyFile = DzBeanUtils.propertiesCopy(file, File.class);
                    copyFile.setCategoryId(copy.getId());
                    Path path = FileUtil.getFilePath(file);
                    Path newPath = FileUtil.getFilePath(copyFile);
                    try {
                        String s = FileUtil.generateFolder(copyFile);
                        java.io.File filePath = new java.io.File(s);
                        if(!filePath.exists()){
                            filePath.mkdirs();
                        }
                        Files.copy(path, newPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        path.toFile().delete();
                    }
                }
                fileService.updateCategoryId(fileIds, copy.getId());
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(FileStoreVO data) {
        boolean b = fileStoreService.updateById(DzBeanUtils.propertiesCopy(data, FileStore.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        FileStore fileStore = fileStoreService.getById(id);
        boolean b = fileStoreService.removeById(id);
        if (b && StringUtils.isNotEmpty(fileStore.getFileIds())){
            List<Long> fileIds = Arrays.stream(fileStore.getFileIds().split(",")).map(Long::valueOf)
                    .collect(Collectors.toList());
            commonBiz.deleteFileByIds(fileIds);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public IPage<FileVO> getPagePaperclip(Integer pageIndex, Integer pageSize, PaperclipCond cond) {
        return DzBeanUtils.pageCopy(fileService.getPage(pageIndex, pageSize, cond), FileVO.class);
    }

    @Override
    public ResponseUtil uploadPaperclip(FileVO fileInfo, MultipartFile file) {
        fileInfo.setTemp(fileInfo.getCategoryId() == 0);
        //验证总容量是否超过1G
        Long totalCount= fileService.getAllSizeInCompany(fileInfo.getCompanyId());
        if ((totalCount + file.getSize()) > (1024L * 1024L * 1024L)){
            return ResponseUtil.failure("附件上传失败: 总附件大小超过1G");
        }
        return ResponseUtil.success(commonBiz.upload(DzBeanUtils.propertiesCopy(fileInfo, File.class), file));
    }

    @Override
    public String previewPaperclip(Long fileId) {
        return FileUtil.previewTextAndImageFile(fileService.getById(fileId));
    }

    @Override
    public void previewPdfPaperclip(Long fileId, HttpServletResponse response) {
        FileUtil.previewPdfFile(fileService.getById(fileId), response);
    }

    @Override
    public void downloadPaperclip(Long fileId, HttpServletResponse response) {
        commonBiz.download(fileId, response);
    }

    @Override
    public void downloadAllPaperclip(FileVO fileVO, HttpServletResponse response) {
        commonBiz.downloadAll(DzBeanUtils.propertiesCopy(fileVO, File.class), response);
    }

    @Override
    public boolean deletePaperclip(Long fileId) {
        return commonBiz.deleteFileByIds(Collections.singletonList(fileId));
    }

    @Override
    public void deleteTempPaperclip(FileCondition cond) {
        commonBiz.deleteTemp(cond);
    }
}
