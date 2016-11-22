package test;

import client.ConnectionLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

public class TestClasses {
    // How often to check load
    private static long INTERVAL = 5;
    public static void main(String args[]) {
        String host = "localhost";
        int port = 1099;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        ConnectionLayer connection = new ConnectionLayer("rmi://"+host+":"+port+"/auction", 1000);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        long noOfWorkers = 0, duration = 0;
        try {
            System.out.println("How many workers would you like to test against?");
            noOfWorkers = Long.valueOf(br.readLine());
            System.out.println("And for how long (in seconds, at least " + INTERVAL + ")?");
            duration = Long.valueOf(br.readLine());
            if (duration < INTERVAL) throw new IOException("Duration must be at least " + INTERVAL + "s");
        } catch (IOException e) {
            System.err.println("Unable to parse input " + e);
        }
        for (int i = 0; i < noOfWorkers; i++) {
            AuctionClientWorker cl = new AuctionClientWorker(connection, String.valueOf(i), duration * 1000);
            new Thread(cl).start();
        }
        try {
            // Output load every 5s
            for (int i = 0; i < duration / INTERVAL; i++) {
                Thread.sleep(INTERVAL * 1000);
                float load = connection.getFailureDetector().determineLoad();
                System.out.println("Server load - " + load);
            }
        } catch (RemoteException e) {
            System.err.println("Unable to determine server load - " + e);
        } catch (InterruptedException e) {
            System.err.println("Unable to sleep thread - " + e);
        }
    }
}
