package com.dzkj.biz.data.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/12
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
public class OtherDataCondition {

    // 任务id
    private List<Long> missionIds;
    private Long missionId;
    // 可查看编组id
    private List<Long> groupIds;
    // 创建时间数值
    private Integer timeNum;
    // 创建时间(前端不用传递)
    private Date createTime;
    // 删除数据参数
    private List<Date> deleteTime;

}
