package client;

import java.rmi.RemoteException;
import java.util.Observable;

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


    @Override
    public void callback(String message) {
        System.out.println(message);
    }

    @Override
    public String toString() {
        return getName();
    }

}
