package com.dzkj.biz.survey.vo;


import com.dzkj.biz.param_set.vo.PointDataStationVO;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:25
 * @description note
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
public class RobotSurveyControlVO {

    private Long id;

    /**
     * 单位id
     */
    private Long companyId;

    /**
     * 监测任务id
     */
    private Long missionId;

    /**
     * 系列号
     */
    private String serialNo;

    /**
     * 测站配置
     **/
    private String stationConfig;

    /**
     * 参数信息
     */
    private String params;

    /**
     * 联测组id
     */
    private Long groupId;

    /**
     * 创建人Id
     */
    private Long createId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 测站中包含的非控制点信息
    **/
    private List<Long> pidList;

    /**
     * 测站配置点信息
     */
    private List<PointDataStationVO> dataStationList;


}
