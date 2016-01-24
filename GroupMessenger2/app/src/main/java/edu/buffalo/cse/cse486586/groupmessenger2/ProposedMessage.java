package edu.buffalo.cse.cse486586.groupmessenger2;

/**
 * Created by Gus on 3/13/15.
 */
public class ProposedMessage {
    int sourcePort;
    int seq;
    double priority;

    public ProposedMessage(int sourcePort, int seq, double priority) {
        this.sourcePort = sourcePort;
        this.seq = seq;
        this.priority = priority;
    }
}
