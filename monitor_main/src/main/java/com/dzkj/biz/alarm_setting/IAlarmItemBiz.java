package com.dzkj.biz.alarm_setting;

import com.dzkj.biz.alarm_setting.vo.AlarmItemVO;
import com.dzkj.common.util.ResponseUtil;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author liao
 * @date 2021-09-09 9:24
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IAlarmItemBiz {

    /**
     * 查询
     *
     * @description 查询
     * @author wangy
     * @date 2021/9/2 16:06
     * @return java.util.List<com.dzkj.entity.alarm_setting.AlarmDistribute>
     **/
    List<AlarmItemVO> list(Long groupId);

    /**
     * 新增
     *
     * @description 新增/批量导入
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmItems alarmItems
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil add(List<AlarmItemVO> alarmItems);

    /**
     * 编辑
     *
     * @description 编辑
     * @author wangy
     * @date 2021/9/2 16:11
     * @param alarmItem AlarmItem
     * @return com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil update(AlarmItemVO alarmItem);

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
}
