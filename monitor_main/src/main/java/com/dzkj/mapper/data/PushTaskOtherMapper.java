package com.dzkj.mapper.data;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.entity.data.PushTaskOther;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/11/18
 * @description 数据推送其他mapper
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface PushTaskOtherMapper extends BaseMapper<PushTaskOther> {

    /**
     * 查询
     *
     * @description 查询
     * @author jing.fang
     * @date 2024/11/18 15:16
     * @param companyId companyId
     * @return: java.util.List<com.dzkj.entity.data.PushTaskOther>
     **/
    List<PushTaskOther> queryList(@Param("companyId") Long companyId);
}
