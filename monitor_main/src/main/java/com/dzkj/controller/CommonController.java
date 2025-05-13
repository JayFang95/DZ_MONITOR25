package com.dzkj.controller;

import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.project.vo.CustomDisplayVO;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 通用controller, 不记录日志
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@RestController
@RequestMapping("mt/common")
public class CommonController {

    @Autowired
    private ICommonBiz commonBiz;

    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public ResponseUtil updateRoleMenu(){
        return ResponseUtil.success(commonBiz.updateRoleMenu());
    }

    /**
     * 文件上传
     *
     * @date 2021/8/16 10:02
     * @param fileInfo file 文件信息
     * @param file multipartFile 文件对象
    **/
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public ResponseUtil upload(File fileInfo, MultipartFile file){
        return ResponseUtil.success(commonBiz.upload(fileInfo, file));
    }

    /**
     * 文件下载
     *
     * @date 2021/8/16 10:31
     * @param fileId fileId
     * @param response response
    **/
    @RequestMapping(value = "download/{id}", method = RequestMethod.GET)
    public void download(@PathVariable("id") Long fileId, HttpServletResponse response){
        commonBiz.download(fileId, response);
    }

    /**
     * 批量下载
     *
     * @date 2021/8/16 10:33
     * @param file file
     * @param response response
    **/
    @RequestMapping(value = "download/all", method = RequestMethod.POST)
    public void download(@RequestBody File file, HttpServletResponse response){
        commonBiz.downloadAll(file, response);
    }

    /**
     * 删除临时文件
     *
     * @description 删除临时文件
     * @author jing.fang
     * @date 2021/8/23 16:10
     * @param condition condition
    **/
    @RequestMapping(value = "temp", method = RequestMethod.POST)
    public void deleteTemp(@RequestBody FileCondition condition){
        commonBiz.deleteTemp(condition);
    }

    /**
     * 删除指定文件
     *
     * @description 删除指定文件
     * @author jing.fang
     * @date 2021/8/23 16:10
     * @param fileIds 需要删除的文件id集合
     **/
    @RequestMapping(value = "file/delete", method = RequestMethod.POST)
    public void deleteFile(@RequestBody ArrayList<Long> fileIds){
        commonBiz.deleteFileByIds(fileIds);
    }

    /**
     * 查询自定义显示默认集合
     *
     * @date 2022/1/12 15:26
    **/
    @RequestMapping(value = "display/list", method = RequestMethod.GET)
    public ResponseUtil getDefaultDisplayList(){
        return ResponseUtil.success(commonBiz.getDefaultDisplayList());
    }

    /**
     * 查询监测任务显示集合
     *
     * @date 2022/1/12 15:26
     **/
    @RequestMapping(value = "display/list/{missionId}", method = RequestMethod.GET)
    public ResponseUtil getDisplayList(@PathVariable("missionId") Long missionId){
        return ResponseUtil.success(commonBiz.getDisplayList(missionId));
    }

    /**
     * 更新监测任务显示信息
     *
     * @date 2022/1/12 15:45
     * @param displayList displayList
     **/
    @RequestMapping(value = "display/update", method = RequestMethod.POST)
    public ResponseUtil updateDisplayList(@RequestBody List<CustomDisplayVO> displayList){
        return commonBiz.updateDisplayList(displayList);
    }

    /**
     * 删除旧连接
     * @param createTime createTime
     */
    @RequestMapping(value = "ws/delete/{userId}/{createTime}", method = RequestMethod.DELETE)
    public ResponseUtil deleteOldWs(@PathVariable("userId") Long userId, @PathVariable("createTime") String createTime){
        commonBiz.deleteOldWs(userId, createTime);
        return ResponseUtil.success();
    }

}
