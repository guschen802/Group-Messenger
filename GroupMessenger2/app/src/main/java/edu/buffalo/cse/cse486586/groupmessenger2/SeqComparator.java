package edu.buffalo.cse.cse486586.groupmessenger2;

import java.util.Comparator;

/**
 * Created by Gus on 3/12/15.
 */
public class SeqComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {
        if (lhs.mSourceSeq > rhs.mSourceSeq){
            return 1;
        }else if (lhs.mSourceSeq < rhs.mSourceSeq){
            return -1;
        }else{
            return 0;
        }
    }
}
