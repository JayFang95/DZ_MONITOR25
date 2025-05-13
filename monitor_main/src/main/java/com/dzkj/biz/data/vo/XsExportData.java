package com.dzkj.biz.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/4/12
 * @description 巡视导出对象
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class XsExportData {

    // 报表期数(不足三位的补0：如第1期为001)
    private String reportNo;
    // 报表编号：编号前缀+期数+(截止日期)，如BXHJC100(20211011)；（前端传递期数+(截止日期)）
    private String reportCode;
    // 巡视任务对象
    private JcInfoVO jcInfoVO;

}
