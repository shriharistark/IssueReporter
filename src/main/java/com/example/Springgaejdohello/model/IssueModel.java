package com.example.Springgaejdohello.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.PojoIf;
import com.googlecode.objectify.condition.ValueIf;

@Entity
public class IssueModel {

    static class IfStatus extends ValueIf<String> {

        @Override
        public boolean matchesValue(String issue) {
            return issue.toLowerCase().equals("open");
        }
    }
	
	@Id Long code;
    @Index String subject;
    @Index String assignee;
    @Index String assignedto;
    @Index List<String> tags;
    @Index (IfStatus.class) String status;
    @Index String description;
    @Index Integer downvotes;
    List<String> downvoters;
    @Index Long lastDateModified;
    @Index int numberOfComments;

    public IssueModel(){

    }

//    public IssueModel(String subject, String assignee, String assignedto, List<String> tags, String status, String description,Integer downvotes) {
//        this.code = new Date().getTime();
//        this.subject = subject;
//        this.assignee = assignee;
//        this.assignedto = assignedto;
//        this.tags = tags;
//        this.status = status;
//        this.description = description;
//        this.downvotes = downvotes;
//        this.downvoters = new ArrayList<>();
//    }

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

    public void setStatus(String statusin) {
        this.status = statusin;
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

    public Integer getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(Integer downvotes) {
        this.downvotes = downvotes;
    }

    public List<String> getDownvoters(){
        return this.downvoters;
    }

    public void setDownvoters(String name){
        this.downvoters = this.downvoters == null ? new ArrayList<>() : this.downvoters;
        this.downvoters.add(name);
    }

    public Long getLastDateModified() {
        return lastDateModified;
    }

    public void setLastDateModified(Long lastDateModified) {
        this.lastDateModified = lastDateModified;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }
}
