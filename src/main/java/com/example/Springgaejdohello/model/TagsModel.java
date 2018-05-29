package com.example.Springgaejdohello.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;

@Entity
public class TagsModel {

    @Id
    String tag;
    @Index
    ArrayList<String> tagSubs;

    public TagsModel(){

    }

    public TagsModel(String tag) {
        this.tag = tag;
        tagSubs = new ArrayList<>();
    }

    public void setTagSubs(){
        for(int i = 3 ; i < this.tag.length() ; i++){
            System.out.println(this.tag.substring(0,i));
            this.tagSubs.add(this.tag.substring(0,i));
        }
    }

    public String getTag(){
        return this.tag;
    }

    public ArrayList<String> getTagSubs(){
        return this.tagSubs;
    }
}
