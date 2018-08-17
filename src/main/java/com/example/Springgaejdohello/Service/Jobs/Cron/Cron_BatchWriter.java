package com.example.Springgaejdohello.Service.Jobs.Cron;

import com.example.Springgaejdohello.Service.BatchWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crons")
public class Cron_BatchWriter {

    BatchWrite batchWrite;

    BatchWrite getBatchWrite() {
        if(batchWrite == null){
            return new BatchWrite();
        }
        return batchWrite;
    }

    @GetMapping("/batchwrite")
    public void makeWrites(){
        try {
            batchWrite.flush();
        } catch (BatchWrite.BatchWriteException e) {
            e.printStackTrace();
        }
    }
}
