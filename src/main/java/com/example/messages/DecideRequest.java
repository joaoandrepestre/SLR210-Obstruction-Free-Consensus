package com.example.messages;

public class DecideRequest {
    int proposal;

    public DecideRequest(int _proposal) {
        proposal = _proposal;
    }

    public int getProposal() {
        return proposal;
    }
}
