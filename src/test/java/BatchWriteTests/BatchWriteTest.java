package BatchWriteTests;

import com.example.Springgaejdohello.Service.BatchWrite;
import com.example.Springgaejdohello.model.IssueModel;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BatchWriteTest {

//    public boolean atleastOneEqual(List<IssueModel> list,String in){
//        for(IssueModel n : list){
//            if(n.getSubject().equals(in))
//                return true;
//        }
//
//        return false;
//    }
//
//    @Test
//    public void simpleBatchWriteTest() {
//        List<IssueModel> issues = new ArrayList<>();
//        BatchWrite batch = new BatchWrite();
//        for (int i = 0; i < 7; i++) {
//            IssueModel issue = new IssueModel();
//            issue.setCode();
//            issue.setSubject("sub: " + i);
//            issues.add(issue);
//        }
//        for (IssueModel l : issues) {
//            batch.addToQueue(l, IssueModel.class);
//        }
//
//
//        Query<IssueModel> query = ObjectifyWorker.getofy().load().type(IssueModel.class).order("date").limit(6);
//        Assert.assertTrue("Match",atleastOneEqual(query.list(),"sub: 1"));
//    }
}
