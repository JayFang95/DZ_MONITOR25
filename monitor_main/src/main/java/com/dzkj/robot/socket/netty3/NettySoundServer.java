package com.dzkj.robot.socket.netty3;

import com.dzkj.common.util.SpringContextUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/3/26 21:16
 * @description netty 服务端
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class NettySoundServer {

    private static final EventLoopGroup MAIN_GROUP = new NioEventLoopGroup(1);
    private static final EventLoopGroup WORK_GROUP = new NioEventLoopGroup();

    private static Channel sc;
    private static int count = 0;

    public static void initNettyServer(int port) throws InterruptedException {
        log.info("nettySound server 初始化 ...");
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    //设置主、工作线程组
                    .group(MAIN_GROUP, WORK_GROUP)
                    //设置通道类型
                    .channel(NioServerSocketChannel.class)
                    //设置初始可连接队列大小：连接数满了后放入队列
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置服务处理逻辑
                    .childHandler(new NettySoundChannelInitializer(
                            SpringContextUtil.getBean("channelHandlerUtil")
                            ));
            sc = serverBootstrap.bind(new InetSocketAddress(port)).sync().channel();
            log.info("netty server 启动成功");
        } catch (InterruptedException e) {
            if (count > 5){
                TimeUnit.SECONDS.sleep(10L * (count + 1));
            }else {
                TimeUnit.MINUTES.sleep(3L * count / 5);
            }
            count ++;
            log.info("netty server 初始化异常: {}, 准备第{}次重试...", e.getMessage(), count);
            initNettyServer(port);
        }
    }

    public static void destroyNettyServer(){
        if (sc != null){
            sc.close();
        }
        MAIN_GROUP.shutdownGracefully();
        WORK_GROUP.shutdownGracefully();
    }

}
