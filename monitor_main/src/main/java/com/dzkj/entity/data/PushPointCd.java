package com.dzkj.entity.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/7/9
 * @description 成都推送点表
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushPointCd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属推送任务id
     */
    private Long pushTaskId;

    /**
     * 设备唯一性代码，在本项目中要保持设备代码的唯一性
     */
    private String code;

    /**
     * 关联测点id
     */
    private Long pointId;

    /**
     * 关联成都局测点id
     */
    private Long pointIdCd;

    /**
     * 关联成都局测点项目id
     */
    private Long projectIdCd;

    /**
     * 监测点报警处理方法
     */
    private Integer alarmHandler;

    private Date createTime;


}
