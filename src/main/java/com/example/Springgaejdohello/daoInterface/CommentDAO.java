package com.example.Springgaejdohello.daoInterface;

public interface CommentDAO {

    public String addComment(String issueID,String message,String parentID);//returns commentID
    public String addComment(String issueID,String message);//returns commentID

    public boolean removeComment(String issueID,String commentID,String parentID);

    public String editComment(String issueID, String commentID,String parentID,String newmessage);
    public String editComment(String issueID, String commentID,String newmessage);


}
