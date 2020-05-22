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

public class ImposeRequest{
    int ballot;
    int proposal;
    public ImposeRequest(int _ballot, int _proposal)
    {
	ballot = _ballot;
	proposal = _proposal;
    }
    int getBallot()
    {
	return ballot;
    }
    int getProposal()
    {
	return proposal;
    }
}
   
