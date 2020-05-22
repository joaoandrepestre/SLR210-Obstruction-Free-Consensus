package com.example;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateEntry{
    int estimate;
    int imposeballot;
    public StateEntry(int _estimate, int _imposeballot)
    {
	estimate = _estimate;
	imposeballot = _imposeballot;
    }
    int getEstimate()
    {
	return estimate;
    }
    int getImposeBallot()
    {
	return imposeballot;
    }
}
      
