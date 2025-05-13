package com.dzkj.robot.socket.netty;

import com.dzkj.common.enums.SocketMsgConst;
import com.dzkj.config.MessageVO;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.robot.box.ControlBoxBo;
import com.dzkj.robot.box.ControlBoxHandler;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/27 14:14
 * @description 心跳处理
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class ServerIdleStateHandler extends IdleStateHandler {

    private static final int READER_IDLE_TIME_SECONDS = 5050;
    private static final int WRITER_IDLE_TIME_SECONDS = 0;
    private static final int ALL_IDLE_TIME_SECONDS = 0;

    public ServerIdleStateHandler() {
        super(READER_IDLE_TIME_SECONDS, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info("客户端 {} {}内没有读取到数据", ctx.channel().remoteAddress(), READER_IDLE_TIME_SECONDS);
        String channelId = ctx.channel().id().asLongText();
        Optional<ControlBoxBo> optional = ControlBoxHandler.getOnlineControlBoxes().stream().filter(item -> item.getClientId().equals(channelId)).findAny();
        if (optional.isPresent()){
            int keepAliveCounter = optional.get().getKeepAliveCounter();
            if (keepAliveCounter < 3){
                //在保活期内，计数器加1
                optional.get().setKeepAliveCounter(keepAliveCounter + 1);
            }else {
                //超过保活统计阈值，关闭连接
                ctx.channel().close();
                ctx.close();

                //离线处理
                ControlBoxHandler.updateOutlineControlBoxes(optional.get());
                ControlBoxHandler.removeOutlineBox(optional.get());
                for (Map.Entry<String, ChannelHandlerContext> entry : ChannelHandlerUtil.ONLINE_CHANNELS.entrySet()) {
                    if (entry.getValue().channel().remoteAddress().equals(ctx.channel().remoteAddress())){
                        log.info("控制器{} 下线", entry.getKey());
                        try {
                            // 掉线通知界面
                            WebSocketServer.sendInfo(new MessageVO(SocketMsgConst.CONTROL_OUT.getCode(),
                                    SocketMsgConst.CONTROL_OUT.getMessage(), Collections.singletonList(entry.getKey())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }else {
            //当前通道已失去绑定对象，直接关闭
            ctx.channel().close();
            ctx.close();
        }
    }

}
