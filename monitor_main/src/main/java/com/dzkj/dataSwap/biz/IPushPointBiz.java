package com.dzkj.dataSwap.biz;

import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.dataSwap.vo.PushUploadParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6 14:18
 * @description 推送任务业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushPointBiz {

    /**
     * 推送测点详情列表
     *
     * @description 推送测点详情列表
     * @author jing.fang
     * @date 2023/6/7 9:06
     * @param taskId taskId
     * @return: java.util.List<com.dzkj.data_swap.vo.PushPointVO>
     **/
    List<PushPointVO> queryList(Long taskId);

    /**
     * 新增推送点
     *
     * @description 新增推送点
     * @author jing.fang
     * @date 2023/6/7 9:07
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(PushPointVO data);

    /**
     * 修改推送点
     *
     * @description 修改推送点
     * @author jing.fang
     * @date 2023/6/7 9:07
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil edit(PushPointVO data);

    /**
     * 删除推送点
     *
     * @description 删除推送点
     * @author jing.fang
     * @date 2023/6/7 9:08
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil delete(Long id);

    /**
     * 查询推送任务可推送测点列表
     *
     * @description 查询推送任务可推送测点列表
     * @author jing.fang
     * @date 2023/6/7 9:38
     * @param missionId missionId
     * @param pushTaskId pushTaskId
     * @param pointId pointId
     * @return: java.util.List<com.dzkj.biz.param_set.vo.PointVO>
     **/
    List<PointVO> listPoint(Long missionId, Long pushTaskId, Long pointId);

    /**
     * 批量添加推送点
     *
     * @description 批量添加推送点
     * @author jing.fang
     * @date 2024/12/30 12:55
     * @param uploadParam uploadParam
     * @param file file
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil uploadBatch(PushUploadParam uploadParam, MultipartFile file);

    /**
     * 推送点excel模板下载
     *
     * @description 推送点excel模板下载
     * @author jing.fang
     * @date 2024/12/30 12:57
     * @param pushTaskId pushTaskId
     * @param missionId missionId
     * @param response response
     * @return: void
     **/
    void download(Long pushTaskId, Long missionId, HttpServletResponse response);
}
