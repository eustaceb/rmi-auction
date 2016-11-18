package client;

import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import server.IAuctionServer;
// REPORT: Have an additional layer for request serving with a queue for tracking load (no of requests that haven't be served yet)
// REPORT: Naive ProbeTask implementation - could be made better by overloading RMISocketFactory()
// REPORT: Retries infinitely, could set max retries.
public class FailureDetector {
    private final static long DEFAULT_TIMEOUT = 5000, DEFAULT_PERIOD = 5000;
    private final static int DEFAULT_NO_OF_PROBES = 10000;
    
    private Timer timer = new Timer(); 
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
     * Create a failure detector that will send probes every <period> milliseconds
     * @param server
     * @param period how often to probe the server in milliseconds
     */
    public FailureDetector(IAuctionServer server, long period) {
        this(server, DEFAULT_TIMEOUT, period);
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
        this.timer.schedule(new ProbeTask(), 1, period);
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
            this.timeout = determineTimeout(noOfProbes, sensitivity);
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
        return (long)determineLoad(noOfProbes) + sensitivity;
    }

    /**
     * Calculates average turnaround with the default number of probes
     * @return average turnaround, in milliseconds
     * @throws RemoteException
     */
    public float determineLoad() throws RemoteException {
        return determineLoad(DEFAULT_NO_OF_PROBES);
    }
    
    /**
     * Calculates average turnaround
     * @param noOfProbes how many probes to use
     * @return average turnaround, in milliseconds
     * @throws RemoteException
     */
    public float determineLoad(int noOfProbes) throws RemoteException {
        server.probe();
        long start = System.currentTimeMillis();
        for (int i = 0; i < noOfProbes; i++) {
            server.probe();
        }
        float averageTurnaround = Float.valueOf((System.currentTimeMillis() - start)) / noOfProbes;
        return averageTurnaround;
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
