package com.dzkj.entity.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author jing.fang
 * @since 2025-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PushTaskCd implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属单位id
     */
    private Long companyId;

    /**
     * 关联项目id-成都局
     */
    private Long projectIdCd;

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
