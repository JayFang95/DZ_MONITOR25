package com.dzkj.biz.data;

import com.dzkj.biz.data.vo.MissionDataCondition;
import com.dzkj.biz.data.vo.MissionDataExport;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/3/30
 * @description note
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public interface IDataMissionBiz {

    /**
     * 任务数据分页查询
     *
     * @description 任务数据分页查询
     * @author jing.fang
     * @date 2022/5/17 15:37
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.dzkj.common.util.ResponseUtil
    **/
    ResponseUtil getPage(Integer pi, Integer ps, MissionDataCondition condition);

    /**
     * 任务数据导出
     *
     * @description 任务数据导出
     * @author jing.fang
     * @date 2022/5/24 14:44
     * @param missionId missionId
     * @param dataExport dataExport
     * @return void
    **/
    void exportList(Long missionId, MissionDataExport dataExport, HttpServletResponse response);
}
