package com.dzkj.robot.socket.netty3;

import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
public class ServerSoundChannelHandler extends SimpleChannelInboundHandler<String> {


    private final ChannelHandlerUtil channelHandlerUtil;

    public ServerSoundChannelHandler(ChannelHandlerUtil channelHandlerUtil) {
        this.channelHandlerUtil = channelHandlerUtil;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String msg) {
        // 心跳格式：前6位固定01 1B 2A + 16位IMEI/MAC + 8位注册码 + 2位设备型号代号 + 20位预留字段 + 36位配置 + 6位CRC16
        if (msg.startsWith("011B2A")){
            //获取报警器的IMEI码：将IMEI码作为key，将channel作为value存入redis
            channelHandlerUtil.handlerRegisterEvent(context, msg);
            //心跳保活
            channelHandlerUtil.handlerSoundHeartEvent(context);
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
                channelHandlerUtil.saveOnlineRecord(key, 0);
            }
        });
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("客户端{}异常：{}", ctx.channel().remoteAddress(), cause.getMessage());
    }

}
