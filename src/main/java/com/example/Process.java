package com.example;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.util.ArrayList;
import java.lang.Math;

import com.example.messages.*;
import com.example.utils.DecisionCheck;

public class Process extends UntypedAbstractActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);// Logger attached to actor
	private final int N;// number of processes
	private final int id;// id of current process
	private Members processes;// other processes' references
	private boolean faultProne;
	private boolean hold;

	// -- OFCons variables --
	private long ballot, readballot, imposeballot;
	private int proposal, estimate;
	private ArrayList<StateEntry> states;

	private int gathercount, ackcount;
	private boolean reading, imposing;

	private DecisionCheck decisionCheck;

	public Process(int ID, int nb, DecisionCheck dc) {
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
		faultProne = false;
		hold = false;
		states = new ArrayList<StateEntry>(N);

		decisionCheck = dc;

		int i;
		for (i = 0; i < N; i++) {
			states.add(new StateEntry(-1, 0));
		}
	}

	public String toString() {
		return "Process{" + "id=" + id;
	}

	/**
	 * Static function creating actor
	 */
	public static Props createActor(int ID, int nb, DecisionCheck dc) {
		return Props.create(Process.class, () -> {
			return new Process(ID, nb, dc);
		});
	}

	void propose() {
		proposal = ((int) (Math.random() * 1000)) % 2;
		ballot = ballot + N;
		int i;
		for (i = 0; i < N; i++) {
			states.set(i, new StateEntry(-1, 0));
		}
		reading = true;
		gathercount = 0;

		log.info("p" + self().path().name() + " proposed " + proposal + " with ballot: " + ballot);
		log.info("p" + self().path().name() + " started the Reading phase");
		for (ActorRef actor : processes.references) {
			actor.tell(new ReadRequest(ballot), self());
		}
	}

	public void onReceive(Object message) throws Throwable {

		if (message instanceof Members) {// save the system's info
			Members m = (Members) message;
			processes = m;
			log.info("p" + self().path().name() + " received processes info");
		}

		/* --- Synoid OFCons --- */
		else if (!(faultProne && (Math.random() < Main.crashProbability))) {

			if (message instanceof LaunchRequest) {
				LaunchRequest launch = (LaunchRequest) message;
				faultProne = launch.getFaulty();
				boolean silent = (faultProne && (Math.random() < Main.crashProbability));
				log.info("p" + self().path().name() + " received the launch request. faultProne: " + faultProne);
				if (!silent && !hold)
					propose();
			}

			else if (message instanceof LeaderElectionMsg) {
				LeaderElectionMsg leaderElection = (LeaderElectionMsg) message;
				hold = (id != leaderElection.leaderId);
				log.info("p" + self().path().name() + " received a leader election. leader id: "
						+ leaderElection.leaderId);
				if (hold)
					log.info("p" + self().path().name() + " is holding...");
			}

			else if (message instanceof AbortReadResponse) {
				AbortReadResponse abort = (AbortReadResponse) message;
				if (reading == true && abort.getBallot() == ballot) {
					log.info("p" + self().path().name() + " received an AbortRead");
					reading = false;
					if (!hold)
						propose();
				}
			} else if (message instanceof AbortImposeResponse) {
				AbortImposeResponse abort = (AbortImposeResponse) message;
				if (imposing == true && abort.getBallot() == ballot) {
					log.info("p" + self().path().name() + " received an AbortImpose");

					imposing = false;
					if (!hold)
						propose();
				}
			} else if (message instanceof GatherResponse) {
				GatherResponse gather = (GatherResponse) message;
				if (reading == true && gather.getBallot() == ballot) {
					states.set(gather.getSenderID(), new StateEntry(gather.getEstimate(), gather.getImposeBallot()));
					gathercount++;

					//log.info("p" + self().path().name() + " received a new GatherResponse. Now gathercount = "
					//		+ gathercount);
				}

				if (reading == true && gathercount > N / 2) {
					reading = false;
					int i;
					long max_impose = 0;
					StateEntry temp;
					for (i = 0; i < N; i++) {
						temp = states.get(i);
						if (temp.getEstimate() != -1)
							if (temp.getImposeBallot() > max_impose) {
								proposal = temp.getEstimate();
								max_impose = temp.getImposeBallot();
							}
					}
					log.info("p" + self().path().name() + " finished Read phase and read v = " + proposal
							+ ", now it has started Impose");

					imposing = true;
					ackcount = 0;
					for (ActorRef actor : processes.references) {
						actor.tell(new ImposeRequest(ballot, proposal), self());
					}
				}
			} else if (message instanceof AckResponse) {
				AckResponse ack = (AckResponse) message;
				if (imposing == true && ack.getBallot() == ballot) {
					ackcount++;
					//log.info("p" + self().path().name() + " received a new AckResponse. Now ackcount = " + ackcount);
				}

				if (imposing == true && ackcount > N / 2) {
					imposing = false;
					log.info("p" + self().path().name() + " sent DECIDE to all");
					for (ActorRef actor : processes.references) {
						actor.tell(new DecideRequest(proposal), self());
					}
				}

			} else if (message instanceof ReadRequest) {
				ReadRequest read = (ReadRequest) message;
				if (readballot >= read.getBallot() || imposeballot >= read.getBallot()) {
					getSender().tell(new AbortReadResponse(read.getBallot()), self());
					//log.info(
					//		"p" + self().path().name() + " has denied p" + getSender().path().name() + " Read request");
				} else {
					readballot = read.getBallot();
					getSender().tell(new GatherResponse(read.getBallot(), imposeballot, estimate, id), self());

					//log.info("p" + self().path().name() + " has accepted p" + getSender().path().name()
					//		+ " Read request");
				}
			} else if (message instanceof ImposeRequest) {
				ImposeRequest impose = (ImposeRequest) message;
				if (readballot > impose.getBallot() || imposeballot > impose.getBallot()) {
					getSender().tell(new AbortImposeResponse(impose.getBallot()), self());
					//log.info("p" + self().path().name() + " has denied p" + getSender().path().name()
					//		+ " Impose request");
				} else {
					estimate = impose.getProposal();
					imposeballot = impose.getBallot();
					getSender().tell(new AckResponse(impose.getBallot()), self());
					//log.info("p" + self().path().name() + " has accepted p" + getSender().path().name()
					//		+ " Impose request");
				}
			} else if (message instanceof DecideRequest) {
				DecideRequest decide = (DecideRequest) message;
				reading = false;
				imposing = false;
				log.info("p" + self().path().name() + " received DECIDE for value : " + decide.getProposal());
				decisionCheck.decided = true;
			}
		}

	}
}
