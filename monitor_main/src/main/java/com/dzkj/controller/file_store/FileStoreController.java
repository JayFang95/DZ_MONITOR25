package com.dzkj.controller.file_store;


import com.dzkj.biz.file_store.IFileStoreBiz;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.biz.file_store.vo.FileStoreVO;
import com.dzkj.biz.file_store.vo.PaperclipCond;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.biz.vo.FileVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1
 * @description 文件管理控制器
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class FileStoreController {

    @Autowired
    private IFileStoreBiz fileStoreBiz;

    @RequestMapping(value = "file_store/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "查询", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil page(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody FileStoreCond cond){
        return ResponseUtil.success(fileStoreBiz.getPage(pageIndex, pageSize, cond));
    }

    @RequestMapping(value = "file_store/add", method = RequestMethod.POST)
    @SysOperateLog(value = "新增", type = LogConstant.CREATE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil add(@RequestBody FileStoreVO data){
        return fileStoreBiz.add(data);
    }

    @RequestMapping(value = "file_store/update", method = RequestMethod.POST)
    @SysOperateLog(value = "修改", type = LogConstant.UPDATE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil update(@RequestBody FileStoreVO data){
        return fileStoreBiz.update(data);
    }

    @RequestMapping(value = "file_store/delete/{id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "删除", type = LogConstant.DELETE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil delete(@PathVariable("id") Long id){
        return fileStoreBiz.delete(id);
    }

    @RequestMapping(value = "file_store/paperclip/page/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    @SysOperateLog(value = "附件查询", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil pagePaperclip(@PathVariable("pageIndex") Integer pageIndex,
                             @PathVariable("pageSize") Integer pageSize,
                             @RequestBody PaperclipCond cond){
        return ResponseUtil.success(fileStoreBiz.getPagePaperclip(pageIndex, pageSize, cond));
    }

    @RequestMapping(value = "common/paperclip/upload", method = RequestMethod.POST)
    @SysOperateLog(value = "附件上传", type = LogConstant.CREATE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil uploadPaperclip(FileVO fileInfo, MultipartFile file){
        return fileStoreBiz.uploadPaperclip(fileInfo, file);
    }

    @RequestMapping(value = "common/paperclip/preview/{file_id}", method = RequestMethod.GET)
    @SysOperateLog(value = "附件预览", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil previewPaperclip(@PathVariable("file_id") Long fileId){
        return ResponseUtil.success(fileStoreBiz.previewPaperclip(fileId));
    }

    @RequestMapping(value = "common/paperclip/preview_pdf/{file_id}", method = RequestMethod.GET)
    @SysOperateLog(value = "pdf附件预览", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public void previewPdfPaperclip(@PathVariable("file_id") Long fileId, HttpServletResponse response){
        fileStoreBiz.previewPdfPaperclip(fileId, response);
    }

    @RequestMapping(value = "common/paperclip/download/{fileId}", method = RequestMethod.GET)
    @SysOperateLog(value = "附件下载", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public void downloadPaperclip(@PathVariable("fileId") Long fileId, HttpServletResponse response){
        fileStoreBiz.downloadPaperclip(fileId, response);
    }

    @RequestMapping(value = "common/paperclip/download_all", method = RequestMethod.POST)
    @SysOperateLog(value = "附件打包下载", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public void downloadAllPaperclip(@RequestBody FileVO fileVO, HttpServletResponse response){
        fileStoreBiz.downloadAllPaperclip(fileVO, response);
    }

    @RequestMapping(value = "common/paperclip/delete/{file_id}", method = RequestMethod.DELETE)
    @SysOperateLog(value = "附件删除", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil deletePaperclip(@PathVariable("file_id") Long fileId){
        return ResponseUtil.success(fileStoreBiz.deletePaperclip(fileId));
    }

    @RequestMapping(value = "common/paperclip/delete_temp", method = RequestMethod.POST)
    @SysOperateLog(value = "临时附件删除", type = LogConstant.RETRIEVE, modelName = LogConstant.FILE_STORE)
    public ResponseUtil deleteTempPaperclip(@RequestBody FileCondition cond){
        fileStoreBiz.deleteTempPaperclip(cond);
        return ResponseUtil.success();
    }

}
