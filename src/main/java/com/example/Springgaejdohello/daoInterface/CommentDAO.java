package com.example.Springgaejdohello.daoInterface;

public interface CommentDAO {

    public String addComment(String issueID,String message,String parentID,String author);//returns commentID
    public String addComment(String issueID,String message,String author);//returns commentID

    public boolean removeComment(String issueID,String commentID,String parentID);

    public String editComment(String commentID,String newmessage);


}
