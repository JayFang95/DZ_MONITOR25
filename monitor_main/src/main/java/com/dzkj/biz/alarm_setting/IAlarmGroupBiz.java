package com.dzkj.biz.alarm_setting;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.alarm_setting.vo.AlarmGroupVO;
import com.dzkj.biz.alarm_setting.vo.AlarmItemVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2021-09-09 9:25
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IAlarmGroupBiz {

    /**
     * 查询
     *
     * @description 查询
     * @author wangy
     * @date 2021/9/2 16:06
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmDistribute>
     **/
    IPage<AlarmGroupVO> list(Integer pi, Integer ps, List<Long> projectIds);

    /**
     * 新增
     *
     * @description 新增
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmGroup AlarmGroup
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(AlarmGroupVO alarmGroup);

    /**
     * 编辑
     *
     * @description 编辑
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmGroup AlarmGroup
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(AlarmGroupVO alarmGroup);

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
     * 导出报警组
     *
     * @author liao
     * @date 2021-10-08 16:28
     * @param projectId 公司id
    **/
    ResponseUtil exportGroup(Long projectId);

    /**
     * 导入报警项数据
     *
     * @description
     * @author jing.fang
     * @date 2022/4/7 16:47
     * @param itemList itemList
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil importGroup(List<AlarmItemVO> itemList);

    /**
     * 查询报警组列表
     *
     * @description 查询报警组列表
     * @author jing.fang
     * @date 2022/3/2 10:40
     * @param projectId projectId
     * @return java.util.List<com.dzkj.biz.vo.DropVO>
    **/
    List<DropVO> getAlarmGroupList(Long projectId);
}
