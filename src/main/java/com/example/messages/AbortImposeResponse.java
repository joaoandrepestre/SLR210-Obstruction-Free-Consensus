package com.example.messages;

public class AbortImposeResponse {
    long ballot;

    public AbortImposeResponse(long _ballot) {
        ballot = _ballot;
    }

    public long getBallot() { // needed to distinguish abort from other older requests
        return ballot;
    }
}
