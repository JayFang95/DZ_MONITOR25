package com.dzkj.robot.socket.netty;

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
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.service.equipment.IControlBoxRecordService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
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
 * @date 2023/3/27 14:15
 * @description 通道事件绑定处理器
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class ServerChannelHandlerBk extends SimpleChannelInboundHandler<String> {

    /**
     * 在线控制器绑定channel map
     */
//    private static final ConcurrentMap<String, ChannelHandlerContext> ONLINE_CHANNELS = new ConcurrentHashMap<>();

    private final RedisTemplate<String ,Object> redisTemplate;
    private final IControlBoxRecordService boxRecordService;

    public ServerChannelHandlerBk(RedisTemplate<String, Object> redisTemplate,
                                  IControlBoxRecordService boxRecordService) {
        this.redisTemplate = redisTemplate;
        this.boxRecordService = boxRecordService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) throws Exception {
        String flag = "";
        if (StringUtils.isNotEmpty(msg) && msg.length() > 5) {
            flag = msg.substring(0, 6);
        }
        if (flag.equals(ControlBoxConstant.REGISTER_PREFIX)){
            //处理注册逻辑
            handlerRegisterEvent(context, msg);
        }else if (flag.equals(ControlBoxConstant.PING_PREFIX)){
            //处理心跳逻辑
            handlerHeartEvent(context, msg);
        }else {
            handlerMeasureEvent(context, msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有客户端连接：{}", ctx.channel().remoteAddress());
//        ctx.channel().writeAndFlush("0103000B0001F5C8");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("有客户端断开：{}", ctx.channel().remoteAddress());
        // 2023/8/2 记录控制器离线时间
        ChannelHandlerUtil.ONLINE_CHANNELS.forEach((key, value) -> {
            if (ctx.channel().remoteAddress() == value.channel().remoteAddress()){
                // 2023/7/13 保存离线记录
                saveOnlineRecord(key, 0);
            }
        });
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("客户端{}异常：{}", ctx.channel().remoteAddress(), cause.getMessage());
    }

    //region
    /**
     * 注册逻辑处理
     * @param context channel上下文
     * @param msg 消息对象
     */
    private void handlerRegisterEvent(ChannelHandlerContext context, String msg) {
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
    private void saveOnlineRecord(String serialNo, int status) {
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
    private void handlerHeartEvent(ChannelHandlerContext context, String msg) {
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
    private void handlerMeasureEvent(ChannelHandlerContext context, String msg) {
        String clientId = context.channel().remoteAddress().toString();
        Optional<ControlBoxBo> optional = ControlBoxHandler.getOnlineControlBoxes().stream()
                .filter(item -> item.getClientId().equals(clientId)).findAny();
        //控制器下线后，原先的cBox已从列表中删除，上层有可能正在使用之前cBox携带的ClientId进行通讯操作返回消息--丢弃
        if (!optional.isPresent()) {
            return;
        }
        if (msg.contains(ControlBoxConstant.REGISTER_PREFIX) || msg.contains(ControlBoxConstant.PING_PREFIX)){
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
}
