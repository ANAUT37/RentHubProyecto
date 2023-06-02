package com.example.renthubpablo.resources;

import com.google.firebase.Timestamp;

import java.sql.Date;

public class MensajeUtil {
    String message,sender,receiver,type, fecha;
    com.google.firebase.Timestamp timestamp;
    boolean seen;

    public MensajeUtil() {
    }

    public MensajeUtil(String message, String sender, String receiver, boolean seen, String type, Date date) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.seen = seen;
        this.type = type;
        this.timestamp=new com.google.firebase.Timestamp(date);
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MensajeUtil{" +
                "message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", type='" + type + '\'' +
                ", fecha='" + fecha + '\'' +
                ", timestamp=" + timestamp +
                ", seen=" + seen +
                '}';
    }
}
