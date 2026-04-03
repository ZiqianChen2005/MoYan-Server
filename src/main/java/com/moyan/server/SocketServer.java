package com.moyan.server;

import com.moyan.controller.RequestHandler;
import java.io.*;
import java.net.*;

public class SocketServer {
    private static final int PORT = 8888;
    private static RequestHandler handler = new RequestHandler();
    
    public static void main(String[] args) {
        System.out.println("========== 陌言服务端启动 ==========");
        System.out.println("监听端口: " + PORT);
        System.out.println("===================================");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[连接] 新客户端: " + clientSocket.getInetAddress().getHostAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    static class ClientHandler extends Thread {
        private Socket socket;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
                PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)
            ) {
                String requestLine;
                StringBuilder requestBuilder = new StringBuilder();
                
                // 读取请求（以空行结束）
                while ((requestLine = reader.readLine()) != null) {
                    if (requestLine.isEmpty()) {
                        break;
                    }
                    requestBuilder.append(requestLine);
                }
                
                String requestJson = requestBuilder.toString();
                if (!requestJson.isEmpty()) {
                    System.out.println("[请求] " + requestJson);
                    String responseJson = handler.handle(requestJson);
                    System.out.println("[响应] " + responseJson);
                    writer.println(responseJson);
                }
                
            } catch (IOException e) {
                System.err.println("[错误] " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("[断开] 客户端断开连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}