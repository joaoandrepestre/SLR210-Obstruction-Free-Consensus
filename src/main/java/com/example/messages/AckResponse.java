package com.example.messages;

public class AckResponse {
    long ballot;

    public AckResponse(long _ballot) {
        ballot = _ballot;
    }

    public long getBallot() {
        return ballot;
    }
}
