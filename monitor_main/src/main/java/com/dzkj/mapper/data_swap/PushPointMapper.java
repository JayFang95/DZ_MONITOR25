package com.dzkj.mapper.data_swap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.dataSwap.vo.PushPointVO;
import com.dzkj.entity.data.PushPoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送点mapper
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface PushPointMapper extends BaseMapper<PushPoint> {

    /**
     * 查询是否存在和当前设备编号和类型不一致数据
     *
     * @description 查询是否存在和当前设备编号和类型不一致数据
     * @author jing.fang
     * @date 2023/6/7 16:22
     * @param data data
     * @return: java.util.List<com.dzkj.entity.data.PushPoint>
     **/
    int findCodeAndDeviceCategoryNotSame(PushPointVO data);

    /**
     * 查询推送点信息
     *
     * @description 查询推送点信息
     * @author jing.fang
     * @date 2023/6/9 14:16
     * @param missionId missionId
     * @return: java.util.List<com.dzkj.entity.data.PushPoint>
     **/
    List<PushPoint> queryByMissionId(@Param("missionId") Long missionId);
}
