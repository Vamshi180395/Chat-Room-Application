package com.example.inclass11;

import java.util.ArrayList;

/**
 * Created by Rama Vamshi Krishna on 11/14/2016.
 */
public class Messages {
    String msgkey,message,userid,imageurl,userfullname;
    ArrayList<Comment> comments=new ArrayList<Comment>();

    public Messages(String msgkey, String message, String userid, String imageurl,String userfullname,ArrayList<Comment> comments) {
        this.msgkey = msgkey;
        this.message = message;
        this.userid = userid;
        this.imageurl = imageurl;
        this.userfullname=userfullname;
        this.comments=comments;
    }

    public Messages(){

    }

    @Override
    public String toString() {
        return "Messages{" +
                "msgkey='" + msgkey + '\'' +
                ", message='" + message + '\'' +
                ", userid='" + userid + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}
