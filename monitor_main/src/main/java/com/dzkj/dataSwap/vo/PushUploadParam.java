package com.dzkj.dataSwap.vo;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送点参数
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushUploadParam {

    /**
     * 推送任务id
     */
    private Long pushTaskId;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 关联任务id
     */
    private Long missionId;

}
