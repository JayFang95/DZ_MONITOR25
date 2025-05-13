package com.dzkj.mapper.equipment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.equipment.vo.EquipCondition;
import com.dzkj.entity.equipment.ControlBox;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.survey.RobotSurveyRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/14
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface ControlBoxMapper extends BaseMapper<ControlBox> {

    /**
     * 分页查询
     *
     * @description: 分页查询
     * @author: jing.fang
     * @Date: 2023/2/14 14:22
     * @param page page
     * @param cond cond
     * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.dzkj.entity.equipment.ControlBox>
    **/
    Page<ControlBox> getPage(Page<ControlBox> page, @Param("cond")  EquipCondition cond);

    /**
     * 列表查询
     * @param cond cond
     * @return List<ControlBox>
     */
    List<ControlBox> getList(@Param("cond")  EquipCondition cond);

    /**
     * 查询测量记录信息
     * @param controlBoxId controlBoxId
     * @return RobotSurveyRecord
     */
    RobotSurveyRecord getSurveyRecordInfoByControlBoxId(@Param("controlBoxId") Long controlBoxId);
}
