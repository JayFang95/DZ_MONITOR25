package com.dzkj.biz.param_set.impl;

import com.dzkj.biz.param_set.IPointDataStationBiz;
import com.dzkj.biz.param_set.vo.PointDataStationVO;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.param_set.PointDataStation;
import com.dzkj.service.param_set.IPointDataStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11 13:49
 * @description 测站配置点信息业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class PointDataStationBizImpl implements IPointDataStationBiz {

    @Autowired
    private IPointDataStationService pointDataStationService;

    @Override
    public List<PointDataStationVO> list(List<Long> pidList) {
        if(pidList == null || pidList.size() == 0){
            return new ArrayList<>();
        }
        return DzBeanUtils.listCopy(pointDataStationService.getListInPid(pidList), PointDataStationVO.class);
    }

    @Override
    public ResponseUtil saveOrUpdate(List<PointDataStationVO> list) {
        boolean saveOrUpdateBatch = pointDataStationService.saveOrUpdateBatch(DzBeanUtils.listCopy(list, PointDataStation.class));
        return ResponseUtil.success(saveOrUpdateBatch);
    }
}
