package com.dzkj.mapper.data_swap;

import com.dzkj.entity.data.PushTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/3/11
 * @description 推送任务mapper
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface PushTaskMapper extends BaseMapper<PushTask> {

    /**
     * 查询推送任务
     *
     * @description 查询推送任务
     * @author jing.fang
     * @date 2025/3/11 下午3:08
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.entity.data.PushTask>
     **/
    List<PushTask> queryList(@Param("companyId") Long companyId);
}
