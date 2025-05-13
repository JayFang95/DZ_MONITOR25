package com.dzkj.dataSwap.biz.impl;

import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushTaskCdBiz;
import com.dzkj.dataSwap.utils.DataSwapCdUtil;
import com.dzkj.dataSwap.vo.PushTaskCdVO;
import com.dzkj.service.data.IPushTaskCdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21 下午3:04
 * @description 推送任务业务-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class PushTaskCdBiz implements IPushTaskCdBiz {

    @Autowired
    private IPushTaskCdService pushTaskCdService;
    @Autowired
    private DataSwapCdUtil dataSwapCdUtil;

    @Override
    public List<PushTaskCdVO> queryList(Long companyId) {
        List<PushTaskCdVO> list = DzBeanUtils.listCopy(pushTaskCdService.queryList(companyId), PushTaskCdVO.class);
        for (PushTaskCdVO vo : list) {
            if (vo.getProjectIdCd() != null){
                //todo 获取项目名称
                String token = dataSwapCdUtil.getToken(vo.getUsername(), vo.getPassword(), vo.getStdUrl());
                System.out.println(token);
            }else{
                vo.setProjectNameCd("暂未设置");
            }
        }
        return list;
    }

    @Override
    public ResponseUtil add(PushTaskCdVO data) {
        data.setStatus(0);
        boolean b = pushTaskCdService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil edit(PushTaskCdVO data) {
        boolean b = pushTaskCdService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil startTask(Long id) {
        boolean b = pushTaskCdService.updateStatus(id, 1);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送开启失败");
    }

    @Override
    public ResponseUtil stopTask(Long id) {
        boolean b = pushTaskCdService.updateStatus(id, 0);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送暂停失败");
    }

    @Override
    public ResponseUtil deleteTask(Long id) {
        boolean b = pushTaskCdService.removeById(id);
        if (b){
            //todo 删除推送任务关联信息
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "任务推送删除失败");
    }
}
