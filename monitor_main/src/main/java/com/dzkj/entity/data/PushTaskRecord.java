package com.dzkj.entity.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/9
 * @description 推送记录数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushTaskRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 关联推送任务id
     */
    private Long missionId;

    /**
     * 推送周期
     */
    private Integer recycleNum;

    private Date createTime;


}
