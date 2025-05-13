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
 * @date 2022/3/31
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
public class DownloadData {

    /**
     * 数据任务类型
     * 1： 现场巡视
     * 2：竖向位移/水平位移(HD)/ 水平位移(XY->S)/倾斜位移(%)
     * 3：水平位移(分层)
     * 4：支撑轴力
     */
    private String type;

    /**
     * 工程信息
     */
    private Long projectId;
    private String projectName;

    /**
     * 任务信息
     */
    private Long missionId;
    private String missionName;

    /**
     * 测量值单位
     */
    private String valueUnit;

    private String stConfigName;

}
