package com.moyan.entity;

public class Response {
    private int code;      // 0成功 1失败
    private String msg;    // 消息
    private Object data;   // 数据
    
    public Response() {}
    
    public Response(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    public static Response success(Object data) {
        return new Response(0, "success", data);
    }
    
    public static Response fail(String msg) {
        return new Response(1, msg, null);
    }
    
    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}