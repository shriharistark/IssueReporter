package com.example.Springgaejdohello.Service.Utils;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.stereotype.Component;

@Component
public class ObjectifyWorker {

    private static Objectify ofy;

    public static Objectify getofy() {
        if(ofy == null) {
            ofy = ObjectifyService.ofy();
        }

        return ofy;
    }

}
