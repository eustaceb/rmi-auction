package client;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import server.IAuctionServer;

public class FailureDetector {
    private final static long DEFAULT_TIMEOUT = 5000, DEFAULT_PERIOD = 5000;
    
    private Timer timer; 
    private IAuctionServer server;
    private long timeout, period;
    
    private class ProbeTask extends TimerTask {
        @Override
        public void run() {
            try {
                server.probe();
            } catch (RemoteException e) {
                System.err.println("Cannot contact the server. Retrying in " + period + "ms");
            }
        }
    }

    /**
     * Init failure detectore with DEFAULT_TIMEOUT and DEFAULT_PERIOD
     * @param server
     */
    public FailureDetector(IAuctionServer server) {
        this(server, DEFAULT_TIMEOUT, DEFAULT_PERIOD);
    }
    
    /**
     * Create a failure detector with a specific timeout and period
     * @param server
     * @param timeout in milliseconds
     * @param period how often to probe the server in milliseconds
     */
    public FailureDetector(IAuctionServer server, long timeout, long period) {
        this.server = server;
        this.timeout = timeout;
        this.period = period;
    }
    
    /**
     * Create a failure detector with a number of probes and sensitivity to determine the timeout + the period
     * @param server
     * @param noOfProbes how many probes to send when determining the timeout
     * @param sensitivity how much a call can deviate from the average
     * @param period how often to probe the server in milliseconds
     */
    public FailureDetector(IAuctionServer server, int noOfProbes, long sensitivity, long period) {
        this.server = server;
        try {
            this.setTimeout(determineTimeout(noOfProbes, sensitivity));
        } catch (RemoteException e) {
            System.err.println("Unable to contact the server in order to determine timeout. Setting default");
            this.timeout = DEFAULT_TIMEOUT;
        }
        timer.schedule(new ProbeTask(), 0, period);
    }
    
    /**
     * Calculates the timeout with respect to sensitivity and number of probes
     * @param sensitivity how much a call can deviate from the average
     * @param noOfProbes number of probes to send
     * @return timeout in milliseconds (average turnaround time + sensitivity)
     * @throws RemoteException
     */
    private long determineTimeout(int noOfProbes, long sensitivity) throws RemoteException {
        // Prime system
        server.probe();
        long start = System.currentTimeMillis();
        for (int i = 0; i < noOfProbes; i++) {
            server.probe();
        }
        long averageTurnaround = (System.currentTimeMillis() - start) / noOfProbes;
        
        return averageTurnaround + sensitivity;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public IAuctionServer getServer() {
        return server;
    }

    public void setServer(IAuctionServer server) {
        this.server = server;
    }


    public long getPeriod() {
        return period;
    }


    public void setPeriod(long period) {
        this.period = period;
    }
}
