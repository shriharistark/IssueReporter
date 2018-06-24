package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.model.CommentModel;

public class CommentBuilder extends CommentModel {

//    private String id;
//    private Key<IssueModel> issueID;
//    private Key parentID;
//    private String message;
//    private String author;

//    CommentBuilder buildComment(String message,String author){
//        this.message = message;
//        this.author = author;
//        this.id = String.valueOf(new Date().getTime());
//        return this;
//    }

    private CommentModel commentModel;

    public CommentBuilder(String message, String author){
        commentModel = new CommentModel();
        commentModel.setMessage(message);
        commentModel.setAuthor(author);
        commentModel.setId();// sets timestamp as the id
    }

    public CommentBuilder hasParent(Boolean isparent){
        commentModel.setParent(isparent);
        System.out.println("has parents test? "+commentModel.hasParent());
        return this;
    }

    public CommentBuilder addParent(String parentId){

        //set parent ID only if it is a parent
        System.out.println("has add parents test? "+commentModel.hasParent());
        if(commentModel.hasParent()) {
            commentModel.setParentID(parentId);
        }
        return this;
    }

    public CommentBuilder bindTo(String issueId){
        commentModel.setIssueKey(issueId);
        commentModel.setIssueID(issueId);
        return this;
    }

    public CommentModel build(){
        return commentModel;
    }

}
