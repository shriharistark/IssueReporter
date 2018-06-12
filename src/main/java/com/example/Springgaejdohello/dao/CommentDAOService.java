package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.daoInterface.CommentDAO;
import com.example.Springgaejdohello.model.CommentModel;
import com.googlecode.objectify.Key;

import java.util.Date;

public class CommentDAOService implements CommentDAO {


    @Override
    public String addComment(String issueID, String parentID, String message,String author) {

        String commentID = String.valueOf(new Date().getTime());
        CommentModel comment = new CommentModel();
        comment.setId(commentID);
        comment.setParentID(parentID);
        comment.setIssueID(issueID);
        comment.setMessage(message);


        String result = "";
        try{
            ObjectifyWorker.getofy().save().entity(comment);
            result = "success";
        }catch (Exception e){
            result = "fail";
            System.out.println(e.toString());
        }

        return result.equals("success")?commentID : "failed";

    }



    @Override
    public String addComment(String issueID, String message,String author) {
        String commentID = String.valueOf(new Date().getTime());
        CommentModel comment = new CommentModel();
        comment.setId(commentID);
        comment.setIssueID(issueID);
        comment.setMessage(message);

        String result = "";
        try{
            ObjectifyWorker.getofy().save().entity(comment);
            result = "success";
        }catch (Exception e){
            result = "fail";
            System.out.println(e.toString());
        }

        return result.equals("success")?commentID : "failed";
    }

    @Override
    public boolean removeComment(String issueID, String commentID, String parentID) {

        Key<CommentModel> comment = Key.valueOf(commentID);
        CommentModel commentEntity;

        String result = "";
        try{
            commentEntity = ObjectifyWorker.getofy().load().key(comment).now();
            ObjectifyWorker.getofy().delete().entity(commentEntity);
            result = "success";
            return true;
        }catch (Exception e){
            result = "fail";
            System.out.println(e.toString());
            return false;
        }

    }

    @Override
    public String editComment(String commentID,String newmessage) {

        Key<CommentModel> commentkey = Key.valueOf(commentID);
        CommentModel commententity;

        try{
            commententity = ObjectifyWorker.getofy().load().key(commentkey).now();
            commententity.setMessage(newmessage);
            return "success";
        }catch (Exception e){
            System.out.println(e.toString());
            return "failed";
        }
    }

}
