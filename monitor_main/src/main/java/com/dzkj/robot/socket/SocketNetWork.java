package com.dzkj.robot.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2022/10/20 9:28
 * @description socket 网络管理类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class SocketNetWork {

    private static final int MAX_POOL = Runtime.getRuntime().availableProcessors() / 3 * 2;

    /**
     * 所有在线的socket集合， key使用控制器编号
     */
    public static final Map<String, Socket> ONLINE_SOCKET_MAP = new ConcurrentHashMap<>();
    /**
     * 线程池
     */
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(MAX_POOL ,MAX_POOL, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.AbortPolicy());
    /**
     * socket重连统计
     */
    private static int reconnectCount = 0;
    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * @description: Socket server 初始化创建
     * @author: jing.fang
     * @Date: 2023/2/20 15:32
    **/
    public static void init(RedisTemplate<String, Object> redisTemplate){
        log.info("开始初始化socket服务器...");
        SocketNetWork.redisTemplate = redisTemplate;
        try {
            ServerSocket serverSocket = new ServerSocket(8008);
            log.info("=====socket服务器创建完成=====");
            start(serverSocket);
        } catch (IOException e) {
            if (reconnectCount > 5){
                log.error("socket服务重启5次后仍然失败:{}, 进入休眠后重试", e.getMessage());
                try {
                    TimeUnit.MINUTES.sleep(3 * (reconnectCount / 5));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            reconnectCount ++;
            log.error("socket服务启动失败:{}, 即将发起第{}次重新创立连接", e.getMessage(), reconnectCount);
            try {
                TimeUnit.SECONDS.sleep(5L * reconnectCount);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            init(redisTemplate);
        }
    }

    /**
     * @description 开启socket服务监听
     * @author jing.fang
     * @date 2022/10/20 9:46
    **/
    private static void start(ServerSocket serverSocket){
        THREAD_POOL.execute(() -> {
            log.info("开始客户端连接监听...");
            while (true){
                try {
                    Socket client = serverSocket.accept();
                    // 开启客户端消息监听
                    THREAD_POOL.execute(new SocketClientHandler(client, redisTemplate));
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("客户端连接异常：{}",e.getMessage());
                }
            }
        });
    }

    /**
     * @description: 处理命令代码串
     * @author: jing.fang
     * @Date: 2023/2/20 15:34
     * @param clientId 客户端id
     * @param serialNo 客户端序列号
     * @param commandCode ASCII 命令字符串
    **/
    public static boolean sendCommand(String clientId, String serialNo, String commandCode){
        Socket socket = ONLINE_SOCKET_MAP.get(serialNo);
        if (socket == null){ return false; }
        String socketAddress = (socket.getRemoteSocketAddress() + "").replace("/", "");
        if (socketAddress.equals(clientId)){
            try {
                OutputStream os = socket.getOutputStream();
                os.write(commandCode.getBytes());
                os.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
