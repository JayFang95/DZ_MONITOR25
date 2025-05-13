package com.dzkj.biz.data.vo;

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
 * @date 2022/4/12
 * @description 任务信息导出
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MissionDataExport {

    //表格标题
    private String title;
    // 三维位移导出
    private Boolean isXyzh;
    // 水平位移分层导出
    private Boolean isSpFc;
    // 三维位移导出数据列表
    private List<PointDataXyzhVO> pointDataXyzhList;
    // 非三维位移导出数据列表
    private List<PointDataZVO> pointDataZList;

    // 图表标题
    private String chartTitle;
    // 图表dataURL
    private String imgDataUrl;

}
