package com.dzkj.biz.dashoborad.vo;

import com.dzkj.biz.project.vo.ProjectVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/5/14
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScreenData {

    private List<ProjectVO> projectList;
    /**
     * 项目完成度
    **/
    private List<Integer> projectProcess;
    /**
     * 项目报警测点统计
     **/
    private List<Integer> proAlarmPtnNum;
    /**
     * 项目总数
     **/
    private int projectNum;
    /**
     * 设备总数
     **/
    private int equipNum;
    /**
     * 监测数据总条数
     **/
    private int monitorDataNum;
    /**
     * 报警工程占比
     **/
    private double alarmProRate;
    /**
     * 监测类型数据信息
     **/
    private List<ProTypeData> proTypeData;
    /**
     * 项目省份统计
     **/
    private List<String> province;
    /**
     * 项目省份统计
     **/
    private List<Integer> provinceNum;

}
