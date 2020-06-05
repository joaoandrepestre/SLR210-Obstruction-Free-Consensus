package com.example;

public class StateEntry {
    int estimate;
    long imposeballot;

    public StateEntry(int _estimate, long _imposeballot) {
        estimate = _estimate;
        imposeballot = _imposeballot;
    }

    public int getEstimate() {
        return estimate;
    }

    public long getImposeBallot() {
        return imposeballot;
    }
}
