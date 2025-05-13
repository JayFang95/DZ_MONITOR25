package com.dzkj.biz.survey.impl;

import com.dzkj.biz.survey.ISurveyCycleBiz;
import com.dzkj.biz.survey.vo.SurveyCycleVo;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.service.survey.ISurveyCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/19 9:57
 * @description 策略测量业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class SurveyCycleBizImpl implements ISurveyCycleBiz {

    @Autowired
    private ISurveyCycleService surveyCycleService;

    @Override
    public List<SurveyCycleVo> getList(Long missionId) {
        return DzBeanUtils.listCopy(surveyCycleService.listByMissionId(missionId), SurveyCycleVo.class);
    }

    @Override
    public ResponseUtil add(SurveyCycleVo data) {
        boolean b = surveyCycleService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500,"新增失败");
    }

    @Override
    public ResponseUtil update(SurveyCycleVo data) {
        boolean b = surveyCycleService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500,"修改失败");
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = surveyCycleService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500,"删除失败");
    }

}
