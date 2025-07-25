package com.dzkj.biz.data.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzkj.biz.data.IDataPtBiz;
import com.dzkj.biz.data.common.BaseDataBiz;
import com.dzkj.biz.data.vo.*;
import com.dzkj.common.constant.CommonConstant;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.alarm_setting.AlarmItem;
import com.dzkj.entity.data.*;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.project.ProMission;
import com.dzkj.service.alarm_setting.IAlarmItemService;
import com.dzkj.service.data.*;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.project.IProMissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
@Component
public class DataPtBizImpl implements IDataPtBiz {

    @Autowired
    private IPointDataZService pointDataZService;
    @Autowired
    private IPointDataZRealService dataZRealService;
    @Autowired
    private IPointDataXyzhService pointDataXyzhService;
    @Autowired
    private IPointDataXyzhRealService dataXyzhRealService;
    @Autowired
    private IJcInfoService jcInfoService;
    @Autowired
    private IProMissionService missionService;
    @Autowired
    private IPointService pointService;
    @Autowired
    private IAlarmItemService alarmItemService;

    @Autowired
    private IPointDataXyzhCorrectService dataXyzhCorrectService;

    @Override
    public IPage<PointDataZVO> zPage(Integer pi, Integer ps, PtDataCondition cond) {
        LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
        if (cond.getDate() != null && cond.getDate().size() == 2) {
            wrapper.ge(PointDataZ::getGetTime, cond.getDate().get(0))
                    .le(PointDataZ::getGetTime, cond.getDate().get(1));
        }
        wrapper.eq(PointDataZ::getPid, cond.getPid()).eq(PointDataZ::getStop, false)
                .eq(cond.getOverLimit() != null, PointDataZ::getOverLimit, cond.getOverLimit())
                .orderByDesc(PointDataZ::getRecycleNum)
                .orderByDesc(PointDataZ::getGetTime);
        IPage<PointDataZVO> page;
        if (ps == CommonConstant.SEARCH_ALL_NO) {
            page = new Page<>(pi, ps);
            List<PointDataZVO> list = DzBeanUtils.listCopy(pointDataZService.list(wrapper), PointDataZVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        } else {
            page = DzBeanUtils.pageCopy(pointDataZService.page(new Page<>(pi, ps), wrapper), PointDataZVO.class);
        }
        return page;
    }

    @Override
    public IPage<PointDataXyzhVO> xyzhPage(Integer pi, Integer ps, PtDataCondition cond) {
        // region 2024/11/21 独立显示页面查询
        if (cond.getDbSource() != null && 2 == cond.getDbSource()) {
            return getCorrectPage(pi, ps, cond);
        }
        // endregion 2024/11/21 独立显示页面查询
        LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
        if (cond.getDate() != null && cond.getDate().size() == 2) {
            wrapper.ge(PointDataXyzh::getGetTime, cond.getDate().get(0))
                    .le(PointDataXyzh::getGetTime, cond.getDate().get(1));
        }
        wrapper.eq(PointDataXyzh::getPid, cond.getPid()).eq(PointDataXyzh::getStop, false)
                .eq(cond.getOverLimit() != null, PointDataXyzh::getOverLimit, cond.getOverLimit())
                .orderByDesc(PointDataXyzh::getRecycleNum)
                .orderByDesc(PointDataXyzh::getGetTime);
        IPage<PointDataXyzhVO> page;
        if (ps == CommonConstant.SEARCH_ALL_NO) {
            page = new Page<>(pi, ps);
            List<PointDataXyzhVO> list = DzBeanUtils.listCopy(pointDataXyzhService.list(wrapper), PointDataXyzhVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        } else {
            page = DzBeanUtils.pageCopy(pointDataXyzhService.page(new Page<>(pi, ps), wrapper), PointDataXyzhVO.class);
        }
        return page;
    }

    private IPage<PointDataXyzhVO> getCorrectPage(Integer pi, Integer ps, PtDataCondition cond) {
        LambdaQueryWrapper<PointDataXyzhCorrect> wrapper = new LambdaQueryWrapper<>();
        if (cond.getDate() != null && cond.getDate().size() == 2) {
            wrapper.ge(PointDataXyzhCorrect::getGetTime, cond.getDate().get(0))
                    .le(PointDataXyzhCorrect::getGetTime, cond.getDate().get(1));
        }
        wrapper.eq(PointDataXyzhCorrect::getPid, cond.getPid()).eq(PointDataXyzhCorrect::getStop, false)
                .eq(cond.getOverLimit() != null, PointDataXyzhCorrect::getOverLimit, cond.getOverLimit())
                .orderByDesc(PointDataXyzhCorrect::getRecycleNum)
                .orderByDesc(PointDataXyzhCorrect::getGetTime);
        IPage<PointDataXyzhVO> page;
        if (ps == CommonConstant.SEARCH_ALL_NO) {
            page = new Page<>(pi, ps);
            List<PointDataXyzhVO> list = DzBeanUtils.listCopy(dataXyzhCorrectService.list(wrapper), PointDataXyzhVO.class);
            page.setRecords(list);
            page.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        } else {
            page = DzBeanUtils.pageCopy(dataXyzhCorrectService.page(new Page<>(pi, ps), wrapper), PointDataXyzhVO.class);
        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseUtil dataInit(PtDataCondition condition) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        if (condition.getIsXyz()) {
            LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<PointDataXyzhReal> wrapper1 = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataXyzh::getPid, condition.getPid());
            wrapper1.eq(PointDataXyzhReal::getPid, condition.getPid());
            pointDataXyzhService.remove(wrapper);
            dataXyzhRealService.remove(wrapper1);
        } else {
            LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<PointDataZReal> wrapper1 = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataZ::getPid, condition.getPid());
            wrapper1.eq(PointDataZReal::getPid, condition.getPid());
            pointDataZService.remove(wrapper);
            dataZRealService.remove(wrapper1);
        }
        return ResponseUtil.success();
    }

    @Override
    public IPage<JcInfoVO> xsPage(Integer pi, Integer ps, PtDataCondition cond) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        if (cond.getDate() != null && cond.getDate().size() == 2) {
            wrapper.ge(JcInfo::getJcDate, cond.getDate().get(0))
                    .le(JcInfo::getJcDate, cond.getDate().get(1));
        }
        wrapper.eq(JcInfo::getMissionId, cond.getMissionId())
                .orderByDesc(JcInfo::getJcDate);
        IPage<JcInfoVO> copy;
        if (ps == CommonConstant.SEARCH_ALL_NO) {
            copy = new Page<>(pi, ps);
            List<JcInfoVO> list = DzBeanUtils.listCopy(jcInfoService.list(wrapper), JcInfoVO.class);
            copy.setRecords(list);
            copy.setTotal(Math.min(list.size(), CommonConstant.SEARCH_ALL_NO));
        } else {
            copy = DzBeanUtils.pageCopy(jcInfoService.page(new Page<>(pi, ps), wrapper), JcInfoVO.class);
        }
        if (copy.getTotal() > 0) {
            List<JcInfoVO> records = copy.getRecords();
            // 0 5 8 13
            for (JcInfoVO info : records) {
                String[] split = info.getInfo().split("\\|\\|");
                info.setTqStatus(split[0].split("<\\$>")[0])
                        .setTzStatus(split[5].split("<\\$>")[0])
                        .setGdStatus(split[8].split("<\\$>")[0])
                        .setPtStatus(split[13].split("<\\$>")[0])
                ;
            }
            copy.setRecords(records);
        }
        return copy;
    }

