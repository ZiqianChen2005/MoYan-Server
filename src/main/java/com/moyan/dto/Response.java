package com.moyan.dto;

public class Response<T> {
    private int code;      // 0成功 1失败
    private String msg;    // 消息
    private T data;        // 数据

    public Response() {}

    public Response(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(0, "success", data);
    }

    public static <T> Response<T> success(String msg, T data) {
        return new Response<>(0, msg, data);
    }

    public static <T> Response<T> fail(String msg) {
        return new Response<>(1, msg, null);
    }

    public static <T> Response<T> fail(int code, String msg) {
        return new Response<>(code, msg, null);
    }

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}