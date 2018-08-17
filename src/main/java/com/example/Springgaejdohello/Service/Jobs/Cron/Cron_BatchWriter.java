package com.example.Springgaejdohello.Service.Jobs.Cron;

import com.example.Springgaejdohello.Service.BatchWrite;
import com.example.Springgaejdohello.model.IssueModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/crons")
public class Cron_BatchWriter {

    static BatchWrite batchWrite;

    BatchWrite getBatchWrite() {
        if(batchWrite == null){
            batchWrite = new BatchWrite();
        }
        return batchWrite;
    }

    @GetMapping("/batchwrite")
    public @ResponseBody String makeWrites(){

        Map<String,Object> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        //ObjectifyService.ofy().save().entity(newTicket).now();


        try {
            getBatchWrite().flush();
            response.put("ok",true);
            response.put("status","All entity in the batch is saved successfully");
        }catch (BatchWrite.BatchWriteException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            System.out.print("Writing erorr on the DB. BatchWrite ");
            e.printStackTrace();
        }

        String jsonResponse = "problem with jackson";
        //mapper.writeValueAsString(response);
        try {
            jsonResponse = mapper.writeValueAsString(response);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }
}
