package com.dzkj.biz.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.system.vo.OperateLogCondition;
import com.dzkj.entity.system.OperateLog;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/27
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IOPerateLogBiz {

    /**
     * 分页查询
     *
     * @description
     * @author wangy
     * @date 2021/8/27 17:26
     * @param pageIndex 索引页
     * @param pageSize 每页显示条目
     * @param condition 查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.entity.system.OperateLog>
    **/
    IPage<OperateLog> getPage(int pageIndex, int pageSize, OperateLogCondition condition);

}
