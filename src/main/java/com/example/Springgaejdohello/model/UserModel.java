package com.example.Springgaejdohello.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserModel {

    @Id String user_email;
    @Index String user_name;
    @Index String user_refresh_token;
    @Index String user_picture;
    @Index String user_team;

    public UserModel(){

    }

}
