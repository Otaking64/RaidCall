package com.example.alex.raidcall;

/**
 * Created by Alex on 7/3/2017.
 */

public class Chat {

    private String Sender;
    private String Message;
    private String Date;

    public Chat(){

    }

    public Chat(String sender, String message, String date) {
        Sender = sender;
        Message = message;
        Date = date;
    }



    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {Date = date;}


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


}
