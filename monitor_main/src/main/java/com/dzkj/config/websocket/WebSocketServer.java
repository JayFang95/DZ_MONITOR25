package com.dzkj.config.websocket;

import com.dzkj.config.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2020/8/12
 * @description websocket服务端实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@ServerEndpoint(value = "/websocket/{userId}/{createTime}", encoders = ServerEncoder.class)
@Component
public class WebSocketServer {
    /**
     * 原子类：统计在线人数
     */
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final CopyOnWriteArraySet<WebsocketClient> WEB_SOCKET_SET = new CopyOnWriteArraySet<WebsocketClient>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    public WebSocketServer() {

    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId, @PathParam("createTime") String createTime) {
        // 查找是否存在相同userId的客户端对象
        Optional<WebsocketClient> optional = WEB_SOCKET_SET.stream()
                .filter(item -> item.getUserId().equals(userId) && item.getCreateTime().equals(createTime)).findFirst();
        if (!optional.isPresent()) {
            WebsocketClient client = new WebsocketClient(userId, createTime, session);
            // 客户端添加到集合
            WEB_SOCKET_SET.add(client);
            //在线数加1
            addOnlineCount();
            log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        }
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        Optional<WebsocketClient> optional = WEB_SOCKET_SET.stream()
                .filter(item -> item.getSession().getId().contains(session.getId())).findFirst();
        if (optional.isPresent()) {
            WebsocketClient client = optional.get();
            //从set中删除客户端
            WEB_SOCKET_SET.remove(client);
            //在线数减1
            subOnlineCount();
            log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * @param session session
     * @param error error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 群发消息
     */
    public static void sendInfo(MessageVO message) throws IOException {
        for (WebsocketClient item : WEB_SOCKET_SET) {
            sendMessage(item.getSession(), message);
        }
    }

    /**
     * 发送指定在线用户
     */
    public static void sendInfoTo(MessageVO message, Long userId) throws IOException {
        for (WebsocketClient item : WEB_SOCKET_SET) {
            if (userId!=null && userId.equals(item.getUserId())) {
                sendMessage(item.getSession(), message);
            }
        }
    }

    /**
     * 发送消息
     */
    public static void sendMessage(Session session, MessageVO message) throws IOException {
        try {
            // The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method
            // 高并发时存在session竞争问题
            synchronized (session){
                if (session.isOpen()) {
                    session.getBasicRemote().sendObject(message);
                }
            }
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }


    public static synchronized int getOnlineCount() {
        return Math.max(ATOMIC_INTEGER.get(), 0);
    }

    public static synchronized void addOnlineCount() {
        ATOMIC_INTEGER.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        if(ATOMIC_INTEGER.get() <= 0){
           return;
        }
        ATOMIC_INTEGER.getAndDecrement();
    }

    public static void deleteOldLink(Long userId, String createTime){
        for (WebsocketClient client : WEB_SOCKET_SET) {
            if(client.getUserId().equals(userId) && client.getCreateTime().equals(createTime)){
                WEB_SOCKET_SET.remove(client);
                subOnlineCount();
            }
        }
    }

}
