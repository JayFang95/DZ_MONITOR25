package com.dzkj.biz.equipment.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.equipment.IControlBoxRecordBiz;
import com.dzkj.biz.equipment.vo.ControlBoxRecordVO;
import com.dzkj.biz.equipment.vo.CtlBoxRecordCondition;
import com.dzkj.common.util.DateUtil;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.service.equipment.IControlBoxRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/7/13 10:34
 * @description 控制器记录业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
@Slf4j
public class ControlBoxRecordBiz implements IControlBoxRecordBiz {

    @Autowired
    private IControlBoxRecordService controlBoxRecordService;

    @Override
    public IPage<ControlBoxRecordVO> getPage(Integer pageIndex, Integer pageSize, CtlBoxRecordCondition cond) {
        Integer timeNum = cond.getTimeNum();
        if(timeNum !=null){
            cond.setTime(DateUtil.getDateOfDay(new Date(), -timeNum));
        }
        return DzBeanUtils.pageCopy(controlBoxRecordService.getPage(pageIndex, pageSize, cond), ControlBoxRecordVO.class);
    }

}
