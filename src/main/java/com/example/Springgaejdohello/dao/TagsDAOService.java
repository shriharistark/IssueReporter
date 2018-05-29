package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.daoInterface.TagsDAO;
import com.example.Springgaejdohello.model.TagsModel;

import java.util.List;

public class TagsDAOService implements TagsDAO {

    @Override
    public List<String> getByPartialText(String partialTag) {

    }

    @Override
    public void setTag(String newtag) {

        for(int i = 0 ; i < newtag.length()-3 ; i++){
        }
    }
}
