package com.dzkj.service.param_set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.param_set.PointDataStation;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/9/11
 * @description 测站配置点服务接口
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPointDataStationService extends IService<PointDataStation> {

    /**
     * 查询
     * @param pidList pidList
     * @return List<PointDataStation>
     */
    List<PointDataStation> getListInPid(List<Long> pidList);
}
