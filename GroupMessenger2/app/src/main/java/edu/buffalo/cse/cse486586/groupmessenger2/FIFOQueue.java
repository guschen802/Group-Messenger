package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gus on 3/12/15.
 */
public class FIFOQueue {
    private List<Message> mQueue = new LinkedList<Message>();
    int mExpectedSeq = 1;
    int mSourcePort = 0;

    public FIFOQueue(int mSourcePort) {
        this.mSourcePort = mSourcePort;
    }

    public Message first(){
        if (mQueue.size() == 0){
            return null;
        }
        return mQueue.get(0);
    }

    public Message dequeue(){
        Message msg = mQueue.get(0);
        mQueue.remove(msg);
        return msg;
    }
    public void add(Message msg){
        mQueue.add(msg);
        Collections.sort(mQueue,new SeqComparator());
        Log.v("FIFO: ", String.valueOf(mSourcePort));
        for(Message ms : mQueue){
            Log.v("FIFO ", ms.toString());
        }

    }

}
