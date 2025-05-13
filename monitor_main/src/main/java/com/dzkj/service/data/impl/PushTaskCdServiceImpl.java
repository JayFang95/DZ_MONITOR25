package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.dataSwap.vo.PushTaskCdVO;
import com.dzkj.entity.data.PushTaskCd;
import com.dzkj.mapper.data_swap.PushTaskCdMapper;
import com.dzkj.service.data.IPushTaskCdService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21
 * @description 推送任务服务-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushTaskCdServiceImpl extends ServiceImpl<PushTaskCdMapper, PushTaskCd> implements IPushTaskCdService {

    @Override
    public List<PushTaskCd> queryList(Long companyId) {
        LambdaQueryWrapper<PushTaskCd> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskCd::getCompanyId, companyId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean add(PushTaskCdVO data) {
        return baseMapper.insert(DzBeanUtils.propertiesCopy(data, PushTaskCd.class)) > 0;
    }

    @Override
    public boolean edit(PushTaskCdVO data) {
        return baseMapper.updateById(DzBeanUtils.propertiesCopy(data, PushTaskCd.class)) > 0;
    }

    @Override
    public boolean updateStatus(Long id, int status) {
        LambdaUpdateWrapper<PushTaskCd> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PushTaskCd::getId, id).set(PushTaskCd::getStatus, status);
        return baseMapper.update(null, wrapper) > 0;
    }
}
