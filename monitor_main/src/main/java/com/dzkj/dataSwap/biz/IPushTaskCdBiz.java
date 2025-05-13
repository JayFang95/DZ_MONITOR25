package com.dzkj.dataSwap.biz;

import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.vo.PushTaskCdVO;

import java.util.List;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21 下午3:03
 * @description 推送任务业务接口-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushTaskCdBiz {

    /**
     * 查询推送任务列表
     *
     * @description 查询推送任务列表
     * @author jing.fang
     * @date 2025/2/24 下午1:53
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.dataSwap.vo.PushTaskCdVO>
     **/
    List<PushTaskCdVO> queryList(Long companyId);

    /**
     * 新增推送任务
     *
     * @description 新增推送任务
     * @author jing.fang
     * @date 2025/2/24 下午1:53
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(PushTaskCdVO data);

    /**
     * 修改推送任务
     *
     * @description 修改推送任务
     * @author jing.fang
     * @date 2025/2/24 下午1:53
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil edit(PushTaskCdVO data);

    /**
     * 开启推送任务
     *
     * @description 开启推送任务
     * @author jing.fang
     * @date 2025/2/24 下午1:53
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil startTask(Long id);

    /**
     * 暂停推送任务
     *
     * @description 暂停推送任务
     * @author jing.fang
     * @date 2025/2/24 下午1:53
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil stopTask(Long id);

    /**
     * 删除推送任务
     *
     * @description 删除推送任务
     * @author jing.fang
     * @date 2025/2/24 下午1:55
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/

    ResponseUtil deleteTask(Long id);
}
