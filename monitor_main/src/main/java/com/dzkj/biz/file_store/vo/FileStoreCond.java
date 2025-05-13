package com.dzkj.biz.file_store.vo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/8/1 9:05
 * @description 文件存储查询对象
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class FileStoreCond {

    private List<Long> projectIds;
    private List<Long> missionIds;
    private Long missionId;

    private Integer timeNum;

    private Date createTime;

}
