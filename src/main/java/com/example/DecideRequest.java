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

public class DecideRequest{
    int proposal;
    public DecideRequest(int _proposal)
    {
	proposal = _proposal;
    }
    int getProposal()
    {
	return proposal;
    }
}
		       
