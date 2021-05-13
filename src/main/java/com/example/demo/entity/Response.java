package com.example.demo.entity;

/**
 * @author ly
 * @since 2021/5/13
 */

/**
 * 用来返回接口调用信息描述
 */

public class Response {
    String msg;
    int code;
    Boolean isTure = true;

    public Response() {
    }

    public Response(String msg, int code, Boolean isTure) {
        this.msg = msg;
        this.code = code;
        this.isTure = isTure;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Boolean getTure() {
        return isTure;
    }

    public void setTure(Boolean ture) {
        isTure = ture;
    }
}