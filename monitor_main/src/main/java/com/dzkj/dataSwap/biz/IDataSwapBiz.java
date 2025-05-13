package com.dzkj.dataSwap.biz;

import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.vo.MonitorDataMap;
import com.dzkj.dataSwap.vo.MonitorErrorVO;
import com.dzkj.dataSwap.vo.PushPointVO;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/4 13:44
 * @description 数据交换接口业务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IDataSwapBiz {

    /**
     * 监测点基础信息和初始数据上报
     *
     * @description 监测点基础信息和初始数据上报
     * @author jing.fang
     * @date 2023/6/7 15:27
     * @param pointList pointList
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil uploadPoint(List<PushPointVO> pointList);

    /**
     * 获取推送点最新采集数据
     *
     * @description 获取推送点最新采集数据
     * @author jing.fang
     * @date 2023/6/9 16:28
     * @param pointIds pointIds
     * @param missionId missionId
     * @return: com.dzkj.data_swap.vo.MonitorDataMap
     **/
    MonitorDataMap queryLatestMonitorData(List<Long> pointIds, Long missionId);

    /**
     * 验证监测周期数据是否已经推送
     *
     * @description 验证监测周期数据是否已经推送
     * @author jing.fang
     * @date 2023/6/9 17:38
     * @param missionId missionId
     * @param recycleNum recycleNum
     * @return: boolean
     **/
    boolean checkPushTask(Long missionId, Integer recycleNum);

    /**
     * 手动推送最新一期数据
     *
     * @param missionId     missionId
     * @param recycleNum    recycleNum
     * @param thirdPartType thirdPartType
     * @return
     */
    ResponseUtil manualPushMonitorData(Long missionId, Integer recycleNum, Integer thirdPartType);

    /**
     * 监测延测上报
     *
     * @description 监测延测上报
     * @author jing.fang
     * @date 2023/6/10 9:34
     * @param monitorErrorVO monitorErrorVO
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil uploadMonitorError(MonitorErrorVO monitorErrorVO);
}
