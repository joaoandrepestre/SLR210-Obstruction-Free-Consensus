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
import java.lang.Math; 


public class Process extends UntypedAbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);// Logger attached to actor
    private final int N;//number of processes
    private final int id;//id of current process
    private Members processes;//other processes' references
    private boolean faulty;
    
    // -- OFCons variables --
    private int ballot, proposal, estimate, readballot, imposeballot;
    private ArrayList<StateEntry> states;

    private int gathercount, ackcount;
    private boolean reading, imposing;

    public Process(int ID, int nb) {
        N = nb;
        id = ID;
	estimate = -1;
	ballot = id - N;
	proposal = -1;
	readballot = 0;
	imposeballot = 0;
	reading = false;
	imposing = false;
	gathercount = 0;
	ackcount = 0;
	faulty = false;
	states = new ArrayList<StateEntry> (N);

	int i;
	for (i = 0; i < N; i++)
	{
	    states.add(new StateEntry(-1, 0));
	}
    }
    
    public String toString() {
        return "Process{" + "id=" + id ;
    }

    /**
     * Static function creating actor
     */
    public static Props createActor(int ID, int nb) {
        return Props.create(Process.class, () -> {
            return new Process(ID, nb);
        });
    }

    void propose()
    {
	proposal = ((int)(Math.random() * 1000) ) % 2;
	ballot = ballot + N;
	int i;
	for (i = 0; i < N; i++)
	{
	    states.set(i, new StateEntry(-1, 0));
	}
	reading = true;
	gathercount = 0;
	for(ActorRef actor: processes.references)
	{
	    //    log.info("p" + self().path().name() + " sent ReadMsg to " + "p" + actor.path().name());
	    actor.tell(new ReadRequest(ballot), self());
	}        
    }
	  
    
    public void onReceive(Object message) throws Throwable {
    	
	if (message instanceof Members) {//save the system's info
	    Members m = (Members) message;
	    processes = m;
	    log.info("p" + self().path().name() + " received processes info");
	}
	if (message instanceof OfconsProposerMsg) {
	    OfconsProposerMsg opm = (OfconsProposerMsg) message;
	    log.info("p" + self().path().name() + " received OfconsProposerMsg: " + opm.message);
	    ReadMsg rm = new ReadMsg("test read");
	    for(ActorRef actor: processes.references) {
		log.info("p" + self().path().name() + " sent ReadMsg to " + "p" + actor.path().name());
		actor.tell(rm, self());
	    }
          }

	/* --- Synoid OFCons --- */

	else if (message instanceof Launch)
	{
	    LaunchRequest launch = (LaunchRequest) message;
	    faulty = launch.getFaulty();
	    propose();
	}
	
	else if (message instanceof AbortReadResponse)
	{
	    AbortReadResponse abort = (AbortReadResponse) message;
	    if (reading == true && abort.getBallot() == ballot)
	    {
		reading = false;
		propose();
	    }
	}
	else if (message instanceof AbortImposeResponse)
	{
	    AbortImposeResponse abort = (AbortImposeResponse) message;
	    if (imposing == true && abort.getBallot() == ballot)
	    {
		imposing = false;
		propose();
	    }
	}
	else if (message instanceof GatherResponse)
	{
	    GatherResponse gather = (GatherResponse) message;
	    if (reading == true && gather.getBallot() == ballot)
	    {
		states.set(gather.getSenderID(),
			   new StateEntry(gather.getEstimate(),
					  gather.getImposeBallot())
		    );
		gathercount++;
	    }

	    if (gathercount > N / 2)
	    {
		reading = false;
		int i;
		int max_impose = 0;
		StateEntry temp;
		for (i = 0; i < N; i++)
		{
		    temp = states.get(i);
		    if (temp.getEstimate() != -1)
			if (temp.getImposeBallot() > max_impose)
			{
			    proposal = temp.getEstimate();
			    max_impose = temp.getImposeBallot();
			}
		}

		imposing = true;
		imposecount = 0;
		for(ActorRef actor: processes.references)
		{
		    actor.tell(new ImposeRequest(ballot, proposal), self());
		}
	    }
	}
	else if (message instanceof AckResponse)
	{
	    AckResponse ack = (AckResponse) message;
	    if (imposing == true && ack.getBallot() == ballot)
	    {
		imposecount++;
	    }

	    if (imposecount > N / 2)
	    {
		imposing = false;
		log.info("p" + self().path().name() + " sent DECIDE to all");
		for(ActorRef actor: processes.references)
		{
		    actor.tell(new DecideRequest(proposal), self());
		}
	    }

	}
	else if (message instanceof ReadRequest)
	{
	    ReadRequest read = (ReadRequest) message;
	    if (readballot >= read.getBallot() || imposeballot >= read.getBallot())
	    {
		getSender().tell(new AbortReadResponse(read.getBallot()), self());
	    }
	    else
	    {
		readballot = read.getBallot();
		getSender().tell(new GatherResponse(read.getBallot(),
					      imposeballot,
					      estimate,
					      id),
			   self());
	    }
	}
	else if (message instanceof ImposeRequest)
	{
	    ImposeRequest impose = (ImposeRequest) message;
	    if (readballot > impose.getBallot() || imposeballot > impose.getBallot())
	    {
		getSender().tell(new AbortImposeResponse(impose.getBallot()), self());
	    }
	    else
	    {
		estimate = impose.getProposal();
		imposeballot = impose.getBallot();
		getSender().tell(new AckResponse(impose.getBallot), self());
	    }
	}
	else if (message instanceof DecideRequest)
	{
	    DecideRequest decide = (DecideRequest) message;
	    reading = false;
	    imposing = false;
	    log.info("p" + self().path().name() + " received DECIDE for value : " + decide.getProposal());    
	}
	   	
/*	if(message instanceof ReadMsg) {
	    ReadMsg rm = (ReadMsg) message;
	    log.info("p" + self().path().name() + " received ReadMsg: " + rm.message);
	    }  */
    }
}
