package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by justas on 03/11/16.
 */
public class AuctionClient extends UnicastRemoteObject implements IAuctionClient {
    private String name;

    public AuctionClient() throws RemoteException {
        super();
        this.name = "Name not set";
    }
    public AuctionClient(String name) throws RemoteException {
        super();
        this.name = name;
    }

    public String getName() throws RemoteException {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void callback(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public String toString() {
        return name;
    }

}
