package com.dzkj.dataSwap.biz.impl;

import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.dataSwap.biz.IPushPointJnBiz;
import com.dzkj.dataSwap.vo.PushPointJnVO;
import com.dzkj.entity.data.PushPointJn;
import com.dzkj.service.data.IPushPointJnService;
import com.dzkj.service.param_set.IPointService;
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
public class PushPointJnBizImpl implements IPushPointJnBiz {

    @Autowired
    private IPushPointJnService pushPointService;
    @Autowired
    private IPointService pointService;

    @Override
    public List<PushPointJnVO> queryList(Long taskId) {
        return DzBeanUtils.listCopy(pushPointService.queryList(taskId), PushPointJnVO.class);
    }

    @Override
    public ResponseUtil add(PushPointJnVO data) {
        boolean b = pushPointService.add(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil addBatch(List<PushPointJnVO> list){
        boolean b = pushPointService.saveBatch(DzBeanUtils.listCopy(list, PushPointJn.class));
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "添加失败");
    }

    @Override
    public ResponseUtil edit(PushPointJnVO data) {
        boolean b = pushPointService.edit(data);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "修改失败");
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = pushPointService.delete(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "删除失败");
    }

    @Override
    public List<PointVO> listPoint(Long missionId, Long pushTaskId, Long pointId) {
        return DzBeanUtils.listCopy(pointService.listPointJn(missionId, pushTaskId, pointId), PointVO.class);
    }

}
