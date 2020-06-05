package com.example.messages;

public class ImposeRequest {
    int ballot;
    int proposal;

    public ImposeRequest(int _ballot, int _proposal) {
        ballot = _ballot;
        proposal = _proposal;
    }

    public int getBallot() {
        return ballot;
    }

    public int getProposal() {
        return proposal;
    }
}
