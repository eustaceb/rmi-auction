package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Auction Client Data Model
 */
// REPORT: Thought about having this as an observer to observe AuctionItem but that lets clients modify items.
public interface IAuctionClient extends Remote {
    String getName() throws RemoteException;
    void callback(String message) throws RemoteException;
}
