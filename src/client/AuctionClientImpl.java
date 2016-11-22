package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuctionClientImpl extends UnicastRemoteObject implements IAuctionClient {
    private static final long serialVersionUID = 1L;
    private String name;

    public AuctionClientImpl() throws RemoteException {
        super();
        this.name = "Name not set";
    }
    public AuctionClientImpl(String name) throws RemoteException {
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
