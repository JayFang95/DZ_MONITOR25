package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushPointJnVO;
import com.dzkj.entity.data.PushPointJn;
import com.dzkj.mapper.data.PushPointJnMapper;
import com.dzkj.service.data.IPushPointJnService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/9
 * @description 推送测点服务实现-济南局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushPointJnServiceImpl extends ServiceImpl<PushPointJnMapper, PushPointJn> implements IPushPointJnService {

    @Override
    public List<PushPointJn> queryList(Long taskId) {
        LambdaQueryWrapper<PushPointJn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPointJn::getPushTaskId, taskId);
        return baseMapper.queryList(taskId);
    }

    @Override
    public boolean add(PushPointJnVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushPointJn.class)) > 0;
    }

    @Override
    public boolean edit(PushPointJnVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushPointJn.class)) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return baseMapper.deleteById(id) > 0;
    }


    @Override
    public boolean removeByTaskId(Long pushTaskId) {
        LambdaQueryWrapper<PushPointJn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushPointJn::getPushTaskId, pushTaskId);
        return baseMapper.delete(wrapper) > 0;
    }

    @Override
    public List<PushPointJn> queryByMissionId(Long missionId) {
        return baseMapper.queryByMissionId(missionId) ;
    }


}
