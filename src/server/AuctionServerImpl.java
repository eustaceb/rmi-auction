package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuctionServerImpl extends UnicastRemoteObject implements IAuctionServer {

    private Map<Integer, AuctionItem> auctionItems;

    public AuctionServerImpl() throws RemoteException {
        super();
        auctionItems = new HashMap<Integer, AuctionItem>();
    }
    @Override
    public int createAuctionItem(String name, float minVal, int closingTime) throws RemoteException {
        AuctionItem item = new AuctionItem(name, minVal, closingTime);
        auctionItems.put(item.getId(), item);
        return item.getId();
    }

    @Override
    public int bid(int auctionItemId, float amount) throws RemoteException {
        Bid b = new Bid("owner", amount);
        auctionItems.get(auctionItemId).makeBid(b);
        return 0;
    }

    @Override
    public String getListing() throws RemoteException {
        StringBuilder result = new StringBuilder();
        for (AuctionItem item : auctionItems.values()) {
            result.append(item.toString());
        }
        return result.toString();
    }
}