package com.linhleeproject.mymessage.messengeros10.models;

/**
 * Created by Linh Lee on 11/28/2016.
 */
public class MessageObject {
    private int messageId;
    private int threadId;
    private String address;
    private String person;
    private long date;
    private String body;
    private int read;
    private int type;
    private String thumbnailBase64 = "";

    public MessageObject(int messageId, int threadId, String address, String person, long date, String body, int read, int type) {
        this.messageId = messageId;
        this.threadId = threadId;
        this.address = address;
        this.person = person;
        this.date = date;
        this.body = body;
        this.read = read;
        this.type = type;
    }

    public MessageObject(long date, String body, int read, int type) {
        this.date = date;
        this.body = body;
        this.read = read;
        this.type = type;
    }

    public MessageObject() {
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getThumbnailBase64() {
        return thumbnailBase64;
    }

    public void setThumbnailBase64(String thumbnailBase64) {
        this.thumbnailBase64 = thumbnailBase64;
    }

    public String toString() {
        return messageId + " " + threadId + " " + address + " " + person + " " + date + " " + body + " " + read;
    }
}
