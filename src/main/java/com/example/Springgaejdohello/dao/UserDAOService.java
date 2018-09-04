package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.Service.Utils.ObjectifyWorker;
import com.example.Springgaejdohello.daoInterface.UserDAO;
import com.example.Springgaejdohello.model.UserModel;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Component;


@Component
public class UserDAOService implements UserDAO {

    @Override
    public Key<UserModel> createUser(UserModel user) {

        /*User email already exists - return null
          User email
         */

        Key<UserModel> newuser_key = null;
        try {
            if(readUser(user.getUser_email().toLowerCase()) == null) {
                newuser_key = ObjectifyWorker.getofy().save().entity(user).now();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return newuser_key;
    }

    @Override
    public UserModel readUser(String user_email) {

        UserModel user = null;
        try {
            user = ObjectifyWorker.getofy().load().type(UserModel.class).id(user_email.toLowerCase()).now();
        }catch (Exception e){
            e.getMessage();
        }

        return user;
    }

    @Override
    public boolean updateUser(String user_email, UserModel updated_user) {

        if(!updated_user.getUser_email().isEmpty()
                && !updated_user.getUser_name().isEmpty()){

            Key<UserModel> newuser_key = ObjectifyWorker.getofy().save().entity(updated_user).now();
            return (newuser_key != null);
        }

        return false;
    }

    @Override
    public boolean deleteUser(String user_email) {

        UserModel user = readUser(user_email.toLowerCase());
        if(user != null){
            user.setUser_status("inactive");
            ObjectifyWorker.getofy().save().entity(user).now();
            return true;
        }

        return false;

    }

    @Override
    public boolean isExistingUser(String user_email) {

        //new user => readuser returns null || null != null false => isExistinguser = false

        return readUser(user_email) != null;
    }
}
