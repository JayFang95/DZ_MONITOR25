package com.dzkj.dataSwap.bean.ctce_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/8 下午1:53
 * @description DataStr
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class DataStr {

    @JsonProperty("LIST")
    private List<DataList> LIST;

}
