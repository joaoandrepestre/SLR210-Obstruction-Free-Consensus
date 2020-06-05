package com.example.messages;

public class GatherResponse {
    long ballot;
    long imposeballot;
    int estimate;
    int senderID;

    public GatherResponse(long _ballot, long _imposeballot, int _estimate, int _senderID) {
        ballot = _ballot;
        imposeballot = _imposeballot;
        estimate = _estimate;
        senderID = _senderID;
    }

    public int getSenderID() {
        return senderID;
    }

    public long getBallot() {
        return ballot;
    }

    public int getEstimate() {
        return estimate;
    }

    public long getImposeBallot() {
        return imposeballot;
    }
}
