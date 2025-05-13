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
public class PtDataStopCondition {

    // 任务id
    private Long missionId;
    // 编组id
    private Long groupId;
    private List<Long> groupIds;
    // 测点id
    private Long pointId;
    // 停测时间数值
    private Integer timeNum;
    // 停测时间(前端不用传递)
    private Date createTime;
    // 停测原因
    private String reason;

}
