package com.dzkj.dataSwap.vo;

import lombok.Data;

import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/24
 * @description 推送任务VO-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushTaskCdVO {

    private Long id;

    /**
     * 所属单位id
     */
    private Long companyId;

    /**
     * 关联项目id-成都局
     */
    private Long projectIdCd;
    private String projectNameCd;

    /**
     * 关联任务id
     */
    private Long missionId;

    /**
     * 推送任务名称
     */
    private String name;

    /**
     * 任务编号(对应对接的项目编号)
     */
    private String code;

    /**
     * 推送状态：1-推送中；0-未推送
     */
    private Integer status;

    /**
     * 延迟上报时间(分钟)
     */
    private Integer delayUploadTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 访问方用户名
     */
    private String username;

    /**
     * 访问方密码
     */
    private String password;

    /**
     * 访问请求地址
     */
    private String stdUrl;


}
