package com.dzkj.robot.socket.netty;

import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/26 21:21
 * @description channel初始化器
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerUtil handlerUtil;

    public NettyChannelInitializer(ChannelHandlerUtil handlerUtil) {
        this.handlerUtil = handlerUtil;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //定义分割标识
                .addLast(new LineBasedFrameDecoder(4 * 1024))
                //设置编解码
                .addLast(new StringEncoder())
                .addLast(new StringDecoder())
                //设置channel处理器
//                .addLast(new ServerIdleStateHandler())
                .addLast(new ServerChannelHandler(handlerUtil));
    }

}
