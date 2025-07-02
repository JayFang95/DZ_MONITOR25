package com.dzkj.robot.socket.netty3;

import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import com.dzkj.robot.socket.common.HexEncoder;
import com.dzkj.robot.socket.common.HexDecoder;
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
public class NettySoundChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandlerUtil channelHandlerUtil;

    public NettySoundChannelInitializer(ChannelHandlerUtil channelHandlerUtil) {
        this.channelHandlerUtil = channelHandlerUtil;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //定义分割标识
//                .addLast(new LineBasedFrameDecoder(4 * 1024))
                //设置编解码
                .addLast(new HexEncoder())
                .addLast(new HexDecoder())
                //设置channel处理器
//                .addLast(new ServerIdleStateHandler())
                .addLast(new ServerSoundChannelHandler(channelHandlerUtil));
    }

}
