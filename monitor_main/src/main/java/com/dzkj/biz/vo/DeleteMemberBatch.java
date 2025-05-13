package com.dzkj.biz.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/4/19 14:00
 * @description DeleteMemberBatch
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class DeleteMemberBatch {

    private String[] useridlist;

    public DeleteMemberBatch(String[] userIdList){
        this.useridlist = userIdList;
    }
}
