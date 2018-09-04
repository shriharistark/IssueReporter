package com.example.Springgaejdohello.daoInterface;

import com.example.Springgaejdohello.Builders.UserBuilder;
import com.example.Springgaejdohello.model.UserModel;
import com.googlecode.objectify.Key;

public interface UserDAO {

    Key<UserModel> createUser(UserModel userModel);
    UserModel readUser(String user_email);
    boolean updateUser(String user_email,UserModel userModel);
    boolean deleteUser(String user_email);
    boolean isExistingUser(String user_email);
}
