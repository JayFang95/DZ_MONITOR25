package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class TableDataCondition {
    // 项目id(全部)
    private Long projectId;
    // 监测任务id(所有任务传递)
    private Long missionId;
    // 测点编组id(非人工巡视任务传递)
    private Long groupId;
    private List<Long> groupIds;
    // 访问用户id
    private Long userId;
    // 开始日期
    private Date startDate;
    // 截止日期
    private Date endDate;
    // 是否是三维位移(三维位移任务传递)
    private Boolean isXyz;
    // 是否是水平位移分层(水平位移分层任务传递)
    private Boolean isFc;
}
