package com.example.Springgaejdohello.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class IssueModel {
	
	@Id Long code;
    @Index String subject;
    @Index String assignee;
    @Index String assignedto;
    @Index List<String> tags;
    @Index String status;
    @Index String description;

    public IssueModel(){

    }

    public IssueModel(String subject, String assignee, String assignedto, List<String> tags, String status, String description) {
        this.code = new Date().getTime();
        this.subject = subject;
        this.assignee = assignee;
        this.assignedto = assignedto;
        this.tags = tags;
        this.status = status;
        this.description = description;

    }

    public Long getCode() {
        return code;
    }

    public void setCode() {
        this.code = new Date().getTime();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssignedto() {
        return assignedto;
    }

    public void setAssignedto(String assignedto) {
        this.assignedto = assignedto;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebSafeKey(){
        return Key.create(IssueModel.class,this.code).toWebSafeString();
    }

}
