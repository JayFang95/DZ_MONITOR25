package com.dzkj.biz;

import com.dzkj.entity.Dict;

import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典业务接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDictBiz {
    /**
     * 查询指定得字典值
     * @author liao
     * @date 2021-09-27 15:33
     * @param typeList typeList
     * @return java.util.Map<java.lang.String,java.util.List<DictPO>>
     **/
    Map<String, List<Dict>> queryDict(List<String> typeList);
}
