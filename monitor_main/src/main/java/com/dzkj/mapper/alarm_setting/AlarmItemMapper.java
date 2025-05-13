package com.dzkj.mapper.alarm_setting;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.alarm_setting.AlarmItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/26
 * @description 报警细项
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface AlarmItemMapper extends BaseMapper<AlarmItem> {

    List<AlarmItem> findByPid(@Param("pid") Long pid);

    List<AlarmItem> getByPidList(@Param("list") List<Long> list);
}
