package server;

import client.IAuctionClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by justas on 04/11/16.
 * The client program(s) should enable a user to create auction items (specifying name, minimum item
 value and closing date/time specified in seconds; returning unique id), and to bid against existing
 auction items. Methods for listing available auction items should also be included. By the end of an
 auction, the owner as well as the bidders of the item should be notified of the result (e.g., winner id,
 winning bid, price not met, etc.).
 */
public interface IAuctionServer extends Remote {
    public int createAuctionItem(IAuctionClient owner, String name, float minVal, long closingTime) throws RemoteException;
    public int bid(IAuctionClient owner, int auctionItemId, float amount) throws RemoteException;
    public String getOpenAuctions() throws RemoteException;
    public String getClosedAuctions() throws RemoteException;
}
