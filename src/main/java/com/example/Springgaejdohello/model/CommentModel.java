package com.example.Springgaejdohello.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

@Entity
public class CommentModel {

    @Id
    String id;

    @Index Key<IssueModel> issueKey;
    @Index Key parentID;
    String message;
    String author;
    @Index Boolean hasParent;
    @Index String issueID;
//
//    //without parentID
//    public CommentModel(String id, String issueID, String message, String author) {
//        this.id = id;
//        this.issueID = Key.valueOf(issueID);
//        this.message = message;
//        this.author = author;
//    }
//
//    //with parentID
//    public CommentModel(String id, String issueID, String parentID, String message,String author) {
//        this.id = id;
//        this.issueID = Key.valueOf(issueID);
//        this.parentID = Key.valueOf(parentID);
//        this.message = message;
//        this.author = author;
//    }

    public CommentModel() {
    }

    public String getId() {
        return id;
    }

    public void setId() {
        this.id = String.valueOf(new Date().getTime());
    }

    public String getWebSafeIssueKey() {
        return issueKey.toWebSafeString();
    }

    public void setIssueKey(String issueIDe) {

        this.issueKey = Key.create(IssueModel.class,issueIDe);
    }

    public String getIssueWebSafeKey(){
        return this.issueKey.toWebSafeString();
    }

    public String getCommentWebSafeKey(){
        return Key.create(CommentModel.class,this.id).toWebSafeString();
    }

    public String getParentID() {
        if(parentID != null) {
            return parentID.toWebSafeString();
        }
        else{
            return "";
        }
    }

    public void setParentID(String parentID) {
        this.parentID = Key.valueOf(parentID);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean hasParent() {
        return hasParent;
    }

    public void setParent(Boolean hasParent) {
        this.hasParent = hasParent;
    }


    //issue id newly added

    public String getIssueID() {
        return issueID;
    }

    public void setIssueID(String issueID) {
        this.issueID = issueID;
    }
}
