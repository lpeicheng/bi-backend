package com.yupao.manager;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket服务
 */
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    //存放会话对象
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session 会话
     * @param userId  会话id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessionMap.put(userId, session);
        System.out.println("连接成功");
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param userId  会话id
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        System.out.println("来自客户端的消息:" + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param session 客户端发送过来的消息
     * @param userId  会话id
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessionMap.remove(userId, session);
        System.out.println("连接关闭");
    }


    /**
     * 群发
     *
     * @param message 消息
     */
    public void sendToClient(String userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
