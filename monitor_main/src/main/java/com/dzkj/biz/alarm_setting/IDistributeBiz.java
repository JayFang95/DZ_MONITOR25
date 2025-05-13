package com.dzkj.biz.alarm_setting;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.alarm_setting.vo.AlarmDistributeVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/6
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDistributeBiz {

    /**
     * 查询
     *
     * @description 查询
     * @author wangy
     * @date 2021/9/2 16:06
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmDistribute>
    **/
    IPage<AlarmDistributeVO> list(Integer pi, Integer ps, List<Long> projectIds);

    /**
     * 新增
     *
     * @description 新增
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmDistribute AlarmDistribute
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil add(AlarmDistributeVO alarmDistribute);

    /**
     * 编辑
     *
     * @description 编辑
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmDistribute AlarmDistribute
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil update(AlarmDistributeVO alarmDistribute);

    /**
     * 删除
     *
     * @description 删除
     * @author wangy
     * @date 2021/9/2 16:11
     * @param id id
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil delete(Long id);

    /**
     * 查询分发规则集合
     *
     * @description 查询分发规则集合
     * @author jing.fang
     * @date 2022/3/2 10:43
     * @param projectId projectId
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getAlarmDistributeList(Long projectId);

    /**
     * 导入报警规则
     *
     * @description 导入报警规则
     * @author jing.fang
     * @date 2022/4/7 17:23
     * @param list list
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil importDistribute(List<AlarmDistributeVO> list);
}
