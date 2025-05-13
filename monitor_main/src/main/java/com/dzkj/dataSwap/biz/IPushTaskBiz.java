package com.dzkj.dataSwap.biz;

import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.vo.PushTaskVO;

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
public interface IPushTaskBiz {

    /**
     * 推送任务查询
     *
     * @description 推送任务查询
     * @author jing.fang
     * @date 2023/6/6 14:30
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.data_swap.vo.PushTaskVO>
     **/
    List<PushTaskVO> queryList(Long companyId);

    /**
     * 新增推送任务
     *
     * @description 新增推送任务
     * @author jing.fang
     * @date 2023/6/6 14:33
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(PushTaskVO data);

    /**
     * 修改推送任务
     *
     * @description 修改推送任务
     * @author jing.fang
     * @date 2023/6/6 14:33
     * @param data data
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil edit(PushTaskVO data);

    /**
     * 开启任务推送
     *
     * @description 开启任务推送
     * @author jing.fang
     * @date 2023/6/7 21:14
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil startTask(Long id);

    /**
     * 暂停任务推送
     *
     * @description 暂停任务推送
     * @author jing.fang
     * @date 2023/6/7 21:14
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil stopTask(Long id);

    /**
     * 删除任务推送
     *
     * @description 删除任务推送
     * @author jing.fang
     * @date 2023/6/7 21:14
     * @param id id
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil deleteTask(Long id);
}
