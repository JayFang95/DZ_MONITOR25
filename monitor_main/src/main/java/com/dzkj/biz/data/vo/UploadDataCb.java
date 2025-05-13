package com.dzkj.biz.data.vo;

import com.dzkj.biz.alarm_setting.vo.AlarmInfoVO;
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
 * @date 2022/4/2
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadDataCb {

    /**
     * 任务类型
     */
    private String type;
    private Long missionId;
    /**
     * 巡视信息
     */
    private JcInfoVO infoVO;
    /**
     * 上传excel表格原始数据
     */
    private List<TablePtData> tableList;
    /**
     * 上传excel表格三维位移计算数据
     */
    private List<PointDataXyzhVO> ptDataXyzh;
    /**
     * 上传excel表格水平xy计算数据
     */
    private List<PointDataXyzVO> ptDataXyz;
    /**
     * 上传excel表格支撑轴力计算数据
     */
    private List<PointDataZlVO> ptDataZl;
    /**
     * 上传excel表格中间计算数据
     */
    private List<PointDataZVO> ptDataZ;
    /**
     * 报警信息集合
     */
    private List<AlarmInfoVO> alarmInfoList;

    private Long userId;

}
