package com.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.example.messages.LaunchRequest;
import com.example.messages.LeaderElectionMsg;
import com.example.utils.DecisionCheck;

public class Main {

    //public static int N = 5; /* Total number of processes */
    //public static int f = (int) Math.ceil((double) (N) / 2) - 1; /* Number of fault prone processes */
    public static double crashProbability = 0.5; /* Probability that a fault prone process will crash */
    //public static double leaderTimeout = 0.5; /* Timeout until leader election emulation in seconds */
    public static final ActorSystem system = ActorSystem.create("system");

    public static boolean checkIfDecided(DecisionCheck[] dcs){
        for(DecisionCheck dc: dcs){
            if(dc.decided)
                return true;
        }
        return false;
    }

    public static void testAndTime(int n, double timeout) throws InterruptedException {
        int f = (int) Math.ceil((double) (n) / 2) - 1;

        // Instantiate an actor system
        system.log().info("System started with N=" + n);

        ArrayList<ActorRef> references = new ArrayList<>();
        DecisionCheck[] decisionChecks = new DecisionCheck[n];

        for (int i = 0; i < n; i++) {
            // Instantiate processes
            decisionChecks[i] = new DecisionCheck();
            final ActorRef a = system.actorOf(Process.createActor(i, n, decisionChecks[i]), "" + i);
            references.add(a);
        }

        // give each process a view of all the other processes
        Members m = new Members(references);
        for (ActorRef actor : references) {
            actor.tell(m, ActorRef.noSender());
        }

        // choose fault prone processes
        // first f processes are fault prone
        Collections.shuffle(references);

        // Choose a leader
        int leaderIndex = (int) ((Math.random() * (((n-1) - f) + 1)) + f);
        int leaderId = Integer.parseInt(references.get(leaderIndex).path().name());
        system.log().info("Leader index: " + leaderIndex + " Leader id: " + leaderId);

        Thread.sleep(1000);

        ActorRef actor;
        for (int i = 0; i < n; i++) {
            actor = references.get(i);
            // the first f processes in the sheffled list will receive a faulty state
            actor.tell(new LaunchRequest(i < f), ActorRef.noSender());
            // Schedule leader election emulation
            system.scheduler().scheduleOnce((FiniteDuration) Duration.create(timeout, TimeUnit.SECONDS), actor,
                    new LeaderElectionMsg(leaderId), system.dispatcher(), null);

        }

        while(!checkIfDecided(decisionChecks));

        system.log().info("Decided");

    }


    public static void main(String[] args) throws InterruptedException {
        testAndTime(100, 2);
    }
}