    @Override
    public ResponseUtil dataXsInit(Long missionId) {
        LambdaQueryWrapper<JcInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JcInfo::getMissionId, missionId);
        boolean b = jcInfoService.remove(wrapper);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "数据初始化失败");
    }

    @Override
    public ResponseUtil deleteXs(Long id) {
        boolean b = jcInfoService.removeById(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(500, "数据删除失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseUtil dataCalculate(PtDataCalculate calculate) {
        List<PointDataCalculate> updateList = calculate.getUpdateList();
        List<Long> deleteIds = calculate.getDeleteIds();
        Boolean isXyz = calculate.getIsXyz();
        if (isXyz == null) {
            return ResponseUtil.success();
        }
        if (updateList != null && !updateList.isEmpty()) {
            if (isXyz) {
                pointDataXyzhService.updateBatchById(DzBeanUtils.listCopy(updateList, PointDataXyzh.class));
            } else {
                pointDataZService.updateBatchById(DzBeanUtils.listCopy(updateList, PointDataZ.class));
            }
        }
        if (deleteIds != null && !deleteIds.isEmpty()) {
            if (isXyz) {
                pointDataXyzhService.removeByIds(deleteIds);
            } else {
                pointDataZService.removeByIds(deleteIds);
            }
        }
        // 重新计算数据
        reCalculateData(calculate);
        return ResponseUtil.success();
    }

    @Override
    public List<PointDataZVO> dataListChart(PtDataChartCondition condition) {
        List<PointDataZ> list = pointDataZService.getDateLimit(condition);
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        List<Date> dateList = list.stream().map(PointDataZ::getGetTime).collect(Collectors.toList());
        LambdaQueryWrapper<PointDataZ> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(PointDataZ::getPid, condition.getSelectIds())
                .in(PointDataZ::getGetTime, dateList).orderByDesc(PointDataZ::getGetTime);
        return DzBeanUtils.listCopy(pointDataZService.list(wrapper1), PointDataZVO.class);
    }

    @Override
    public List<PointDataXyzhVO> dataXyzhListChart(PtDataChartCondition condition) {
        List<PointDataXyzh> list = pointDataXyzhService.getDateLimit(condition);
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        List<Date> dateList = list.stream().map(PointDataXyzh::getGetTime).collect(Collectors.toList());
        LambdaQueryWrapper<PointDataXyzh> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(PointDataXyzh::getPid, condition.getSelectIds())
                .in(PointDataXyzh::getGetTime, dateList).orderByDesc(PointDataXyzh::getGetTime);
        return DzBeanUtils.listCopy(pointDataXyzhService.list(wrapper1), PointDataXyzhVO.class);
    }

    @Override
    public ResponseUtil updateData(PtDataUpdateAndCalculate data) {
        boolean update;
        if(data.getIsXyz()) {
            update = pointDataXyzhService.updateById(DzBeanUtils.propertiesCopy(data, PointDataXyzh.class));
        } else {
            update = pointDataZService.updateById(DzBeanUtils.propertiesCopy(data, PointDataZ.class));
        }
        if(update){
            reCalculateData(DzBeanUtils.propertiesCopy(data, PtDataCalculate.class));
        }
        return update ? ResponseUtil.success() : ResponseUtil.failure();
    }

    /**
     * 进行重新计算(数据导入逻辑)
     * 重新计算时，需要判断各行的超限情况，若出现超限时，设置超限标识并记录超限信息，但不生成报警记录；
     */
    private void reCalculateData(PtDataCalculate calculate) {
        ProMission mission = missionService.findById(calculate.getMissionId());
        if (calculate.getIsXyz()) {
            LambdaQueryWrapper<PointDataXyzh> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataXyzh::getPid, calculate.getPid()).eq(PointDataXyzh::getStop, false)
                    .orderByAsc(PointDataXyzh::getGetTime);
            List<PointDataXyzh> list = pointDataXyzhService.list(wrapper);
            if(list.isEmpty()) {
                return;
            }
            List<Point> points = pointService.queryByMissionId(mission==null ? 0 : mission.getId());
            for (int i = 0; i < list.size(); i++) {
                PointDataXyzh data = list.get(i);
                PointDataXyzhVO copy = DzBeanUtils.propertiesCopy(data, PointDataXyzhVO.class);
                // 数据赋值计算
                if (i == 0) {
                    BaseDataBiz.setFirstData(copy, points, true);
                } else {
                    PointDataXyzh lastData = list.get(i - 1);
                    BaseDataBiz.setNewData(copy, lastData, mission, points, true);
                }
                copy.setRecycleNum(data.getRecycleNum());
                list.set(i, DzBeanUtils.propertiesCopy(copy, PointDataXyzh.class));
            }
            // 验证测点是否超限，记录超限信息
            List<Long> pidList = list.stream().map(PointDataXyzh::getPid).collect(Collectors.toList());
            List<AlarmItem> alarmItems = alarmItemService.getByPidList(pidList);
            List<AlarmItem> items = alarmItems.stream().filter(item -> item.getMonitorType().equals(mission.getTypeName()))
                    .collect(Collectors.toList());
            for (PointDataXyzh data : list) {
                List<AlarmItem> collect = items.stream().filter(item -> item.getPtId().equals(data.getPid())).collect(Collectors.toList());
                List<String> nameList = collect.stream().filter(item -> item.getAlarmType() == 2).map(AlarmItem::getResultItemType)
                        .distinct().collect(Collectors.toList());
                AtomicInteger atomicInteger = new AtomicInteger(0);
                StringBuilder sb = new StringBuilder();
                List<String> infoList = new ArrayList<>();
                PointDataXyzhVO copy = DzBeanUtils.propertiesCopy(data, PointDataXyzhVO.class);
                BaseDataBiz.checkValueOverXyz(copy, collect, "累计变化量", atomicInteger, sb, infoList);
                BaseDataBiz.checkValueOverXyz(copy, collect, "单次变化量", atomicInteger, sb, infoList);
                BaseDataBiz.checkValueOverXyz(copy, collect, "日变化速率", atomicInteger, sb, infoList);
                BaseDataBiz.checkValueOverXyz(copy, collect, "测量值", atomicInteger, sb, infoList);
                BaseDataBiz.checkValueOverXyz2(copy, collect, nameList, atomicInteger, sb, infoList);
                if(atomicInteger.get() > 0){
                    data.setOverLimit(true);
                    data.setOverLimitInfo(sb.substring(0, sb.toString().length()-1));
                }else {
                    data.setOverLimit(false);
                }
            }
            //更新入库
            pointDataXyzhService.updateBatchById(list);
        } else {
            LambdaQueryWrapper<PointDataZ> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PointDataZ::getPid, calculate.getPid()).eq(PointDataZ::getStop, false)
                    .orderByAsc(PointDataZ::getGetTime);
            List<PointDataZ> list = pointDataZService.list(wrapper);
            if(list.isEmpty()) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                // 数据赋值计算
                PointDataZ data = list.get(i);
                PointDataZVO copy = DzBeanUtils.propertiesCopy(data, PointDataZVO.class);
                if (i == 0) {
                    BaseDataBiz.setFirstData(copy, mission.getType());
                } else {
                    PointDataZ lastData = list.get(i - 1);
                    BaseDataBiz.setNewData(copy, lastData, mission, mission.getType());
                }
                copy.setRecycleNum(data.getRecycleNum());
                list.set(i, DzBeanUtils.propertiesCopy(copy, PointDataZ.class));
            }
            // 验证测点是否超限，记录超限信息
            List<Long> pidList = list.stream().map(PointDataZ::getPid).collect(Collectors.toList());
            List<AlarmItem> alarmItems = alarmItemService.getByPidList(pidList);
            List<AlarmItem> items = alarmItems.stream().filter(item -> item.getMonitorType().equals(mission.getTypeName()))
                    .collect(Collectors.toList());
            for (PointDataZ data : list) {
                List<AlarmItem> collect = items.stream().filter(item -> item.getPtId().equals(data.getPid())).collect(Collectors.toList());
                List<String> nameList = collect.stream().filter(item -> item.getAlarmType() == 2).map(AlarmItem::getResultItemType)
                        .distinct().collect(Collectors.toList());
                AtomicInteger atomicInteger = new AtomicInteger(0);
                StringBuilder sb = new StringBuilder();
                PointDataZVO copy = DzBeanUtils.propertiesCopy(data, PointDataZVO.class);
                BaseDataBiz.checkValueOver(copy, collect, "累计变化量", atomicInteger, sb);
                BaseDataBiz.checkValueOver(copy, collect, "单次变化量", atomicInteger, sb);
                BaseDataBiz.checkValueOver(copy, collect, "日变化速率", atomicInteger, sb);
                BaseDataBiz.checkValueOver(copy, collect, "测量值", atomicInteger, sb);
                BaseDataBiz.checkValueOver2(copy, collect, nameList, atomicInteger, sb);
                if(atomicInteger.get() > 0){
                    data.setOverLimit(true);
                    data.setOverLimitInfo(sb.substring(0, sb.toString().length()-1));
                }else {
                    data.setOverLimit(false);
                }
            }
            //更新入库
            pointDataZService.updateBatchById(list);
        }
    }

}