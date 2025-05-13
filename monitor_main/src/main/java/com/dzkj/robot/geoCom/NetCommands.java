package com.dzkj.robot.geoCom;

import com.dzkj.bean.RtCode;
import com.dzkj.bean.TmcAngSwitch;
import com.dzkj.bean.TmcStation;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.common.CommonUtil;
import com.dzkj.enums.*;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description GeoCOM命令集——网口(TCP/IP)
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class NetCommands {

    /**
     * 命令行休止符
     */
    private final static String M_TERMINATOR = "\r\n";

    /**
     * @description: 获取棱镜常数
     * @author: jing.fang
     * @Date: 2023/2/16 14:32
     * @param cBox cBox
     * @param results 棱镜常数值(双精度)
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetPrismCorr(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetPrismCorr").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 设置角度补偿开关
     * @author: jing.fang
     * @Date: 2023/2/18 14:06
     * @param cBox cBox
     * @param swAngSwitch TMC_ANG_SWITCH开关参数
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcSetAngSwitch(ControlBoxBo cBox, TmcAngSwitch swAngSwitch)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_SetAngSwitch").getCode();
        commandCode +=swAngSwitch.getEInclineCorr().getCode() +"," +
                swAngSwitch.getEStandAxisCorr().getCode() + "," +
                swAngSwitch.getECollimationCorr().getCode() + "," +
                swAngSwitch.getETiltAxisCorr().getCode() + "," +
                M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 获取角度补偿开关状态
     * @author: jing.fang
     * @Date: 2023/2/18 14:11
     * @param cBox cBox
     * @param results TMC_ANG_SWITCH开关参数列表
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetAngSwitch(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetAngSwitch").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 设置测站
     * @author: jing.fang
     * @Date: 2023/2/18 14:12
     * @param cBox cBox
     * @param station 测站坐标:dE0,dN0,dH0,dHi
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcSetStation(ControlBoxBo cBox, TmcStation station)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_SetStation").getCode();
        commandCode += station.getDE0() +
                "," + station.getDN0() +
                "," + station.getDH0() +
                "," + station.getDHi() + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 获得测站坐标
     * @author: jing.fang
     * @Date: 2023/2/18 14:14
     * @param cBox cBox
     * @param results TMC_STATION测站坐标列表:dE0,dN0,dH0,dHi
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetStation(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetStation").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 获得正倒镜状态(1-正镜)
     * @author: jing.fang
     * @Date: 2023/2/18 14:15
     * @param cBox cBox
     * @param results TMC_FACE:1/2
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetFace(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetFace").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 设置方位角
     * @author: jing.fang
     * @Date: 2023/2/18 14:16
     * @param cBox cBox
     * @param hzOrientation hzOrientation
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcSetOrientation(ControlBoxBo cBox, double hzOrientation)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_SetOrientation").getCode();
        commandCode += hzOrientation + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 测角测距
     * @author: jing.fang
     * @Date: 2023/2/18 14:18
     * @param cBox cBox
     * @param command command
     * @param mode mode
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcDoMeasure(ControlBoxBo cBox, TmcMeasurePrg command, TmcInclinePrg mode)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_DoMeasure").getCode();
        commandCode += command.getCode() + "," +
                mode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 获取测量坐标
     * @author: jing.fang
     * @Date: 2023/2/18 14:19
     * @param cBox cBox
     * @param waitTime 超时时间
     * @param mode 倾斜补偿模式
     * @param results  Coordinate对象列表:dE,dN,dH,CoordTime,dE_Cont,dN_Cont,dH_Cont,CoordContTime
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetCoordinate(ControlBoxBo cBox, long waitTime, TmcInclinePrg mode, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetCoordinate").getCode();
        commandCode +=waitTime +"," +
                mode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 获取目标高度
     * @author: jing.fang
     * @Date: 2023/2/18 14:21
     * @param cBox cBox
     * @param results TMC_HEIGHT值
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetHeight(ControlBoxBo cBox,  List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetHeight").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 设置目标高度
     * @author: jing.fang
     * @Date: 2023/2/18 14:22
     * @param cBox cBox
     * @param height 目标高值
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcSetHeight(ControlBoxBo cBox, double height)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_SetHeight").getCode();
        commandCode +=height+ M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 返回完整测量值列表:角度、倾斜和距离测量值
     * @author: jing.fang
     * @Date: 2023/2/18 14:23
     * @param cBox cBox
     * @param waitTime 等待时间
     * @param mode mode
     * @param results 水平角Ha,竖直角Va,角度测量精度Aa,横向倾斜角度Tia,纵向倾斜角度Lia,倾斜精度Ai,斜距Sd,测量时间time
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode tmcGetFullMeas(ControlBoxBo cBox, long waitTime, TmcInclinePrg mode, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "TMC_GetFullMeas").getCode();
        commandCode += waitTime + "," +
                mode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }
    // endregion

    // region EDM
    /**
     * @description: 激光导向-开关
     * @author: jing.fang
     * @Date: 2023/2/18 14:25
     * @param cBox cBox
     * @param eLaser eLaser
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode edmLaserPointer(ControlBoxBo cBox, OnOffType eLaser)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "EDM_Laserpointer").getCode();
        commandCode += eLaser.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }
    //endregion
    /**
     * @description: 开机
     * @author: jing.fang
     * @Date: 2023/2/18 14:44
     * @param cBox cBox
     * @param eOnMode  eOnMode
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode comSwitchOnTPS(ControlBoxBo cBox, ComTpsStartUpMode eOnMode)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "COM_SwitchOnTPS").getCode();
        commandCode += eOnMode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 关机
     * @author: jing.fang
     * @Date: 2023/2/18 14:45
     * @param cBox cBox
     * @param eOffMode eOffMode
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode comSwitchOffTPS(ControlBoxBo cBox, ComTpsStopMode eOffMode)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "COM_SwitchOffTPS").getCode();
        commandCode += eOffMode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }
    // endregion

    // region CSV
    /**
     * @description: 获取仪器号
     * @author: jing.fang
     * @Date: 2023/2/18 14:46
     * @param cBox cBox
     * @param results 仪器号(long数字)
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode csvGetInstrumentNo(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "CSV_GetInstrumentNo").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 获取仪器名
     * @author: jing.fang
     * @Date: 2023/2/18 14:47
     * @param cBox cBox
     * @param results 仪器名
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode csvGetInstrumentName(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "CSV_GetInstrumentName").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }

    /**
     * @description: 获取仪器电池信息
     * @author: jing.fang
     * @Date: 2023/2/18 14:48
     * @param cBox cBox
     * @param results 电池信息:容量百分比,电源类型(1外置，2内置),无效参数
     * @return com.dzkj.bean.RtCode  GRC_OK/GRC_LOW_POWER/GRC_BATT_EMPTY
    **/
    public static RtCode csvCheckPower(ControlBoxBo cBox, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "CSV_CheckPower").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }
    //endregion

    //region AUT
    /**
     * @description: 取消耗时长的操作，如AUT_Search、AUT_MakePositioning、 AUT_SearchNext等
     * @author: jing.fang
     * @Date: 2023/2/18 14:50
     * @param cBox cBox
     * @param clientId  clientId
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode autCancel(ControlBoxBo cBox, String clientId)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "AUT_Cancel").getCode();
        commandCode += M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 执行自动目标搜索
     * @author: jing.fang
     * @Date: 2023/2/18 14:51
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode autSearch(ControlBoxBo cBox, double hzArea, double vArea, boolean bDummy)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "AUT_Search").getCode();
        commandCode += hzArea+"," +
                vArea+",0" +
                M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 精确目标定位
     * @author: jing.fang
     * @Date: 2023/2/18 14:53
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode autFineAdjust(ControlBoxBo cBox, double dSrchHz, double dSrchV, boolean bDummy)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "AUT_FineAdjust").getCode();
        commandCode += dSrchHz +"," +
                dSrchV + ",0" +
                M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 望远镜照准指定方向
     * @author: jing.fang
     * @Date: 2023/2/18 14:55
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode autMakePositioning(ControlBoxBo cBox, double hz, double v, AutPosMode posMode, AutAtrMode atrMode, boolean bDummy)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "AUT_MakePositioning").getCode();
        commandCode += hz + "," +
                v + "," +
                posMode.getCode() + "," +
                atrMode.getCode() + ",0" +
                M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }

    /**
     * @description: 将望远镜转向另一面
     * @author: jing.fang
     * @Date: 2023/2/18 14:57
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode autChangeFace(ControlBoxBo cBox, AutPosMode posMode, AutAtrMode atrMode, boolean bDummy)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "AUT_ChangeFace").getCode();
        commandCode +=posMode.getCode() + "," +
                atrMode.getCode() + ",0" +
                M_TERMINATOR;
        return cBox.processCommand(commandCode, null);
    }
    //endregion
    /**
     * @description: 测量水平角(Hz)、竖直角(Vz)、斜距(Sd)、测量名师
     * @author: jing.fang
     * @Date: 2023/2/18 14:58
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode bapMeasDistanceAngle(ControlBoxBo cBox, BapMeasurePrg distMode, List<String> results)
    {
        String commandCode = CommonUtil.getCommandCodeByName(DeviceType.LEI_CA, "BAP_MeasDistanceAngle").getCode();
        commandCode += distMode.getCode() + M_TERMINATOR;
        return cBox.processCommand(commandCode, results);
    }
    // endregion

}
