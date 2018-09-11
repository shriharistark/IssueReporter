package com.example.Springgaejdohello.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserModel {

    @Id String user_email;
    @Index String user_name;
    @Index String user_team;
    @Index String user_type;
    String user_refresh_token;
    String user_picture;
    @Index String user_status;
    String user_password; //of course it is SHA 256 encrypted with salt!

    public UserModel(){

    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_team() {
        return user_team;
    }

    public void setUser_team(String user_team) {
        this.user_team = user_team;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_refresh_token() {
        return user_refresh_token;
    }

    public void setUser_refresh_token(String user_refresh_token) {
        this.user_refresh_token = user_refresh_token;
    }

    public String getUser_picture() {
        return user_picture;
    }

    public void setUser_picture(String user_picture) {
        this.user_picture = user_picture;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }
}
