package com.dzkj.biz.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author wangy
 * @date 2021/8/27
 * @description 系统日志条件对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OperateLogCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    // 所属公司id
    private Long companyId;

    // 开始时间
    private Date startDate;

    // 开结束时间
    private Date endDate;

    // 日志内容
    private String content;

}
