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

public class LaunchRequest{
    boolean faulty;
    public LaunchRequest(boolean _faulty)
	{
	    faulty = _faulty;
	}
    boolean getFaulty()
	{
	    return faulty;
	}
    
}
