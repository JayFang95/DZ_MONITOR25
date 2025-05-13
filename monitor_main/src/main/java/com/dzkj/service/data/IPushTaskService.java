package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.dataSwap.vo.PushTaskVO;
import com.dzkj.entity.data.PushTask;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送任务服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushTaskService extends IService<PushTask> {

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2023/6/6 14:51
     * @param companyId companyId
     * @return: List<PushTask>
     **/
    List<PushTask> queryList(Long companyId);

    /**
     *
     * 添加推送任务
     *
     * @description 添加推送任务
     * @author jing.fang
     * @date 2023/6/6 14:56
     * @param data data
     * @return:
     **/
    boolean add(PushTaskVO data);

    /**
     *
     * 修改推送任务
     *
     * @description 修改推送任务
     * @author jing.fang
     * @date 2023/6/6 14:56
     * @param data data
     * @return:
     **/
    boolean edit(PushTaskVO data);

    /**
     * 更新推送任务状态
     *
     * @description 更新推送任务状态
     * @author jing.fang
     * @date 2023/6/7 21:18
     * @param id id
     * @param status status
     * @return: boolean
     **/
    boolean updateStatus(Long id, int status);

    /**
     * 根据项目code查询推送信息
     *
     * @description 根据项目code查询推送信息
     * @author jing.fang
     * @date 2024/12/5 10:32
     * @param projectCode projectCode
     * @return: com.dzkj.entity.data.PushTask
     **/
    PushTask getByCode(String projectCode);
}
