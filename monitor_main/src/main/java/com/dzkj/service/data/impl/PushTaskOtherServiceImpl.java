package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushTaskOtherVO;
import com.dzkj.entity.data.PushTaskOther;
import com.dzkj.mapper.data.PushTaskOtherMapper;
import com.dzkj.service.data.IPushTaskOtherService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18
 * @description 数据推送其他服务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushTaskOtherServiceImpl extends ServiceImpl<PushTaskOtherMapper, PushTaskOther> implements IPushTaskOtherService {

    @Override
    public List<PushTaskOther> queryList(Long companyId) {
        return baseMapper.queryList(companyId);
    }

    @Override
    public boolean add(PushTaskOtherVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushTaskOther.class)) > 0;
    }

    @Override
    public boolean edit(PushTaskOtherVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushTaskOther.class)) > 0;
    }

    @Override
    public boolean existWithMissionId(Long missionId) {
        LambdaQueryWrapper<PushTaskOther> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskOther::getMissionId, missionId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean updateStatus(Long id, int status) {
        LambdaUpdateWrapper<PushTaskOther> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PushTaskOther::getId, id).set(PushTaskOther::getStatus, status);
        return baseMapper.update(null, wrapper) > 0;
    }
}
