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
import java.util.*;
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
                log.error("页面通知信息发送异常：{}", e.getMessage(), e);
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
        } else {
            //判断是报警器格式进来的注册，先判断数据长度是否正常
            if (msg.length() == 94) {
                String serialNo = msg.substring(6, 22);
                // 注册时添加到在线map
                if(!ONLINE_CHANNELS.containsKey(serialNo)){
                    ONLINE_CHANNELS.put(serialNo, context);
                } else {
                    ChannelHandlerContext contextExit = ONLINE_CHANNELS.get(serialNo);
                    if (!contextExit.channel().remoteAddress().toString().equals(context.channel().remoteAddress().toString())) {
                        if (contextExit.channel().isActive()) {
                            contextExit.channel().close();
                            contextExit.close();
                        }
                        ONLINE_CHANNELS.put(serialNo, context);
                    }
                }
                if (redisTemplate.opsForValue().get(RedisConstant.PREFIX + serialNo) == null) {
                    // redis 设置控制器信息
                    redisTemplate.opsForValue().set(RedisConstant.PREFIX + serialNo, "1", ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS + 5000, TimeUnit.MILLISECONDS);
                    // 通知页面更新
                    try {
                        WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_ON.getCode(), SocketMsgConst.CONTROL_ON.getMessage(), Collections.singletonList(serialNo)));
                    } catch (IOException e) {
                        log.info("报警器上线通知异常：{}", e.getMessage());
                    }
                    // 2023/7/13 保存上线记录
                    saveOnlineRecord(serialNo, 1);

                    // 设置ControlBoxBo对象
                    ControlBoxHandler.setControlBoxInfo(context.channel().remoteAddress().toString(), serialNo);
                }
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

    /**
     * 声光报警器10秒间隔心疼
     */
    public void handlerSoundHeartEvent(ChannelHandlerContext context) {
        String clientId = context.channel().remoteAddress().toString();
        for (String key : ONLINE_CHANNELS.keySet()) {
            if (ONLINE_CHANNELS.get(key).channel().remoteAddress().equals(context.channel().remoteAddress())){
                ControlBoxHandler.setControlBoxInfo(clientId, key);
                // redis 心跳保活
                redisTemplate.opsForValue().set(RedisConstant.PREFIX + key, "1",
                        ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS + 5000, TimeUnit.MILLISECONDS);
                break;
            }
        }
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
     * @return String
     */
    public static String hex2String(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
        }
        return new String(bytes);
    }

    /**
     * @description: 发送声光报警指令
     * @param soundCfgInfo 配置信息(serialNo1,serialNo2...;报警时间;漏测开启标识(1, 0)，漏传开启标识(1,0)，超限开启标识(1,0))
     * @param type 类型：0-测试；1-漏测；2-漏传; 3-超限
     */
    public static void sendSoundAlarmCode(String soundCfgInfo, int type){
        try {
            if (soundCfgInfo != null && !soundCfgInfo.isEmpty() && !"manual".equals(soundCfgInfo)){
                String[] split = soundCfgInfo.split(";");
                String[] serialNoList = split[0].split(",");
                String[] alarmFlgList = split[2].split(",");
                if ((type == 1 && Objects.equals(alarmFlgList[0], "0"))
                        || (type == 2 && Objects.equals(alarmFlgList[1], "0"))
                        || (type == 3 && Objects.equals(alarmFlgList[2], "0"))){
                    log.info("未开启 {}(1-漏测，2-漏传，3-超限) 类型声光报警", type);
                    return;
                }
                log.info("开始 {}(1-漏测，2-漏传，3-超限) 类型声光报警", type);
                //发送声光报警开启指令
                for (String serialNo : serialNoList) {
                    ChannelHandlerContext context = ONLINE_CHANNELS.get(serialNo);
                    if (context != null){
                        try {
                            context.writeAndFlush("0106040E0003A938").sync();
                        } catch (InterruptedException e) {
                            log.error("发送声光开始指令到控制器 {} 失败: {}", serialNo, e.getMessage());
                        }
                    }
                }
                log.error("发送 {}(1-漏测，2-漏传，3-超限) 开启声光指令到控制器 {} 结束", type, split[0]);
                //发送声光报警关闭指令
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    private int count = 0;
                    @Override
                    public void run() {
                        count++;
                        for (String serialNo : serialNoList) {
                            ChannelHandlerContext context = ONLINE_CHANNELS.get(serialNo);
                            if (context != null){
                                try {
                                    context.writeAndFlush("0106040E0000E939").sync();
                                } catch (InterruptedException e) {
                                    log.error("发送声光关闭指令到控制器 {} 失败: {}", serialNo, e.getMessage());
                                }
                            }
                        }
                        log.error("发送声光停止指令到控制器 {} 结束", split[0]);
                        if (count == 2) {
                            timer.cancel(); // 任务执行两次后取消定时器
                        }
                    }
                };
                // 设置定时任务，延迟60000毫秒后开始，只执行一次
                timer.schedule(task, Integer.parseInt(split[1]) * 1000L, 60 * 1000L);
            }
        } catch (NumberFormatException e) {
            log.error("发送声光报警指令失败: {}", e.getMessage(), e);
        }
    }


}
