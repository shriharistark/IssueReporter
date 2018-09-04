package com.example.Springgaejdohello.Builders;

import com.example.Springgaejdohello.model.UserModel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class UserObjectSerializer extends StdSerializer<UserModel> {


    public UserObjectSerializer(){
        this(null);
    }

    public UserObjectSerializer(Class<UserModel> userClass){
        super(userClass);
    }

    @Override
    public void serialize(UserModel user, JsonGenerator gen, SerializerProvider serializer) throws IOException {

        gen.writeStartObject();
        gen.writeStringField("user_name",user.getUser_name());
        gen.writeStringField("user_email",user.getUser_email());
        gen.writeStringField("user_picture",user.getUser_picture());
        gen.writeStringField("user_team",user.getUser_team());
        gen.writeStringField("user_type",user.getUser_type());
        gen.writeEndObject();

    }
}
