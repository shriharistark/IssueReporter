package com.example.Springgaejdohello.Service;

import com.example.Springgaejdohello.model.IssueModel;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class BatchWrite {

    public class BatchWriteException extends Exception{

        public BatchWriteException(String s){
            super(s);
        }
    }

    static List<Object> writeQueue;
    //max queue limit
    static int MAX_SIZE = 500;

    public BatchWrite(){
        if(writeQueue == null) {
            writeQueue = new ArrayList<>();
        }
    }

    //manual trigger to flush the queue writes to datastore
    public void flush() throws BatchWriteException{

        //clears the batch whenever called irrespective of size
        try{
            System.out.print(writeQueue);
            ObjectifyService.ofy().save().entities(writeQueue).now();
            writeQueue.clear();
        }catch (Exception e){
            throw new BatchWriteException(e.getMessage());
        }
    }


    public void addToQueue(Object entity, Class<?> classname) throws BatchWriteException {
        //whenever you try to add more elements than MAX, it does force push
        writeIfQueueFull();
        writeQueue.add(classname.cast(entity));
    }

    public boolean isQueueFull(){
        if(writeQueue.size() > MAX_SIZE){
            return true;
        }

        return false;
    }

    private boolean writeIfQueueFull() throws BatchWriteException {
        if(isQueueFull()){
            flush();
            return true;
        }

        else {
            System.out.print("Queue is not yet full.. "+writeQueue.size());
            return false;
        }
    }

}
