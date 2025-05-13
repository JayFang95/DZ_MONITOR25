package com.dzkj.robot.socket;

import com.dzkj.common.constant.RedisConstant;
import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.common.util.DateUtil;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.constant.ControlBoxConstant;
import com.dzkj.robot.box.ControlBoxAo;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/17
 * @description socket 客户端连接处理类
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class SocketClientHandler implements Runnable{

    /**
     * Socket 通信对象
    **/
    private final Socket socket;
    private final String clientId;
    private String serialNo;
    private final RedisTemplate<String, Object> redisTemplate;

    public SocketClientHandler(Socket socket, RedisTemplate<String, Object> redisTemplate) {
        this.socket = socket;
        this.clientId = (socket.getRemoteSocketAddress()+"").replace("/","");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        InputStream is = null;
        BufferedReader br = null;
        try {
            // 设置读取超时时间
            socket.setSoTimeout(ControlBoxConstant.READ_TIMEOUT_MILLIONS);
            // 接收客户端信息
            is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            String message;
            while ((message = br.readLine()) != null){
                if(StringUtils.isNotEmpty(serialNo)){
                    // redis 保活更新，比心跳间隔多500毫秒，防止同时过期出现在线误删异常
                    redisTemplate.opsForValue().set(RedisConstant.PREFIX + serialNo, "1", ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS, TimeUnit.MILLISECONDS);
                }
                // 消息处理
                onReceive(message);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if(br != null){
                try {
                    br.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("socket{}通信结束，关闭资源" , socket.getRemoteSocketAddress());
        }
    }

    /**
     * 接收到消息实际(message)
    **/
    private void onReceive(String message) {
        String regFlag = "";
        String heartBeatFlag = "";
        if (message.length() > 5)
        {
            regFlag = message.substring(0, 6);
            heartBeatFlag = regFlag;
        }
        //设备连接时，发送的注册包信息
        if (regFlag.equals(ControlBoxConstant.REGISTER_PREFIX))
        {
            String[] infos = message.split("_");
            if (infos.length == 3){
                serialNo = infos[2];
                // 注册时添加到在线map
                SocketNetWork.ONLINE_SOCKET_MAP.put(serialNo, socket);
                // redis 设置控制器信息
                redisTemplate.opsForValue().set(RedisConstant.PREFIX + serialNo, "1", ControlBoxConstant.SOCKET_REDIS_TIMEOUT_MILLIONS, TimeUnit.MILLISECONDS);
                // 通知页面更新
                try {
                    WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_ON.getCode(), SocketMsgConst.CONTROL_ON.getMessage(), Collections.singletonList(serialNo)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 设置ControlBoxBo对象
            ControlBoxHandler.setControlBoxInfo(clientId, serialNo);
            //激活surveyBiz中上线事件
            List<ControlBoxAo> aoList = ControlBoxHandler.getAllControlBoxes().stream()
                    .filter(it -> serialNo.equals(it.getSerialNo()) && it.getBindMission())
                    .collect(Collectors.toList());
            for (ControlBoxAo boxAo : aoList) {
                boxAo.setStatus("在线");
                boxAo.setOnlineTime(new Date());
                boxAo.getSurveyBiz().currentBoxOnOffLine();
            }
            log.info("上线...{}在线数:{}--{}",serialNo, ControlBoxHandler.getOnlineControlBoxes().size(), DateUtil.dateToDateString(new Date()));
        }
        else if (heartBeatFlag.equals(ControlBoxConstant.PING_PREFIX))
        {
            ControlBoxHandler.setControlBoxInfo(clientId, serialNo);
        }
        else
        {
            Optional<ControlBoxBo> optional = ControlBoxHandler.getOnlineControlBoxes().stream().filter(item -> item.getClientId().equals(clientId)).findAny();
            //控制器下线后，原先的cBox已从列表中删除，上层有可能正在使用之前cBox携带的ClientId进行通讯操作返回消息--丢弃
            if (!optional.isPresent()) {
                return;
            }
            ControlBoxBo cBox = optional.get();
            cBox.setKeepAliveCounter(0);
            cBox.received(message);
        }
    }

}
