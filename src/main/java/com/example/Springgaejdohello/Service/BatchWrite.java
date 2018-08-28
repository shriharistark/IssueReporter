package com.example.Springgaejdohello.Service;

import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.*;
import org.joda.time.chrono.StrictChronology;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            Map<Key<Object>,Object> results = ObjectifyService.ofy().save().entities(writeQueue).now();
            writeQueue.clear();
        }catch (Exception e){
            throw new BatchWriteException(e.getMessage());
        }
    }


    public void addToQueue(Object entity, Class<?> classname) throws BatchWriteException {
        //whenever you try to add more elements than MAX, it does force push
        writeIfQueueFull();

        writeQueue.add(classname.cast(entity));
        Queue batchPushTaskQueue = QueueFactory.getQueue("batch-write");
        TaskHandle batch_writes = batchPushTaskQueue.add(TaskOptions.Builder.withUrl("/crons/batchwrite").method(TaskOptions.Method.GET));

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
