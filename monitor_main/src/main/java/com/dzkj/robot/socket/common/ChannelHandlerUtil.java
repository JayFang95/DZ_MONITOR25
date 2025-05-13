package com.dzkj.robot.socket.common;

import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.constant.ControlBoxConstant;
import com.dzkj.entity.equipment.ControlBoxRecord;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.box.ControlBoxMete;
import com.dzkj.service.equipment.IControlBoxRecordService;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/3/26 10:24
 * @description ChannelHandlerUtil
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
@Component
public class ChannelHandlerUtil {

    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;
    @Autowired
    private IControlBoxRecordService boxRecordService;

    /**
     * 在线控制器绑定channel map
     */
    public static final ConcurrentMap<String, ChannelHandlerContext> ONLINE_CHANNELS = new ConcurrentHashMap<>();


    //region 消息处理
    /**
     * 注册逻辑处理
     * @param context channel上下文
     * @param msg 消息对象
     */
    public void handlerRegisterEvent(ChannelHandlerContext context, String msg) {
        String[] infos = msg.split("_");
        if (infos.length == 3){
            String serialNo = infos[2];
            // 注册时添加到在线map
            ChannelHandlerUtil.ONLINE_CHANNELS.put(serialNo, context);
            // redis 设置控制器信息
            redisTemplate.opsForValue().set(RedisConstant.PREFIX + serialNo, "1", ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS, TimeUnit.MILLISECONDS);
            // 通知页面更新
            try {
                WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_ON.getCode(), SocketMsgConst.CONTROL_ON.getMessage(), Collections.singletonList(infos[2])));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 2023/7/13 保存上线记录
            saveOnlineRecord(serialNo, 1);

            // 设置ControlBoxBo对象
            ControlBoxHandler.setControlBoxInfo(context.channel().remoteAddress().toString(), serialNo);
            //激活surveyBiz中上线事件
            List<ControlBoxAo> aoList = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(it -> serialNo.equals(it.getSerialNo()))
                    .collect(Collectors.toList());
            for (ControlBoxAo boxAo : aoList) {
                boxAo.setStatus("在线");
                boxAo.setOnlineTime(new Date());
                boxAo.getSurveyBiz().currentBoxOnOffLine();
            }
        }
    }

    /**
     * 控制器上线记录
     */
    public void saveOnlineRecord(String serialNo, int status) {
        ControlBoxRecord record = new ControlBoxRecord();
        record.setSerialNo(serialNo);
        record.setStatus(status);
        boxRecordService.save(record);
    }

    /**
     * 心跳逻辑处理
     * @param context channel上下文
     * @param msg 消息对象
     */
    public void handlerHeartEvent(ChannelHandlerContext context, String msg) {
        String clientId = context.channel().remoteAddress().toString();
        for (String key : ChannelHandlerUtil.ONLINE_CHANNELS.keySet()) {
            if (ChannelHandlerUtil.ONLINE_CHANNELS.get(key).channel().remoteAddress().equals(context.channel().remoteAddress())){
                ControlBoxHandler.setControlBoxInfo(clientId, key);
                // redis 心跳保活
                redisTemplate.opsForValue().set(RedisConstant.PREFIX + key, "1",
                        ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS, TimeUnit.MILLISECONDS);
                break;
            }
        }
    }

    /**
     * 测量反馈逻辑处理
     * @param context channel上下文
     * @param msg 消息对象
     */
    public void handlerMeasureEvent(ChannelHandlerContext context, String msg) {
        String clientId = context.channel().remoteAddress().toString();
        Optional<ControlBoxBo> optional = ControlBoxHandler.getOnlineControlBoxes().stream()
                .filter(item -> item.getClientId().equals(clientId)).findAny();
        //控制器下线后，原先的cBox已从列表中删除，上层有可能正在使用之前cBox携带的ClientId进行通讯操作返回消息--丢弃
        if (!optional.isPresent()) {
            return;
        }
        ControlBoxBo cBox = optional.get();
        cBox.setKeepAliveCounter(0);
        Optional<ControlBoxAo> boxAo = ControlBoxHandler.getAllControlBoxes().stream()
                .filter(item -> item.getSerialNo().equals(cBox.getSerialNo())).findAny();
        // 2024/3/25 判断是否是温度气压控制器返回信息
        if (boxAo.isPresent() && "温度气压控制器".equals(boxAo.get().getType())) {
            Optional<ControlBoxMete> optionalMete = ControlBoxHandler.getOnlineMeteBoxes().stream()
                    .filter(item -> item.getClientId().equals(clientId)).findAny();
            //控制器下线后，原先的cBox已从列表中删除，上层有可能正在使用之前cBox携带的ClientId进行通讯操作返回消息--丢弃
            optionalMete.ifPresent(mete -> mete.received(msg));
            return;
        }
        //消息处理
        cBox.received(msg);
    }
    //endregion


    /**
     * @description: 处理命令代码串
     * @author: jing.fang
     * @Date: 2023/2/20 15:34
     * @param clientId 通道id
     * @param serialNo 控制器序列号
     * @param commandCode ASCII 命令字符串
     **/
    public static boolean sendCommand(String clientId, String serialNo, String commandCode){
        try {
            ChannelHandlerContext context = ONLINE_CHANNELS.get(serialNo);
            if (context == null || !clientId.equals(context.channel().remoteAddress().toString())){
                return false;
            }
            context.writeAndFlush(commandCode).sync();
            return true;
        } catch (Exception e) {
            log.error("发送指令 {} 到控制器失败: {}", commandCode, e.getMessage());
            return false;
        }
    }

    /**
     * hex转字符串
     * @param hex hex
     * @return
     */
    public static String hex2String(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
        }
        return new String(bytes);
    }


}
