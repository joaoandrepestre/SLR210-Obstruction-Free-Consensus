package com.example.messages;

public class ReadRequest {
    int ballot;

    public ReadRequest(int _ballot) {
        ballot = _ballot;
    }

    public int getBallot() {
        return ballot;
    }
}
