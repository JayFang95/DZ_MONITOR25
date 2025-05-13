package com.dzkj.robot.survey;

import com.dzkj.bean.RtCode;
import com.dzkj.bean.TmcAngSwitch;
import com.dzkj.common.CommonUtil;
import com.dzkj.common.util.ThreadPoolUtil;
import com.dzkj.enums.*;
import com.dzkj.robot.bean.OperateCompleteResult;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.geoCom.NetCommands;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/15 14:47
 * @description 莱卡设备业务实现
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@Slf4j
public class DeviceBizLeica implements IDeviceBiz {

    private ControlBoxBo currentBoxBo;
    /**
     * 重复执行命令次数(每隔60秒发一次)，总时长3分钟
     **/
    private final int repeatNum = 3;
    /**
     * 重复执行命令次数(每隔10秒发一次)，总时长2分钟
     **/
    private String commandName = "";
    /**
     * 子过程名称
     **/
    private String subStepName = "";
    /**
     * 日志信息
     **/
    private String logInfo = "";

    private Consumer<OperateCompleteResult> surveyOperateCompleted;

    public DeviceBizLeica(Consumer<OperateCompleteResult> surveyCompleted){
        this.surveyOperateCompleted = surveyCompleted;
    }

    @Override
    public void cancelOperate() {
        if (currentBoxBo != null)
        {
            currentBoxBo.cancelCommand();
        }
    }

    @Override
    public void offLineOperate() {
        if (currentBoxBo != null)
        {
            currentBoxBo.offLineOperate();
        }
    }

    @Override
    public void refreshCurrentBoxBo(ControlBoxBo controlBoxBo) {
        currentBoxBo = controlBoxBo;
    }

