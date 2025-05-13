package com.dzkj.dataSwap.controller;


import com.dzkj.dataSwap.biz.IPushPointCdBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21
 * @description 推送点controller-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("mt/common/pushPointCd")
public class PushPointCdController {

    @Autowired
    private IPushPointCdBiz pushPointCdBiz;

}
