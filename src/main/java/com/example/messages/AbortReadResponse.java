package com.example.messages;

public class AbortReadResponse {
    long ballot;

    public AbortReadResponse(long _ballot) {
        ballot = _ballot;
    }

    public long getBallot() { // needed to distinguish abort from other older requests
        return ballot;
    }
}
