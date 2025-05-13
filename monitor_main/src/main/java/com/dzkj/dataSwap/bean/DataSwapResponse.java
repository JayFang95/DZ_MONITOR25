package com.dzkj.dataSwap.bean;

import lombok.Data;

import java.util.LinkedHashMap;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/7 15:39
 * @description 数据交换系统响应对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class DataSwapResponse {

    private ResHead head;

    private LinkedHashMap<String, String> body;

}
