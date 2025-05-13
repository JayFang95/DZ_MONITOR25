package com.dzkj.dataSwap.biz.impl;

import com.dzkj.dataSwap.biz.IPushPointCdBiz;
import com.dzkj.service.data.IPushPointCdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21 下午3:04
 * @description 推送点业务-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Component
public class PushPointCdBiz implements IPushPointCdBiz {

    @Autowired
    private IPushPointCdService pushPointCdService;
}
