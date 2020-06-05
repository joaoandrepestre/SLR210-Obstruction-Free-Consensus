package com.example.messages;

public class AckResponse {
    int ballot;

    public AckResponse(int _ballot) {
        ballot = _ballot;
    }

    public int getBallot() {
        return ballot;
    }
}
