package com.example.Springgaejdohello.Builders;

import com.example.Springgaejdohello.model.UserModel;

public class UserBuilder {

    UserModel user;

    private String user_email;
    private String user_name;
    private String user_team = "";
    private String user_type = "";
    private String user_refresh_token = "";
    private String user_picture = "";
    private String user_status = "";

    //user email & user name is mandatory!
    public UserBuilder(String user_email, String user_name){
        user = new UserModel();
        this.user_email = user_email;
        this.user_name = user_name;
    }

    public UserBuilder setUser_team(String user_team){
        this.user_team = user_team;
        return this;
    }

    public UserBuilder setUser_type(String user_type){
        this.user_type = user_type;
        return this;
    }

    public UserBuilder setRefresh_token(String refresh_token){
        this.user_refresh_token = refresh_token;
        return this;
    }

    public UserBuilder setUser_picture(String user_picture){
        this.user_picture = user_picture;
        return this;
    }

    public UserBuilder setUser_status(String user_status){

        if(user_status.toLowerCase().equals("active")
                || user_status.toLowerCase().equals("inactive")
                || user_status.toLowerCase().equals("unverified")){

            this.user_status = user_status.toLowerCase();
        }

        else {
            this.user_status = "inactive";
        }

        return this;
    }

    public UserModel build(){

        user.setUser_name(this.user_name);
        user.setUser_email(this.user_email);
        user.setUser_picture(this.user_picture);
        user.setUser_refresh_token(this.user_refresh_token);
        user.setUser_team(this.user_team);
        user.setUser_type(this.user_type);
        user.setUser_status(this.user_status);

        return user;
    }

    //getters for serialization
    public String getUser_email() {
        return user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_team() {
        return user_team;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getUser_picture() {
        return user_picture;
    }

    @Override
    public String toString() {
        return "user_email='" + user_email + '\'' +
                ", user_name='" + user_name + '\'' +
                ", user_team='" + user_team + '\'' +
                ", user_type='" + user_type + '\'' +
                ", user_picture='" + user_picture + '\'';
    }
}
