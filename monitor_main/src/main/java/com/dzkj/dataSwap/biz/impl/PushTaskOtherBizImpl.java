package com.dzkj.dataSwap.biz.impl;

import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskOtherBiz;
import com.dzkj.dataSwap.vo.PushTaskOtherVO;
import com.dzkj.service.data.IPushTaskOtherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18 10:42
 * @description 任务推送其他业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class PushTaskOtherBizImpl implements IPushTaskOtherBiz {

    @Autowired
    private IPushTaskOtherService pushTaskOtherService;

    @Override
    public List<PushTaskOtherVO> queryList(Long companyId) {
        return DzBeanUtils.listCopy(pushTaskOtherService.queryList(companyId), PushTaskOtherVO.class);
    }

    @Override
    public ResponseUtil add(PushTaskOtherVO data) {
        if (pushTaskOtherService.existWithMissionId(data.getMissionId())) {
            return ResponseUtil.failure(500, "重复创建: 关联任务数据推送已存在!");
        }
        //新增时默认未推送状态
        data.setStatus(0);
        boolean b = pushTaskOtherService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil edit(PushTaskOtherVO data) {
        boolean b = pushTaskOtherService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil startTask(Long id) {
        boolean b = pushTaskOtherService.updateStatus(id, 1);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送开启失败");
    }

    @Override
    public ResponseUtil stopTask(Long id) {
        boolean b = pushTaskOtherService.updateStatus(id, 0);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送暂停失败");
    }

    @Override
    public ResponseUtil deleteTask(Long id) {
        boolean b = pushTaskOtherService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送删除失败");
    }
}
