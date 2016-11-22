package server;

import client.IAuctionClient;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IAuctionServer extends Remote {
    /**
     * Create an auction item
     * @param owner client object
     * @param name item name
     * @param minVal minimum bid
     * @param closingTime closing time in seconds
     * @return success/error message
     * @throws RemoteException
     */
    String createAuctionItem(IAuctionClient owner, String name, float minVal, long closingTime) throws RemoteException;

    /**
     * Make a bid
     * @param owner client object
     * @param auctionItemId item id
     * @param amount bid amount
     * @return success/error message
     * @throws RemoteException
     */
    String bid(IAuctionClient owner, int auctionItemId, float amount) throws RemoteException;

    /**
     * Returns a nicely formatted string that contains a list of open auctions
     * @return list of open auctions
     * @throws RemoteException
     */
    String getOpenAuctions() throws RemoteException;

    /**
     * Returns a set of item IDs instead of a user-friendly string
     * Mainly used by AuctionClientWorker
     * @return set of auction item ids
     * @throws RemoteException
     */
    ArrayList<Integer> getOpenAuctionIds() throws RemoteException;
    /**
     * Returns a nicely formatted string that contains a list of closed auctions
     * @return list of closed auctions
     * @throws RemoteException
     */
    String getClosedAuctions() throws RemoteException;

    /**
     * Probes the server to check if alive
     * @throws RemoteException
     */
    void probe() throws RemoteException;
}
