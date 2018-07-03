package com.example.Springgaejdohello.daoInterface;


import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.util.List;

public interface TagsDAO {

    String createTag(String newtag);
    List<String> getAllTags();
}