    @Override
    public void openDevice(boolean directInvoke) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                int rNum = 0;
                commandName = "OpenDevice";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode = CommonUtil.SYS_GRC_DEFAULT;
                while (rtCode != CommonUtil.SYS_GRC_OK && rNum < repeatNum) {
                    if (rNum != 0 ){
                        log.info("命令:[{}]第{}次执行:错误原因{}", commandName, rNum, rtCode);
                    }
                    //重复执行命令过程中，控制器掉线,取消线程
                    if (currentBoxBo == null) {
                        rtCode = CommonUtil.SYS_GRC_OFFLINE;
                        break;
                    }

                    currentBoxBo.setProcessCommandTimeout(true, 90L);
                    currentBoxBo.setCommandName(commandName);
                    try {
                        rtCode = NetCommands.comSwitchOnTPS(currentBoxBo,
                                ComTpsStartUpMode.COM_TPS_STARTUP_REMOTE);
                    }catch (Exception e){
                        log.info("开机指令失败或解析错误: {}", e.getMessage());
                    }finally {
                        rNum++;
                    }
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK || rtCode == CommonUtil.SYS_GRC_CANCEL) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, directInvoke));
            }
        });
    }

    @Override
    public void closeDevice() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "CloseLaser";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.comSwitchOffTPS(currentBoxBo, ComTpsStopMode.COM_TPS_STOP_SHUT_DOWN);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void openLaser() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "OpenLaser";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.edmLaserPointer(currentBoxBo, OnOffType.ON);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void closeLaser() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "CloseLaser";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.edmLaserPointer(currentBoxBo, OnOffType.OFF);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void changeFace() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "ChangeFace";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.autChangeFace(currentBoxBo, AutPosMode.AUT_PRECISE,
                            AutAtrMode.AUT_TARGET,
                            false);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void openComp() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "OpenComp";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    TmcAngSwitch swAngSwitch = new TmcAngSwitch();
                    swAngSwitch.setEInclineCorr(OnOffType.ON);
                    swAngSwitch.setEStandAxisCorr(OnOffType.ON);
                    swAngSwitch.setECollimationCorr(OnOffType.ON);
                    swAngSwitch.setETiltAxisCorr(OnOffType.ON);
                    //可以同时打开双轴补偿和角度补偿
                    rtCode = NetCommands.tmcSetAngSwitch(currentBoxBo, swAngSwitch);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }

                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void surveyTest() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "SurveyTest";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results = new ArrayList<>();

                //region 1测量
                subStepName = "[测量]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.bapMeasDistanceAngle(currentBoxBo, BapMeasurePrg.BAP_DEF_DIST, results);
                }
                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode == CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null) {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }
                    
                    log.info(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行 获取前三个结果
                List<String> resultAll = new ArrayList<>(new ArrayList<>(results.subList(0, 3)));
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 2获取测站坐标
                results = new ArrayList<>();
                subStepName = "[获取测站坐标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetStation(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 3获取测点坐标
                results = new ArrayList<>();
                subStepName = "[获取测点坐标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetCoordinate(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(new ArrayList<>(results.subList(0, 3)));
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 4获取仪器正倒镜状态
                results = new ArrayList<>();
                subStepName = "[获取仪器正倒镜状态]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetFace(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, false));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 5获取棱镜参数
                results = new ArrayList<>();
                subStepName = "[获取棱镜参数]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetPrismCorr(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 6 获取棱镜高
                results = new ArrayList<>();
                subStepName = "[获取棱镜高]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetHeight(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 7 获取角度补偿开关状态
                results = new ArrayList<>();
                subStepName = "[获取角度补偿开关状态]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                   currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetAngSwitch(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 8获取仪器名称
                results = new ArrayList<>();
                subStepName = "[获取仪器名称]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvGetInstrumentName(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 9获取仪器序号
                results = new ArrayList<>();
                subStepName = "[获取仪器序号]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvGetInstrumentNo(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, resultAll, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void getDeviceInfo() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "GetDeviceInfo";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results = new ArrayList<>();
                //region 1获取仪器名称

                subStepName = "[获取仪器名称]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvGetInstrumentName(currentBoxBo, results);
                }
                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行
                List<String> resultAll = new ArrayList<>(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 2获取仪器序号
                results = new ArrayList<>();
                subStepName = "[获取仪器序号]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvGetInstrumentNo(currentBoxBo, results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 3获取仪器电源信息
                results = new ArrayList<>();
                subStepName = "[获取仪器电源信息]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvCheckPower(currentBoxBo, results);
                }
                
                //异常退出
                if (!(rtCode == CommonUtil.SYS_GRC_OK || Objects.equals(rtCode.getName(), "GRC_LOW_POWER") ||
                        Objects.equals(rtCode.getName(), "GRC_BATT_EMPTY"))) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                rtCode = CommonUtil.SYS_GRC_OK; //设置返回码为SYS_GRC_OK
                //endregion

                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, resultAll, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void getDevicePowerInfo(boolean directInvoke) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "GetDevicePowerInfo";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                List<String> results = new ArrayList<>();
                RtCode rtCode;
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.csvCheckPower(currentBoxBo, results);
                }
                if (rtCode == null) {
                    log.info("命令:[{}]异常: {}", commandName, "rtCode为空");
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, commandName, null, directInvoke));
                    return;
                }
                //异常退出
                if (!(rtCode == CommonUtil.SYS_GRC_OK || Objects.equals(rtCode.getName(), "GRC_LOW_POWER") ||
                        Objects.equals(rtCode.getName(), "GRC_BATT_EMPTY"))) {
                    log.error(logInfo);
                    log.info("命令:[{}]执行错误: {}", commandName, rtCode.getNote());
                    if (!directInvoke){
                        rtCode = CommonUtil.SYS_GRC_OK;
                    }
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }
                // 正常执行
                rtCode = CommonUtil.SYS_GRC_OK; //设置返回码为SYS_GRC_OK
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, results, directInvoke));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]捕捉异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, directInvoke));
            }
        });
    }

    @Override
    public void getFullMeas() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "GetFullMeas";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results = new ArrayList<>();
                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetFullMeas(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, results, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void checkStation() {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "CheckStation";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results = new ArrayList<>();

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetStation(currentBoxBo, results);
                }
                if (rtCode == null) {return;}
                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                if (rtCode == CommonUtil.SYS_GRC_OK) {
                    log.info(logInfo);
                } else {
                    log.error(logInfo);
                }
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, results, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, true));
            }
        });
    }

    @Override
    public void doMeasureAndGetFullResult(boolean isFace1, boolean checkFace, double searchHa, double searchVa) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "DoMeasureAndGetFullResult";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results = new ArrayList<>();

                //region 1获取仪器正倒镜状态
                //需要检查正倒镜状态
                if (checkFace) {
                    subStepName = "[获取仪器正倒镜状态]";
                    logInfo = "***子过程:" + subStepName + "开始>>>";
                    log.info(logInfo);

                    //控制器掉线/在线
                    if (currentBoxBo == null) {
                        rtCode = CommonUtil.SYS_GRC_OFFLINE;
                    } else {
                        currentBoxBo.setProcessCommandTimeout(true, null);
                        currentBoxBo.setCommandName(commandName);
                        rtCode = NetCommands.tmcGetFace(currentBoxBo, results);
                    }
                    if (rtCode == null) {return;}
                    //异常退出
                    if (rtCode != CommonUtil.SYS_GRC_OK) {
                        log.error(logInfo);
                        //激发任务完成事件
                        surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null));
                        return;
                    }

                    //正常执行
                    logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                    log.info(logInfo);

                    boolean isF1 = Objects.equals(results.get(0), "0");
                    //如果正倒镜不一致，转向
                    if (isFace1 != isF1) {
                        subStepName = "[正倒镜转换]";
                        logInfo = "***子过程:" + subStepName + "开始>>>";
                        log.info(logInfo);

                        //控制器掉线/在线
                        if (currentBoxBo == null) {
                            rtCode = CommonUtil.SYS_GRC_OFFLINE;
                        } else {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            rtCode = NetCommands.autChangeFace(currentBoxBo, AutPosMode.AUT_PRECISE,
                                    AutAtrMode.AUT_TARGET,
                                    false);
                        }

                        if (rtCode == null) {return;}
                        //异常退出
                        if (rtCode != CommonUtil.SYS_GRC_OK) {
                            log.error(logInfo);
                            //激发任务完成事件
                            surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                            return;
                        }

                        //正常执行
                        logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                        log.info(logInfo);
                    }
                }
                //endregion

                //region 2精确照准目标
                subStepName = "[精确照准目标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, 60L); //设置超时60s
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.autFineAdjust(currentBoxBo, searchHa, searchVa, false);
                }
                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode == CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null)
                        {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }

                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 3清除测量结果
                subStepName = "[清除测量结果]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcDoMeasure(currentBoxBo, TmcMeasurePrg.TMC_CLEAR,
                            TmcInclinePrg.TMC_AUTO_INC);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 4开始测量
                subStepName = "[测量]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcDoMeasure(currentBoxBo, TmcMeasurePrg.TMC_DEF_DIST,
                            TmcInclinePrg.TMC_AUTO_INC);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode == CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null) {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }

                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 5获取完整测量信息
                results = new ArrayList<>();
                subStepName = "[获取完整测量信息]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetFullMeas(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK)
                {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                List<String> resultAll = new ArrayList<>(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 6获取测点坐标
                results = new ArrayList<>();
                subStepName = "[获取测点坐标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetCoordinate(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, true));
                    return;
                }

                // 正常执行
                resultAll.addAll(new ArrayList<>(results.subList(0, 3)));
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, resultAll, true));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, false));
            }
        });
    }

    @Override
    public void checkMeasureAndGetFullResult(double searchHa, double searchVa, double ha, double va, double ht, int pauseTime, boolean directInvoke) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                if (pauseTime > 0){
                    logInfo = "命令:[" + commandName + "]即将暂停"+pauseTime+"s>>>";
                    log.info(logInfo);
                }
                //暂停时间
                Thread.sleep(pauseTime * 1000L);

                commandName = "CheckMeasureAndGetFullResult";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);

                RtCode rtCode;
                List<String> results;

                //region 1设置棱镜高
                subStepName = "[设置棱镜高]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcSetHeight(currentBoxBo, ht);
                }
                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 2转动仪器到指定方向
                subStepName = "[转动仪器到指定方向]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, 30L); //设置超时30s
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.autMakePositioning(currentBoxBo, ha, va, AutPosMode.AUT_PRECISE,
                            AutAtrMode.AUT_TARGET, false);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode ==CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null) {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }

                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 3精确照准目标
                subStepName = "[精确照准目标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, 60L); //设置超时60s
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.autFineAdjust(currentBoxBo, searchHa, searchVa, false);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode == CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null) {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }

                    log.info(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 4清除测量结果
                subStepName = "[清除测量结果]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcDoMeasure(currentBoxBo, TmcMeasurePrg.TMC_DEF_DIST,
                            TmcInclinePrg.TMC_AUTO_INC);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //任务完成激发事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 5开始测量
                subStepName = "[测量]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcDoMeasure(currentBoxBo, TmcMeasurePrg.TMC_DEF_DIST,
                            TmcInclinePrg.TMC_AUTO_INC);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    //测量超时，取消搜索
                    if (rtCode == CommonUtil.SYS_GRC_TIMEOUT) {
                        if (currentBoxBo != null) {
                            currentBoxBo.setProcessCommandTimeout(true, null);
                            currentBoxBo.setCommandName(commandName);
                            NetCommands.autCancel(currentBoxBo, "");
                        }
                    }

                    log.error(logInfo);
                    //任务完成激发事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 6获取完整测量信息
                results = new ArrayList<>();
                subStepName = "[获取完整测量信息]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetFullMeas(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                // 正常执行
                List<String> resultAll = new ArrayList<>(results);
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 7获取测点坐标
                results = new ArrayList<>();
                subStepName = "[获取测点坐标]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);

                //控制器掉线/在线
                if (currentBoxBo == null) {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                } else {
                    currentBoxBo.setProcessCommandTimeout(true, null);
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.tmcGetCoordinate(currentBoxBo, 3000, TmcInclinePrg.TMC_AUTO_INC,
                            results);
                }

                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK) {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                // 正常执行
                resultAll.addAll(new ArrayList<>(results.subList(0, 3)));
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, resultAll, directInvoke));
            } catch (Exception e) {
                logInfo = "命令:[" + commandName + "]异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, directInvoke));
            }
        });
    }

    @Override
    public void prepareStop(double ha, double va, boolean closeDevice, boolean directInvoke) {
        ThreadPoolUtil.getBoxPool().execute(() -> {
            try {
                commandName = "PrepareStop";
                logInfo = "命令:[" + commandName + "]开始>>>";
                log.info(logInfo);
                //region 1转到指定位置

                subStepName = "[转到指定位置]";
                logInfo = "***子过程:" + subStepName + "开始>>>";
                log.info(logInfo);
                RtCode rtCode;

                //控制器掉线/在线
                if (currentBoxBo == null)
                {
                    rtCode = CommonUtil.SYS_GRC_OFFLINE;
                }
                else
                {
                    currentBoxBo.setProcessCommandTimeout(true, 30L); //设置超时30s
                    currentBoxBo.setCommandName(commandName);
                    rtCode = NetCommands.autMakePositioning(currentBoxBo, ha, va, AutPosMode.AUT_NORMAL,
                            AutAtrMode.AUT_POSITION, false);
                }
                if (rtCode == null) {return;}
                //异常退出
                if (rtCode != CommonUtil.SYS_GRC_OK)
                {
                    log.error(logInfo);
                    //激发任务完成事件
                    surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                    return;
                }

                //正常执行
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                log.info(logInfo);
                //endregion

                //region 2关机
                //需要关机
                if (closeDevice)
                {
                    //控制器掉线/在线
                    if (currentBoxBo == null)
                    {
                        rtCode = CommonUtil.SYS_GRC_OFFLINE;
                    }
                    else
                    {
                        currentBoxBo.setProcessCommandTimeout(true, null);
                        currentBoxBo.setCommandName(commandName);
                        rtCode = NetCommands.comSwitchOffTPS(currentBoxBo,
                                ComTpsStopMode.COM_TPS_STOP_SHUT_DOWN);
                    }

                    if (rtCode == null) {return;}
                    //异常退出
                    if (rtCode != CommonUtil.SYS_GRC_OK)
                    {
                        log.error(logInfo);
                        //激发任务完成事件
                        surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
                        return;
                    }

                    // 正常执行
                    logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]***子过程:" + subStepName + "结束<<<";
                    log.info(logInfo);
                }
                //endregion

                //日志
                logInfo = "[" + rtCode.getName() + "：" + rtCode.getNote() + "]命令:[" + commandName + "]结束<<<";
                log.info(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(rtCode, commandName, null, directInvoke));
            }
            catch (Exception e)
            {
                log.info("命令:[{}]执行异常!", currentBoxBo != null ? currentBoxBo.getCommandName() : commandName);
                logInfo = "命令:[" + commandName + "]捕捉异常!---" + e.getMessage();
                log.error(logInfo);
                //激发任务完成事件
                surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_ERR, commandName, null, directInvoke));
            }
        });
    }

    @Override
    public void usePreSurveyResult(int chResultsCount, int chIndex, boolean directInvoke) {
        commandName = "UsePreSurveyResult";
        List<String> results = new ArrayList<>();
        results.add(chResultsCount + "");
        results.add(chIndex + "");
        //激发任务完成事件
        surveyOperateCompleted.accept(new OperateCompleteResult(CommonUtil.SYS_GRC_OK, commandName, results, directInvoke));
    }

}
