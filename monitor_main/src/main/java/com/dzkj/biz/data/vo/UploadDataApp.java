package com.dzkj.biz.data.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/8/8 11:16
 * @description 数据上传对象(app)
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class UploadDataApp {

    /**
     * 数据任务类型
     * 1： 现场巡视
     * 2：竖向位移/水平位移(HD)/ 水平位移(XY->S)/倾斜位移(%)
     * 3：水平位移(分层)
     * 4：支撑轴力
     */
    private String type;

    /**
     * 任务信息
     */
    private Long missionId;
    private Long userId;

    private List<DataApp> dataList;
}
