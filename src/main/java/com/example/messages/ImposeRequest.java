package com.example.messages;

public class ImposeRequest {
    long ballot;
    int proposal;

    public ImposeRequest(long _ballot, int _proposal) {
        ballot = _ballot;
        proposal = _proposal;
    }

    public long getBallot() {
        return ballot;
    }

    public int getProposal() {
        return proposal;
    }
}
