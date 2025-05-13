package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.dataSwap.vo.PushTaskOtherVO;
import com.dzkj.entity.data.PushTaskOther;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18
 * @description 数据推送其他服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushTaskOtherService extends IService<PushTaskOther> {

    /**
     * 查询推送列表
     *
     * @description 查询推送列表
     * @author jing.fang
     * @date 2024/11/18 11:29
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.entity.data.PushTaskOther>
     **/
    List<PushTaskOther> queryList(Long companyId);

    /**
     * 查询任务推送数据
     *
     * @description 查询任务推送数据
     * @author jing.fang
     * @date 2024/11/18 11:29
     * @param missionId missionId
     * @return: boolean
     **/
    boolean existWithMissionId(Long missionId);

    /**
     * 新增推送数据
     *
     * @description 新增推送数据
     * @author jing.fang
     * @date 2024/11/18 11:30
     * @param data data
     * @return: boolean
     **/
    boolean add(PushTaskOtherVO data);

    /**
     * 修改推送数据
     *
     * @description 修改推送数据
     * @author jing.fang
     * @date 2024/11/18 11:30
     * @param data data
     * @return: boolean
     **/
    boolean edit(PushTaskOtherVO data);

    /**
     * 更新推送状态
     *
     * @description 更新推送状态
     * @author jing.fang
     * @date 2024/11/18 11:30
     * @param id id
     * @param i 状态值
     * @return: boolean
     **/
    boolean updateStatus(Long id, int i);
}
