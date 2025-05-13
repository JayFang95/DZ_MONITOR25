package com.dzkj.biz.data;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.util.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
public interface IDataOtherBiz {

    /**
     * 三维原始数据查询
     *
     * @description 三维原始数据查询
     * @author jing.fang
     * @date 2022/5/20 16:00
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return java.lang.Object
    **/
    IPage<RobotSurveyDataVO> getXyzPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 手动三维位移原始数据查询
     *
     * @description 手动三维位移原始数据查询
     * @author jing.fang
     * @date 2023/8/7 11:45
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PointDataXyzhVO>
     **/
    IPage<PointDataXyzhRealVO> xyzHandPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 水平XY原始数据查询
     *
     * @description 水平XY原始数据查询
     * @author jing.fang
     * @date 2022/5/20 16:00
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PointDataXyzVO>
    **/
    IPage<PointDataXyzVO> getXyPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 轴力原始数据查询
     *
     * @description 轴力原始数据查询
     * @author jing.fang
     * @date 2022/5/20 16:00
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PointDataZlVO>
    **/
    IPage<PointDataZlVO> getZlPage(Integer pi, Integer ps, OtherDataCondition condition);

    /**
     * 分页查询停测记录
     *
     * @description 分页查询停测记录
     * @author jing.fang
     * @date 2022/5/23 14:24
     * @param pi pi
     * @param ps ps
     * @param condition condition
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.dzkj.biz.data.vo.PtStopVO>
    **/
    IPage<PtStopVO>  getStopPage(Integer pi, Integer ps, PtDataStopCondition condition);

    /**
     * 删除监测数据记录
     *
     * @description 删除监测数据记录
     * @author jing.fang
     * @date 2022/5/23 15:12
     * @param condition condition
     * @return void
    **/
    boolean deleteData(OtherDataCondition condition);

    /**
     * 水平位移数据导出
     *
     * @description 水平位移数据导出
     * @author jing.fang
     * @date 2022/5/23 15:55
     * @param list list
     * @param response response
    **/
    void xyExport(List<PointDataXyzVO> list, HttpServletResponse response);

    /**
     * 轴力数据导出
     *
     * @description 轴力数据导出
     * @author jing.fang
     * @date 2022/5/23 15:55
     * @param list list
     * @return void
    **/
    void zlExport(List<PointDataZlVO> list, HttpServletResponse response);

    /**
     * 测点停测记录导出
     *
     * @description 测点停测记录导出
     * @author jing.fang
     * @date 2022/5/24 14:13
     * @param list list
     * @param response response
     * @return void
    **/
    void stopExport(List<PtStopVO> list, HttpServletResponse response);

    /**
     * 下载过程数据记录
     *
     * @description 下载过程数据记录
     * @author jing.fang
     * @date 2023/6/20 10:17
     * @param fileName fileName
     * @param response response
     * @return: void
     **/
    void downloadProcess(String fileName, HttpServletResponse response);

    /**
     * 过程文件下载验证
     *
     * @description 过程文件下载验证
     * @author jing.fang
     * @date 2025/3/5 下午8:53
     * @param filename filename
     * @return: com.dzkj.common.util.ResponseUtil
     **/
    ResponseUtil downloadProcessCheck(String filename);
}
