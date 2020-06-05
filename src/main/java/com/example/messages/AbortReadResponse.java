package com.example.messages;

public class AbortReadResponse {
    int ballot;

    public AbortReadResponse(int _ballot) {
        ballot = _ballot;
    }

    public int getBallot() { // needed to distinguish abort from other older requests
        return ballot;
    }
}
