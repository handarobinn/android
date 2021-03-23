package com.Test;

public class Message {
    public long msg_id;
    public boolean isSend;
    public String message;

    public Message() {
    }

    public Message(long msg_id, boolean isSend, String message) {
        this.msg_id = msg_id;
        this.isSend = isSend;
        this.message = message;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}