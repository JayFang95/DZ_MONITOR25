package com.dzkj.dataSwap.biz.impl;

import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskBiz;
import com.dzkj.dataSwap.vo.PushTaskVO;
import com.dzkj.service.data.IPushPointJnService;
import com.dzkj.service.data.IPushPointService;
import com.dzkj.service.data.IPushTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6 14:18
 * @description 推送任务业务
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Service
public class PushTaskBizImpl implements IPushTaskBiz {

    @Autowired
    private IPushTaskService pushTaskService;
    @Autowired
    private IPushPointService pushPointService;
    @Autowired
    private IPushPointJnService pushPointJnService;

    @Override
    public List<PushTaskVO> queryList(Long companyId) {
        return DzBeanUtils.listCopy(pushTaskService.queryList(companyId), PushTaskVO.class);
    }

    @Override
    public ResponseUtil add(PushTaskVO data) {
        //新增时默认未推送状态
        data.setStatus(0);
        boolean b = pushTaskService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil edit(PushTaskVO data) {
        boolean b = pushTaskService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil startTask(Long id) {
        boolean b = pushTaskService.updateStatus(id, 1);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送开启失败");
    }

    @Override
    public ResponseUtil stopTask(Long id) {
        boolean b = pushTaskService.updateStatus(id, 0);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送暂停失败");
    }

    @Override
    public ResponseUtil deleteTask(Long id) {
        boolean b = pushTaskService.removeById(id);
        if (b){
            pushPointService.removeByTaskId(id);
            pushPointJnService.removeByTaskId(id);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送删除失败");
    }

}
