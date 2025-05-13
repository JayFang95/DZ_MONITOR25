package com.dzkj.robot.socket.netty2;

import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.constant.ControlBoxConstant;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

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
public class ServerMeteChannelHandler extends SimpleChannelInboundHandler<String> {

    private final ChannelHandlerUtil handlerUtil;

    public ServerMeteChannelHandler(ChannelHandlerUtil handlerUtil) {
        this.handlerUtil = handlerUtil;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) {
        String flag = "";
        String transMsg = ChannelHandlerUtil.hex2String(msg);
        if (StringUtils.isNotEmpty(transMsg) && transMsg.length() > 5) {
            flag = transMsg.substring(0, 6);
        }
        String[] split = transMsg.split("\r\n");
        if (flag.equals(ControlBoxConstant.REGISTER_PREFIX)){
            //处理注册逻辑
            handlerUtil.handlerRegisterEvent(context, split[0]);
        }else if (flag.equals(ControlBoxConstant.PING_PREFIX)){
            //处理心跳逻辑
            handlerUtil.handlerHeartEvent(context, split[0]);
        }else {
            // 2025/1/21 接收到正弦采集数据后返回到前端
            if (msg.startsWith("0103") && msg.endsWith("0D0A")){
                try {
                    WebSocketServer.sendInfo(new MessageVO(1000, msg));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.contains(ControlBoxConstant.REGISTER_PREFIX_HEX)
                    || msg.contains(ControlBoxConstant.PING_PREFIX_HEX)
                    || transMsg.contains("\r\n")){
                return;
            }
            handlerUtil.handlerMeasureEvent(context, msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("有客户端连接：{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("有客户端断开：{}", ctx.channel().remoteAddress());
        // 2023/8/2 记录控制器离线时间
        ChannelHandlerUtil.ONLINE_CHANNELS.forEach((key, value) -> {
            if (ctx.channel().remoteAddress() == value.channel().remoteAddress()){
                // 2023/7/13 保存离线记录
                handlerUtil.saveOnlineRecord(key, 0);
            }
        });
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("客户端{}异常：{}", ctx.channel().remoteAddress(), cause.getMessage());
    }
}
