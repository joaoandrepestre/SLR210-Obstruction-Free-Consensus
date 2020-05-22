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

public class GatherResponse{
    int ballot;
    int imposeballot;
    int estimate;
    int senderID;
    public GatherResponse(int _ballot, int _imposeballot, int _estimate, int _senderID)
    {
	ballot = _ballot;
	imposeballot = _imposeballot;
	estimate = _estimate;
	senderID = _senderID;
    }
    int getSenderID()
    {
	return senderID;
    }
	
    int getBallot()
    {
	return ballot;
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
