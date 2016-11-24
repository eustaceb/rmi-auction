package client;

import server.IAuctionServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Ideally, the failure detector should be optional
 * It should be assigned using connection.setFailureDetector(new FailureDetector(param1, param2, ...))
 * Having failure detector params in the constructor is a bad idea
 */
public class ConnectionLayer {
    private FailureDetector failureDetector;
    private String connectionStr;

    private boolean connected = false;

    private IAuctionServer server;

    /**
     * Basic constructor that only requires a connection string
     * @param connectionStr
     */
    public ConnectionLayer(String connectionStr) {
        this.connectionStr = connectionStr;
        connect();
        failureDetector = new FailureDetector(this);
    }

    /**
     * The period parameter is passed to the FailureDetector, determines how often to retry in case connection breaks
     * @param connectionStr
     * @param period
     */
    public ConnectionLayer(String connectionStr, long period) {
        this.connectionStr = connectionStr;
        connect();
        failureDetector = new FailureDetector(this, period);
    }

    private void connect() {
        if (!isConnected()) {
            try {
                server = (IAuctionServer) Naming.lookup(connectionStr);
                // Flag used by the servlet
                setConnected(true);
            } catch (MalformedURLException e) {
                System.err.println("Malformed URL - " + e);
            } catch (NotBoundException e) {
                System.err.println("Unable to bind the server - " + e);
            } catch (RemoteException e) {
                System.err.println("Unable to contact the server - " + e);
            }
        }
    }

    public void reconnect() {
        connect();
        if (isConnected()) {
            System.out.println("Reconnected!");
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void setConnected(boolean connected) {
        this.connected = connected;
    }

    public FailureDetector getFailureDetector() {
        return failureDetector;
    }

    public IAuctionServer getServer() throws RemoteException {
        if (isConnected()) {
            return server;
        } else {
            throw new RemoteException("Server is dead.");
        }
    }

    public void setServer(IAuctionServer server) {
        this.server = server;
    }
}
