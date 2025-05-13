package com.dzkj.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.entity.system.MonitorType;
import com.dzkj.mapper.system.MonitorTypeMapper;
import com.dzkj.service.system.IMonitorTypeService;
import org.springframework.stereotype.Service;

 /**
  * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
  *
  * @author jing.fang
  * @date 2021/8/17
  * @description note
  * history
  * <author>    <time>    <version>    <desc>
  *  作者姓名     修改时间     版本号        描述
  */
@Service
public class MonitorTypeServiceImpl extends ServiceImpl<MonitorTypeMapper, MonitorType> implements IMonitorTypeService {

   @Override
   public boolean checkTypeName(MonitorType monitorType) {
      LambdaQueryWrapper<MonitorType> wrapper = new LambdaQueryWrapper<>();
      wrapper.eq(MonitorType::getPName, monitorType.getPName())
              .eq(MonitorType::getName, monitorType.getName())
              .ne(monitorType.getId()!=null, MonitorType::getId, monitorType.getId());
      return baseMapper.selectCount(wrapper) > 0;
   }

 }
