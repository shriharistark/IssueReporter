package com.example.Springgaejdohello.daoInterface;

import com.example.Springgaejdohello.dao.CommentBuilder;
import com.example.Springgaejdohello.model.CommentModel;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.util.List;

public interface CommentDAO {

    public String addComment(CommentModel comment);//returns commentID

    public boolean removeComment(String issueID,String commentID,String parentID);

    public String editComment(String commentID,String newmessage);

    QueryResultIterator<CommentModel> getAllCommentsOf(String issueID,String cursor);

}
