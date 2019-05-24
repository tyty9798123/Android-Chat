package com.example.activityhomework;

import com.stfalcon.chatkit.commons.models.IMessage;

import java.util.Date;

public class Message implements IMessage {

    private String id, text;
    private Author author;
    private Date createdAt;
    public Message(String id, String text, Author author, Date createdAt){
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    public void setId(){
        this.id = id;
    }

    public void setText(){
        this.text = text;
    }

    public void setUser(){
        this.author = author;
    }

    public void setCreatedAt(){
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}