package com.dzkj.robot.socket;

import com.dzkj.constant.ControlBoxConstant;
import com.dzkj.robot.socket.common.ChannelHandlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/12/9
 * @description history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
@RestController
@RequestMapping("mt/common")
public class SocketController {

    /**
     * 添加socket client
     * @param code code
     */
    @RequestMapping("add/{code}")
    public void addSocketClient(@PathVariable String code){
        new Thread(() -> {
            Socket socket = null;
            OutputStream os = null;
            try {
                socket = new Socket("127.0.0.1", 8008);
                os = socket.getOutputStream();
                Socket finalSocket = socket;
                new Thread(() -> {
                    InputStream is = null;
                    try {
                        is = finalSocket.getInputStream();
                        int len;
                        byte[] bytes = new byte[1024];
                        while ((len = is.read(bytes)) != -1){
                            System.out.println(new String(bytes, 0, len));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }).start();
                int count = 0;
                String msg = "";
                do {
                    if (count == 0) {
                        msg = ControlBoxConstant.REGISTER_PREFIX + "DTU_" + code + "\r\n";
                    } else {
                        msg = ControlBoxConstant.PING_PREFIX + "DTU_" + code + "\r\n";
                    }
                    os.write(msg.getBytes());
                    os.flush();
                    count++;
                    TimeUnit.SECONDS.sleep(5);
                } while (count < 20);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (os != null){
                    try {
                        os.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @RequestMapping("test/netty/{clientId}/{serialNo}")
    public void testSendMsg(@PathVariable String clientId, @PathVariable String serialNo){
        int count = 0;
        while (count < 10){
            ChannelHandlerUtil.sendCommand("/" + clientId, serialNo, "TEST SEND COMMAND");
            count ++;
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        ChannelHandlerContext context1 = ServerChannelHandler.getOnlineChannels().get("TEST002");
//        if (context1 != null){
//            new Thread(() -> {
//                while (true){
//                    context1.writeAndFlush("TEST SEND MSG");
//                    try {
//                        TimeUnit.SECONDS.sleep(4);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }).start();
//        }
//        for (ChannelHandlerContext context : ServerChannelHandler.ONLINE_CHANNEL_LIST) {
//            while (true){
//                context.writeAndFlush("测试一下信息发送");
//                try {
//                    TimeUnit.SECONDS.sleep(4);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//        try {
//            TimeUnit.SECONDS.sleep(60);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

}
