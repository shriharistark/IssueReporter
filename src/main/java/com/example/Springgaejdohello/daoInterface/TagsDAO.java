package com.example.Springgaejdohello.daoInterface;


import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.util.List;

public interface TagsDAO {

    List<String> getByPartialText(String partialTag);
    void setTag(String newtag);
}
