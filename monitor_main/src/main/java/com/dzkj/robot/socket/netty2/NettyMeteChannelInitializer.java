package com.dzkj.robot.socket.netty2;

import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.robot.socket.netty.HexDecoder;
import com.dzkj.robot.socket.netty.HexEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

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
public class NettyMeteChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerUtil handlerUtil;

    public NettyMeteChannelInitializer(ChannelHandlerUtil handlerUtil) {
        this.handlerUtil = handlerUtil;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //设置编解码
                .addLast(new HexEncoder())
                .addLast(new HexDecoder())
                //设置channel处理器
                .addLast(new ServerMeteChannelHandler(handlerUtil));
    }

}
