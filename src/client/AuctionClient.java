package client;

/**
 * Created by justas on 04/11/16.
 */
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import server.IAuctionServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Random;

/**
 * Created by justas on 03/11/16.
 */
public class AuctionClient implements IAuctionClient {

    private int port;
    private String host, name;

    protected AuctionClient() throws RemoteException {
        super();
    }

    protected AuctionClient(String host, int port) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {
        // TODO: Implement update
    }

    @Override
    public String toString() {
        return getName();
    }

}
