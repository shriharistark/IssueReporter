package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.daoInterface.CommentDAO;
import com.example.Springgaejdohello.model.CommentModel;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentDAOService implements CommentDAO {


    @Override
    public String addComment(CommentModel comment) {


        String result = "";
        try{
            ObjectifyService.ofy().save().entity((CommentModel)comment).now();
            result = "success";
        }catch (Exception e){
            result = "fail";
            System.out.println(e.toString());
        }

        return result.equals("success")?comment.getId() : "failed";

    }

    @Override
    public boolean removeComment(String issueID, String commentID, String parentID) {

        Key<CommentModel> comment = Key.valueOf(commentID);
        CommentModel commentEntity;

        String result = "";
        try{
            commentEntity = ObjectifyService.ofy().load().key(comment).now();
            ObjectifyService.ofy().delete().entity(commentEntity);
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
            commententity = ObjectifyService.ofy().load().key(commentkey).now();
            commententity.setMessage(newmessage);
            return "success";
        }catch (Exception e){
            System.out.println(e.toString());
            return "failed";
        }
    }

    @Override
    public QueryResultIterator<CommentModel> getAllCommentsOf(String issueId,String cursorStr) {


        Query<CommentModel> query = ObjectifyService.ofy().load().type(CommentModel.class)
               .filter("issueID",issueId).limit(3);
        if(!cursorStr.isEmpty()) {
            query = query.startAt(Cursor.fromWebSafeString(cursorStr));
        }

        boolean continu = false;
        QueryResultIterator<CommentModel> queryres = query.iterator();

        List<CommentModel> resultSet = new ArrayList<CommentModel>();
        while(queryres.hasNext()) {
            resultSet.add(queryres.next());
            continu = true;
        }

        return query.iterator();
    }

    public List<CommentModel> getReplies(String issueId, String parentComment){

        List<CommentModel> replies = ObjectifyService.ofy().load().type(CommentModel.class)
                .filter("parentID",parentComment).list();

        return replies;
    }
}
