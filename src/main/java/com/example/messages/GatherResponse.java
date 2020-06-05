package com.example.messages;

public class GatherResponse {
    int ballot;
    int imposeballot;
    int estimate;
    int senderID;

    public GatherResponse(int _ballot, int _imposeballot, int _estimate, int _senderID) {
        ballot = _ballot;
        imposeballot = _imposeballot;
        estimate = _estimate;
        senderID = _senderID;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getBallot() {
        return ballot;
    }

    public int getEstimate() {
        return estimate;
    }

    public int getImposeBallot() {
        return imposeballot;
    }
}
