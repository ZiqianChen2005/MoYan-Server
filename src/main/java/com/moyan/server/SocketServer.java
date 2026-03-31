package com.moyan.server;

import com.moyan.controller.RequestHandler;
import java.io.*;
import java.net.*;

public class SocketServer {
    private static final int PORT = 8888;
    private static RequestHandler handler = new RequestHandler();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器启动，监听端口: " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("新客户端连接: " + clientSocket.getInetAddress());
                
                // 为每个客户端创建独立线程
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
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
                String request;
                while ((request = reader.readLine()) != null) {
                    System.out.println("收到请求: " + request);
                    String response = handler.handle(request);
                    System.out.println("返回响应: " + response);
                    writer.println(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}