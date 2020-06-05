package com.example.messages;

public class ReadRequest {
    long ballot;

    public ReadRequest(long _ballot) {
        ballot = _ballot;
    }

    public long getBallot() {
        return ballot;
    }
}
