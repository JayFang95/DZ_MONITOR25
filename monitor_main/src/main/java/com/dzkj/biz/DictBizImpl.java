package com.dzkj.biz;

import com.dzkj.entity.Dict;
import com.dzkj.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/22
 * @description 字典业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class DictBizImpl implements IDictBiz {

    @Autowired
    private IDictService dictService;

    @Override
    public Map<String, List<Dict>> queryDict(List<String> typeList) {
        List<Dict> list = dictService.queryList(typeList);
        Map<String, List<Dict>> map = new HashMap<>();
        for (String type : typeList) {
            List<Dict> voList = new ArrayList<>();
            for (Dict vo : list) {
                if(vo.getCode().equals(type)){
                    voList.add(vo);
                }
            }
            map.put(type, voList);
        }
        return map;
    }
}
