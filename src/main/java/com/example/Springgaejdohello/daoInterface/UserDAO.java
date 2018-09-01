package com.example.Springgaejdohello.daoInterface;

import com.example.Springgaejdohello.model.UserModel;

public interface UserDAO {

    String createUser();
    UserModel readUser();
    boolean updateUser();
    boolean deleteUser();
    boolean existingUser();
}
