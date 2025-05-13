package com.dzkj.dataSwap.vo;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/6
 * @description 推送任务VO
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PushTaskVO {

    private Long id;

    /**
     * 关联公司id
     */
    private Long companyId;

    /**
     * 关联任务id
     */
    private Long missionId;
    private String missionName;

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
     * 延迟上报监测数据时间(分钟)
     */
    private Integer delayUploadTime;

    /**
     * 访问方标识id
     */
    private String appId;

    private String appKey;
    /**
     * RSA公钥： base64字符串的格式给出
     */
    private String publicKey;
    /**
     * 铁路系统对接请求地址
     */
    private String stdUrl;
    /**
     * 测站约定KEY
     */
    private String key;

    /**
     * 测站接口访问KEY
     */
    private String secondkey;

    /**
     * 第三方类型
     */
    private int thirdPartType;

}
