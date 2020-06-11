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

    public static double crashProbability = 0.5; /* Probability that a fault prone process will crash */

    public static boolean checkIfDecided(DecisionCheck[] dcs) {
        for (DecisionCheck dc : dcs) {
            if (dc.decided)
                return true;
        }
        return false;
    }

    public static long testAndTime(int n, double timeout) throws InterruptedException {
        ActorSystem system;
        int f = (int) Math.ceil((double) (n) / 2) - 1;
        long startTime, endTime, duration;
        duration = 0;

        ArrayList<ActorRef> references;
        DecisionCheck[] decisionChecks;
        Members m;

        int leaderIndex, leaderId;

        for (int k = 0; k < 5; k++) {
            system = ActorSystem.create("system");
            // Instantiate an actor system
            system.log().info("System started with N=" + n);

            references = new ArrayList<>();
            decisionChecks = new DecisionCheck[n];

            for (int i = 0; i < n; i++) {
                // Instantiate processes
                decisionChecks[i] = new DecisionCheck();
                ActorRef a = system.actorOf(Process.createActor(i, n, decisionChecks[i]), "" + i);
                references.add(a);
            }

            // give each process a view of all the other processes
            m = new Members(references);
            for (ActorRef actor : references) {
                actor.tell(m, ActorRef.noSender());
            }

            // choose fault prone processes
            // first f processes are fault prone
            Collections.shuffle(references);

            // Choose a leader
            leaderIndex = (int) ((Math.random() * (((n - 1) - f) + 1)) + f);
            leaderId = Integer.parseInt(references.get(leaderIndex).path().name());
            system.log().info("Leader index: " + leaderIndex + " Leader id: " + leaderId);

            Thread.sleep(1000);

            ActorRef actor;
            startTime = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                actor = references.get(i);
                // the first f processes in the sheffled list will receive a faulty state
                actor.tell(new LaunchRequest(i < f), ActorRef.noSender());
                // Schedule leader election emulation
                system.scheduler().scheduleOnce((FiniteDuration) Duration.create(1000*timeout, TimeUnit.MILLISECONDS), actor,
                        new LeaderElectionMsg(leaderId), system.dispatcher(), null);

            }

            while (!checkIfDecided(decisionChecks));
            endTime = System.currentTimeMillis();
            duration += (endTime - startTime);
            System.out.println("MAIN - sub: N = " + n + " timeout = " + timeout + "s time = " + (endTime - startTime) + "ms");

            system.terminate();
        }
        duration/=5;

        return duration;
    }

    public static void main(String[] args) throws InterruptedException {
        int ns[] = {3, 10, 100};
        int n;
        long time;
        for(int i=0;i<3;i++){
            n = ns[i];
            for(double timeout=0.5;timeout<=2.0;timeout+=0.5){
                time = testAndTime(n, timeout);
                System.out.println("MAIN: N = " + n + " timeout = " + timeout + "s time = " + time + "ms");
            }
        }
    }
}
