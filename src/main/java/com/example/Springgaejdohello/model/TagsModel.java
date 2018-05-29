package com.example.Springgaejdohello.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

@Entity
public class TagsModel {

    @Index
    String tag;
    @Index
    ArrayList<String> tagSubs;

    public TagsModel(String tag) {
        this.tag = tag;
    }

    public void setTagSubs(String tag){
        for(int i = 0 ; i <= tag.length() ; i++){
            tagSubs.add(tag.substring(i,i+3));
        }
    }
}
