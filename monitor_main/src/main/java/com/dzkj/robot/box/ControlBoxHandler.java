package com.dzkj.robot.box;

import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.robot.survey.SurveyBiz;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/17
 * @description 控制器处理
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class ControlBoxHandler {

    /**
     * 在线控制器集合
     */
    private final static List<ControlBoxBo> ONLINE_CONTROL_BOXES = new ArrayList<>();
    private final static List<ControlBoxMete> ONLINE_METE_BOXES = new ArrayList<>();
    /**
     * 所有添加的控制器信息
     */
    private final static List<ControlBoxAo> ALL_CONTROL_BOXES = new ArrayList<>();

    /**
     * 获取在线控制器集合
    **/
    public static List<ControlBoxBo> getOnlineControlBoxes(){
        return ONLINE_CONTROL_BOXES;
    }
    /**
     * 获取在线控制器集合
     **/
    public static List<ControlBoxMete> getOnlineMeteBoxes(){
        return ONLINE_METE_BOXES;
    }

    /**
     * 删除离线控制器
     **/
    public static void removeOutlineBox(ControlBoxBo cBox){
        //关闭计数器，保证线程结束对象回收
        if (cBox.getCountDownLatch() != null) {
            cBox.getCountDownLatch().countDown();
        }
        // 2024/3/25 修改温度气压控制采集器列表
        Optional<ControlBoxMete> optional = ONLINE_METE_BOXES.stream().filter(item -> item.getSerialNo().equals(cBox.getSerialNo())).findAny();
        optional.ifPresent(ONLINE_METE_BOXES::remove);
        ONLINE_CONTROL_BOXES.remove(cBox);
    }

    /**
     * @description: 通过设备上线时发送的注册包消息，完善设备信息
     * @author: jing.fang
     * @Date: 2023/2/17 20:44
     * @param clientId IP+PORT
     * @param serialNo 设备序列号
    **/
    public static void setControlBoxInfo(String clientId, String serialNo)
    {
        // 通过ClientID找到对应设备，保活
        Optional<ControlBoxBo> optionalBox = ONLINE_CONTROL_BOXES.stream().filter(it -> Objects.equals(it.getClientId(), clientId)).findAny();
        if (optionalBox.isPresent())
        {
            ControlBoxBo cBox = optionalBox.get();
            cBox.setKeepAliveCounter(0);
            return;
        }
        //携带注册包信息重新上线
        optionalBox = ONLINE_CONTROL_BOXES.stream().filter(it -> Objects.equals(it.getSerialNo(), serialNo)).findAny();

        //控制器还在保活期内，短暂断线重新上线，原cBox赋值新ClientId后，保活
        if (optionalBox.isPresent())
        {
            log.info("{} 改变ID重新上线", optionalBox.get().getSerialNo());
            ControlBoxBo cBox = optionalBox.get();
            cBox.setClientId(clientId);
            cBox.setKeepAliveCounter(0);
            return;
        }
        //重新生成控制器对象
        Date onlineTime = new Date();
        ControlBoxBo cBox = new ControlBoxBo();
        cBox.setOnLine(true);
        cBox.setOnLineTime(onlineTime);
        cBox.setClientId(clientId);
        cBox.setSerialNo(serialNo);
        cBox.setKeepAliveCounter(0);
        ONLINE_CONTROL_BOXES.add(cBox);
        // 2024/3/25 修改温度气压控制采集器列表
        ONLINE_METE_BOXES.add(DzBeanUtils.propertiesCopy(cBox, ControlBoxMete.class));
        updateOnlineControlBoxes(serialNo, onlineTime);
    }

    /**
     * 获取所有控制器集合
     **/
    public static List<ControlBoxAo> getAllControlBoxes(){
        return ALL_CONTROL_BOXES;
    }

    /**
     * 更新离线控制器信息
     **/
    public static void  updateOutlineControlBoxes(ControlBoxBo cBox){
        List<ControlBoxAo> aoList = ALL_CONTROL_BOXES.stream().filter(item -> item.getSerialNo().equals(cBox.getSerialNo())).collect(Collectors.toList());
        for (ControlBoxAo boxAo : aoList) {
            boxAo.setStatus("离线");
            boxAo.setOnlineTime(null);
            if (boxAo.getSurveyBiz()!=null){
                //激活离线事件
                boxAo.getSurveyBiz().currentBoxOnOffLine();
                boxAo.getSurveyBiz().setControlBoxBo(null);
                if (boxAo.getSurveyBiz().getDeviceBiz() != null){
                    boxAo.getSurveyBiz().getDeviceBiz().refreshCurrentBoxBo(null);
                    boxAo.getSurveyBiz().setDeviceBiz(null);
                }
            }
        }
    }

    /**
     * 控制器上线时更新信息
     **/
    public static void  updateOnlineControlBoxes(String serialNo, Date onlineTime){
        List<ControlBoxAo> aoList = ALL_CONTROL_BOXES.stream().filter(item -> item.getSerialNo().equals(serialNo)).collect(Collectors.toList());
        for (ControlBoxAo boxAo : aoList) {
            boxAo.setStatus("在线");
            boxAo.setOnlineTime(onlineTime);
        }
    }

    /**
     * 添加控制器信息
     **/
    public static void addControlBoxAo(ControlBoxAo cBox){
        cBox.setDeviceType(0);
        cBox.setSurveyStatus("停测");
        cBox.setDeviceType(0);
        Optional<ControlBoxBo> optional = ONLINE_CONTROL_BOXES.stream()
                .filter(item -> Objects.equals(item.getSerialNo(), cBox.getSerialNo())).findAny();
        if (optional.isPresent()){
            cBox.setStatus("在线");
            cBox.setOnlineTime(optional.get().getOnLineTime());
        }else {
            cBox.setStatus("离线");
        }
        ALL_CONTROL_BOXES.add(cBox);
    }

    /**
     * 删除控制器
     **/
    public static void removeBoxAo(Long id){
        Optional<ControlBoxAo> optional = ALL_CONTROL_BOXES.stream().filter(item -> item.getId().equals(id)).findAny();
        optional.ifPresent(ALL_CONTROL_BOXES::remove);
    }

    /**
     * 获取指定控制器业务对象
     * @param controlBoxId 控制器id
     */
    public static SurveyBiz getBoxSurveyBiz(Long controlBoxId){
        for (ControlBoxAo boxAo : ALL_CONTROL_BOXES) {
            if (boxAo.getId().equals(controlBoxId)){
                return boxAo.getSurveyBiz();
            }
        }
        return null;
    }

}
