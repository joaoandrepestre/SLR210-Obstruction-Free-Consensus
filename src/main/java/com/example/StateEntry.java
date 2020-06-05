package com.example;

public class StateEntry {
    int estimate;
    int imposeballot;

    public StateEntry(int _estimate, int _imposeballot) {
        estimate = _estimate;
        imposeballot = _imposeballot;
    }

    public int getEstimate() {
        return estimate;
    }

    public int getImposeBallot() {
        return imposeballot;
    }
}
