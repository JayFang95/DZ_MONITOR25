package com.dzkj.biz.file_store;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.file_store.vo.FileStoreCond;
import com.dzkj.biz.file_store.vo.FileStoreVO;
import com.dzkj.biz.file_store.vo.PaperclipCond;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.biz.vo.FileVO;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1 9:00
 * @description 文件存储业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IFileStoreBiz {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2023/8/1 9:07
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.file_store.vo.FileStoreVO>
     **/
    IPage<FileStoreVO> getPage(Integer pageIndex, Integer pageSize, FileStoreCond cond);

    /**
     * 保存文件存储信息
     *
     * @description 保存文件存储信息
     * @author jing.fang
     * @date 2023/8/1 9:19
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(FileStoreVO data);

    /**
     * 保存文件存储信息
     *
     * @description 保存文件存储信息
     * @author jing.fang
     * @date 2023/8/1 9:19
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(FileStoreVO data);

    /**
     * 删除文件存储对象
     *
     * @description 删除文件存储对象
     * @author jing.fang
     * @date 2023/8/1 9:20
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil delete(Long id);

    /**
     * 文件存储附件查看
     *
     * @description 文件存储附件查看
     * @author jing.fang
     * @date 2023/8/1 9:22
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param cond cond
     * @return: Page<FileVO>
     **/
    IPage<FileVO> getPagePaperclip(Integer pageIndex, Integer pageSize, PaperclipCond cond);

    /**
     * 附件上传
     *
     * @description 附件上传
     * @author jing.fang
     * @date 2023/8/1 14:40
     * @param fileInfo fileInfo
     * @param file file
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil uploadPaperclip(FileVO fileInfo, MultipartFile file);

    /**
     * 文件预览
     *
     * @description 文件预览
     * @author jing.fang
     * @date 2023/8/1 14:41
     * @param fileId fileId
     * @return: java.lang.String
     **/
    String previewPaperclip(Long fileId);

    /**
     * PDF格式文件预览
     *
     * @description PDF格式文件预览
     * @author jing.fang
     * @date 2023/8/1 14:42
     * @param fileId fileId
     * @param response response
     * @return: void
     **/
    void previewPdfPaperclip(Long fileId, HttpServletResponse response);

    /**
     * 文件下载
     *
     * @description 文件下载
     * @author jing.fang
     * @date 2023/8/1 14:42
     * @param fileId fileId
     * @param response response
     * @return: void
     **/
    void downloadPaperclip(Long fileId, HttpServletResponse response);

    /**
     * 文件打包下载
     *
     * @description 文件打包下载
     * @author jing.fang
     * @date 2023/8/1 14:43
     * @param fileVO fileVO
     * @param response response
     * @return: void
     **/
    void downloadAllPaperclip(FileVO fileVO, HttpServletResponse response);

    /**
     * 附件删除
     *
     * @description 附件删除
     * @author jing.fang
     * @date 2023/8/1 14:47
     * @param fileId fileId
     * @return: boolean
     **/
    boolean deletePaperclip(Long fileId);

    /**
     * 临时附件删除
     *
     * @description 临时附件删除
     * @author jing.fang
     * @date 2023/8/1 14:44
     * @param cond cond
     * @return: void
     **/
    void deleteTempPaperclip(FileCondition cond);

}
