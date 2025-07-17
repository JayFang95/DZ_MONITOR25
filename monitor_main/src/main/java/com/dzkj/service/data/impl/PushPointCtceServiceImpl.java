package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushPointCtceVO;
import com.dzkj.entity.data.PushPointCtce;
import com.dzkj.mapper.data.PushPointCtceMapper;
import com.dzkj.service.data.IPushPointCtceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/9
 * @description 推送测点服务实现-中铁四局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushPointCtceServiceImpl extends ServiceImpl<PushPointCtceMapper, PushPointCtce> implements IPushPointCtceService {

    @Override
    public List<PushPointCtce> queryList(Long taskId) {
        LambdaQueryWrapper<PushPointCtce> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPointCtce::getPushTaskId, taskId);
        return baseMapper.queryList(taskId);
    }

    @Override
    public boolean add(PushPointCtceVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushPointCtce.class)) > 0;
    }

    @Override
    public boolean edit(PushPointCtceVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushPointCtce.class)) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }


    @Override
    public boolean removeByTaskId(Long pushTaskId) {
        LambdaQueryWrapper<PushPointCtce> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPointCtce::getPushTaskId, pushTaskId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PushPointCtce> queryByMissionId(Long missionId) {
        return baseMapper.queryByMissionId(missionId) ;
    }


}
