package com.dzkj.biz.dashoborad.vo;

import com.dzkj.biz.param_set.vo.PointVO;
import com.dzkj.biz.param_set.vo.PtGroupVO;
import com.dzkj.biz.project.vo.ProMissionVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/9/6 17:08
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InfoData {

    private List<ProMissionVO> missionList;
    private List<PtGroupVO> groupList;
    private List<PointVO> pointList;

}
