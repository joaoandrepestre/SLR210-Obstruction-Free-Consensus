package com.example.messages;

public class AbortImposeResponse {
    int ballot;

    public AbortImposeResponse(int _ballot) {
        ballot = _ballot;
    }

    public int getBallot() { // needed to distinguish abort from other older requests
        return ballot;
    }
}
