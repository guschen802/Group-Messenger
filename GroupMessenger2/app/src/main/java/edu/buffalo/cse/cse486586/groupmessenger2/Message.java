package edu.buffalo.cse.cse486586.groupmessenger2;

/**
 * Created by Gus on 3/8/15.
 */
public class Message implements Comparable{
    static int nextPriority = 1;
    static final int REQUEST_PRIORITY =0;
    static final int PROPOSE_PRIORITY =1;
    static final int FINAL_PRIORITY =2;
    int mSource = 0;
    int mSourceSeq = 0;
    double mPriority = -1;
    int mProposedCount = GroupMessengerActivity.REMOTE_NUM;
    double mMaxProposedPriority = 0;
    boolean mDeliverable = false;
    String msg = "";

    public Message(int mSource, int mSourceSeq, String msg) {
        this.mSource = mSource;
        this.mSourceSeq = mSourceSeq;
        this.mMaxProposedPriority = this.mPriority;
        this.msg = msg;
    }

    @Override
    public int compareTo(Object another) {
        Message value2 = (Message) another;
        if ((mPriority) > (value2.mPriority)){
            return 1;
        }else if ((mPriority) < (value2.mPriority)){
            return -1;
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        String m = "";
        m += "id: " + String.valueOf(mSource) + " seq: " + String.valueOf(mSourceSeq) +
                " mPriority" + String.valueOf(mPriority) + " msg: " + msg +
                " deliverable: " + String.valueOf(mDeliverable) + "\n";
        return m;
    }
}
