package com.dzkj.service.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.entity.data.PushTaskRecord;
import com.dzkj.mapper.data_swap.PushTaskRecordMapper;
import com.dzkj.service.data.IPushTaskRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/9
 * @description 推送记录服务接口实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushTaskRecordServiceImpl extends ServiceImpl<PushTaskRecordMapper, PushTaskRecord> implements IPushTaskRecordService {

    @Override
    public boolean exitPushTaskRecord(Long missionId, Integer recycleNum) {
        LambdaQueryWrapper<PushTaskRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PushTaskRecord::getMissionId, missionId)
                .eq(PushTaskRecord::getRecycleNum, recycleNum);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
