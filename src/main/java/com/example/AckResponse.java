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

public class AckResponse{
    int ballot;
    public AckResponse(int _ballot)
    {
	ballot = _ballot;
    }
    int getBallot()
    {
	return ballot;
    }
}
