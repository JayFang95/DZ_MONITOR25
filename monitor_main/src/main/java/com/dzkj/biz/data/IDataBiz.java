package com.dzkj.biz.data;

import com.dzkj.biz.data.vo.UploadData;
import com.dzkj.biz.data.vo.UploadDataApp;
import com.dzkj.biz.data.vo.UploadDataCb;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDataBiz {

    /**
     * 手工上传数据
     *
     * @description 手工上传数据
     * @author jing.fang
     * @date 2022/3/30 18:09
     * @param uploadData uploadData
     * @param file file
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil upload(UploadData uploadData, MultipartFile file);

    /**
     * 模板下载
     *
     * @description 模板下载
     * @author jing.fang
     * @date 2022/3/31 15:58
     * @param missionId missionId
     * @param secondId secondId
     * @param response response
    **/
    void download(Long missionId, Long secondId, HttpServletResponse response);

    /**
     * 数据入库
     *
     * @description 数据入库
     * @author jing.fang
     * @date 2022/4/13 15:05
     * @param dataCb dataCb
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil saveToDb(UploadDataCb dataCb);

    /**
     * 数据上传app
     *
     * @description 数据上传app
     * @author jing.fang
     * @date 2024/8/8 14:06
     * @param uploadDataApp uploadDataApp
     * @param index index
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil uploadApp(int index, UploadDataApp uploadDataApp);

    /**
     * 获取支撑轴力上传数据模版
     *
     * @description 获取支撑轴力上传数据模版
     * @author jing.fang
     * @date 2024/8/8 16:01
     * @param groupId groupId
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil getZlUploadData(Long groupId);
}
