package com.moyan.server;

import com.google.gson.Gson;
import com.moyan.controller.RequestHandler;
import com.moyan.dto.Response;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws")
@Component
public class WebSocketServer {

    private static RequestHandler requestHandler;
    private static final Gson gson = new Gson();
    private static final CopyOnWriteArraySet<WebSocketServer> connections = new CopyOnWriteArraySet<>();

    private Session session;

    @Autowired
    public void setRequestHandler(RequestHandler handler) {
        requestHandler = handler;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connections.add(this);
        System.out.println("[WebSocket] 新连接: " + session.getId());
        System.out.println("[WebSocket] 当前连接数: " + connections.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            System.out.println("[WebSocket] 收到消息: " + message);

            String response = requestHandler.handle(message);

            System.out.println("[WebSocket] 发送响应: " + response);
            session.getBasicRemote().sendText(response);

        } catch (Exception e) {
            System.err.println("[WebSocket] 处理消息失败: " + e.getMessage());
            e.printStackTrace();

            try {
                String errorResponse = gson.toJson(Response.fail("服务器错误: " + e.getMessage()));
                session.getBasicRemote().sendText(errorResponse);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        connections.remove(this);
        System.out.println("[WebSocket] 连接关闭: " + session.getId());
        System.out.println("[WebSocket] 原因: " + closeReason);
        System.out.println("[WebSocket] 当前连接数: " + connections.size());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("[WebSocket] 发生错误: " + session.getId());
        throwable.printStackTrace();

        try {
            String errorResponse = gson.toJson(Response.fail("连接错误: " + throwable.getMessage()));
            session.getBasicRemote().sendText(errorResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
