package com.dzkj.controller.survey;


import com.dzkj.bean.SurveyOnceResult;
import com.dzkj.biz.survey.IRobotSurveyControlBiz;
import com.dzkj.biz.survey.vo.OnlineCfgResultVo;
import com.dzkj.biz.survey.vo.RobotSurveyCond;
import com.dzkj.biz.survey.vo.RobotSurveyControlVO;
import com.dzkj.common.annotation.SysOperateLog;
import com.dzkj.common.constant.LogConstant;
import com.dzkj.common.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16 9:28
 * @description 测量控制controller
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
@RestController
@RequestMapping("/mt")
public class RobotSurveyControlController {

    @Autowired
    private IRobotSurveyControlBiz surveyControlBiz;

    @RequestMapping(value = "common/surveyControl/list/{companyId}", method = RequestMethod.GET)
    @SysOperateLog(value = "查询仪器配置参数列表", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil getList(@PathVariable Long companyId){
        return ResponseUtil.success(surveyControlBiz.getList(companyId));
    }

    @RequestMapping(value = "common/surveyControl/find", method = RequestMethod.POST)
    @SysOperateLog(value = "查询控制器配置信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil findControlInfo(@RequestBody RobotSurveyCond surveyCond){
        return ResponseUtil.success(surveyControlBiz.findControlInfo(surveyCond));
    }

    @RequestMapping(value = "common/surveyControl/save", method = RequestMethod.POST)
    @SysOperateLog(value = "保存控制器配置信息", type = LogConstant.UPDATE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil saveControlInfo(@RequestBody RobotSurveyControlVO surveyControlVO){
        return surveyControlBiz.saveControlInfo(surveyControlVO);
    }

    @RequestMapping(value = "common/surveyControl/checkStation/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "测站验证", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil checkStation(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.checkStation(equipId));
    }

    @RequestMapping(value = "common/surveyControl/checkPoint/{equipId}", method = RequestMethod.POST)
    @SysOperateLog(value = "测点验证", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil checkPoint(@PathVariable Long equipId, @RequestBody OnlineCfgResultVo resultVo){
        return ResponseUtil.success(surveyControlBiz.checkPoint(equipId, resultVo));
    }

    @RequestMapping(value = "common/surveyControl/refresh/{equipId}/{deviceType}", method = RequestMethod.POST)
    @SysOperateLog(value = "刷新控制器仪器类型", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil refreshControlBox(@PathVariable Long equipId, @PathVariable Integer deviceType){
        return ResponseUtil.success(surveyControlBiz.refreshControlBox(equipId, deviceType));
    }

    @RequestMapping(value = "common/surveyControl/openDevice/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "开机", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil openDevice(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.openDevice(equipId));
    }

    @RequestMapping(value = "common/surveyControl/closeDevice/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "关机", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil closeDevice(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.closeDevice(equipId));
    }

    @RequestMapping(value = "common/surveyControl/openLaser/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "打开激光", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil openLaser(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.openLaser(equipId));
    }

    @RequestMapping(value = "common/surveyControl/closeLaser/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "关闭激光", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil closeLaser(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.closeLaser(equipId));
    }

    @RequestMapping(value = "common/surveyControl/changeFace/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "正倒棱镜", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil changeFace(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.changeFace(equipId));
    }

    @RequestMapping(value = "common/surveyControl/openComp/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "打开补偿", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil openComp(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.openComp(equipId));
    }

    @RequestMapping(value = "common/surveyControl/surveyTest/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "测量测试", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil surveyTest(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.surveyTest(equipId));
    }

    @RequestMapping(value = "common/surveyControl/calculateHv", method = RequestMethod.POST)
    @SysOperateLog(value = "测量结果Ha Va转换", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil calculateHv(@RequestBody OnlineCfgResultVo cfgResultVo){
        return ResponseUtil.success(surveyControlBiz.calculateHv(cfgResultVo));
    }

    @RequestMapping(value = "common/surveyControl/calculate", method = RequestMethod.POST)
    @SysOperateLog(value = "测量结果计算", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil calculate(@RequestBody OnlineCfgResultVo cfgResultVo){
        return ResponseUtil.success(surveyControlBiz.calculate(cfgResultVo));
    }

    @RequestMapping(value = "common/surveyControl/calculatePoint", method = RequestMethod.POST)
    @SysOperateLog(value = "测点验证计算", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil calculatePoint(@RequestBody OnlineCfgResultVo cfgResultVo){
        return ResponseUtil.success(surveyControlBiz.calculatePoint(cfgResultVo));
    }

    @RequestMapping(value = "common/surveyControl/start/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "开启控制器测量", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil startSurvey(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.startSurvey(equipId));
    }

    @RequestMapping(value = "common/surveyControl/info/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取测量过程信息", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil getSurveyInfo(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.getSurveyInfo(equipId));
    }

    @RequestMapping(value = "common/surveyControl/stop/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "停止控制器测量", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil stopSurvey(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.stopSurvey(equipId));
    }

    @RequestMapping(value = "common/surveyControl/startOnce/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "临时加测", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil startOnce(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.startOnce(equipId));
    }

    @RequestMapping(value = "common/surveyControl/getOnce/{equipId}", method = RequestMethod.GET)
    @SysOperateLog(value = "获取加测结果", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil getOnceResult(@PathVariable Long equipId){
        return ResponseUtil.success(surveyControlBiz.getOnceResult(equipId));
    }

    @RequestMapping(value = "common/surveyControl/deleteOnce", method = RequestMethod.POST)
    @SysOperateLog(value = "删除加测结果", type = LogConstant.DELETE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil deleteOnce(@RequestBody List<SurveyOnceResult> resultList){
        return ResponseUtil.success(surveyControlBiz.deleteOnce(resultList));
    }

    @RequestMapping(value = "common/surveyControl/survey/mete/{equipId}/{serialNo}", method = RequestMethod.GET)
    @SysOperateLog(value = "温度气压采集", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil meteSurvey(@PathVariable("serialNo") String serialNo, @PathVariable("equipId") Long equipId){
        return ResponseUtil.success(surveyControlBiz.meteSurvey(serialNo, equipId));
    }

    @RequestMapping(value = "common/surveyControl/survey/vw/{equipId}/{serialNo}", method = RequestMethod.GET)
    @SysOperateLog(value = "振弦采集", type = LogConstant.RETRIEVE, modelName = LogConstant.EQUIP_CONTROL)
    public ResponseUtil vibrationWireSurvey(@PathVariable("serialNo") String serialNo, @PathVariable("equipId") Long equipId){
        return ResponseUtil.success(surveyControlBiz.vibrationWireSurvey(serialNo, equipId));
    }

}
