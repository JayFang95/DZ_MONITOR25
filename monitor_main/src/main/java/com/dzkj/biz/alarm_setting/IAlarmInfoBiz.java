package com.dzkj.biz.alarm_setting;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.alarm_setting.vo.AlarmDetailCond;
import com.dzkj.biz.alarm_setting.vo.AlarmDetailVO;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoCondition;
import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IAlarmInfoBiz {

    /**
     * 分页查询
     *
     * @description 分页查询
     * @author jing.fang
     * @date 2022/3/30 9:20
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.alarm_setting.vo.AlarmInfoVO>
    **/
    IPage<AlarmInfoVO> page(Integer pageIndex, Integer pageSize, AlarmInfoCondition condition);

    /**
     * 处理报警
     *
     * @description 处理报警
     * @author jing.fang
     * @date 2022/3/30 11:06
     * @param data data
     * @return boolean
    **/
    boolean handel(AlarmInfoVO data);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2022/3/30 11:17
     * @param id id
     * @return boolean
    **/
    boolean delete(Long id);

    /**
     * 删除
     *
     * @description 删除
     * @author jing.fang
     * @date 2022/3/30 11:17
     * @param ids ids
     * @return boolean
     **/
    boolean delete(List<Long> ids);

    /**
     * 报警详情查看
     *
     * @description 报警详情查看
     * @author jing.fang
     * @date 2022/5/7 13:45
     * @param cond cond
     * @return com.dzkj.biz.alarm_setting.vo.AlarmDetailVO
    **/
    AlarmDetailVO detail(AlarmDetailCond cond);
}
