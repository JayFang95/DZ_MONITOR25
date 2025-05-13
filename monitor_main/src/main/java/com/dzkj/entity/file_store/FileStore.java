package com.dzkj.entity.file_store;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1
 * @description 文件存储对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class FileStore implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属工程id
     */
    private Long projectId;
    @TableField(exist = false)
    private String projectName;

    /**
     * 所属任务id
     */
    private Long missionId;
    @TableField(exist = false)
    private String missionName;

    /**
     * 文件包名
     */
    private String packageName;

    /**
     * 存储文件id字符
     */
    private String fileIds;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 创建人
     */
    private String creator;
    private Long creatorId;

    private Date createTime;


}
