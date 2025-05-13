package com.dzkj.biz;


import com.dzkj.biz.project.vo.CustomDisplayVO;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.File;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 通用业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface ICommonBiz {

    /**
     * 更新角色权限
     *
     * @description 更新角色权限
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @return: boolean
     **/
    boolean updateRoleMenu();

    /**
     * 文件上传
     *
     * @description 文件上传
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @param file file
     * @param multipartFile multipartFile
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     **/
    Map<String, Object> upload(File file, MultipartFile multipartFile);

    /**
     * 文件下载
     *
     * @description 文件下载
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @param fileId fileId
     * @param response response
     * @return: void
     **/
    void download(Long fileId, HttpServletResponse response);

    /**
     * 批量下载
     *
     * @description 批量下载
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @param file file
     * @param response response
     * @return: void
     **/
    void downloadAll(File file, HttpServletResponse response);

    /**
     * 删除文件
     *
     * @description 删除文件
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @param fileIds fileIds
     * @return: java.lang.Boolean
     **/
    Boolean deleteFileByIds(List<Long> fileIds);

    /**
     * 删除指定类型临时文件
     *
     * @description 删除指定类型临时文件
     * @author jing.fang
     * @date 2023/8/1 16:02
     * @param condition condition
     * @return: void
     **/
    void deleteTemp(FileCondition condition);

    /**
     * 根据category信息查询文件列表
     *
     * @description 根据category信息查询文件列表
     * @author jing.fang
     * @date 2023/8/1 16:03
     * @param file file
     * @return: java.util.List<com.dzkj.entity.File>
     **/
    List<File> getByCategoryInfo(File file);

    /**
     * 查询默认显示列表集合
     *
     * @description 查询默认显示列表集合
     * @author jing.fang
     * @date 2023/8/1 16:03
     * @return: java.util.List<com.dzkj.biz.project.vo.CustomDisplayVO>
     **/
    List<CustomDisplayVO> getDefaultDisplayList();

    /**
     * 查询指定任务显示列表
     *
     * @description 查询指定任务显示列表
     * @author jing.fang
     * @date 2023/8/1 16:03
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.biz.project.vo.CustomDisplayVO>
     **/
    List<CustomDisplayVO> getDisplayList(Long missionId);

    /**
     * 更新监测任务信息
     *
     * @description 更新监测任务信息
     * @author jing.fang
     * @date 2023/8/1 16:03
     * @param displayList displayList
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil updateDisplayList(List<CustomDisplayVO> displayList);

    /**
     * 删除旧ws连接
     * @param userId userId
     * @param createTime createTime
     */
    void deleteOldWs(Long userId, String createTime);
}
