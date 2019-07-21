package ru.sky.myTransfer.divider;

import java.io.Serializable;

public class Message implements Serializable {
    private final MessageType type;
    private final String data;

    public Message(MessageType type) {
        this.type = type;
        data = null;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }

//    public void setData(String data) {
//        this.data = data;
//    }
}