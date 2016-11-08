package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class AuctionServerImpl extends UnicastRemoteObject implements IAuctionServer {

    private Map<Integer, AuctionItem> auctionItems;

    public AuctionServerImpl() throws RemoteException {
        super();
        auctionItems = new HashMap<Integer, AuctionItem>();

    }
    private void sampleData(){
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(
                "Shoe made from potatoes", "Caged goat", "Rock", "Movie II", "Movie III", "Movie IV",
                "Curse", "Potato", "Crams", "Lobster", "Dust", "TV", "Burbonic plague", "Glowing mushroom"));

        Random rg = new Random();
        try {
            for (int i = 0; i <5; i++) {
                long endTime = System.currentTimeMillis() + 60 * 1000 * (10 + rg.nextInt(50));
                this.createAuctionItem(items.get(rg.nextInt(items.size())), rg.nextFloat() * 100, endTime);
            }
            System.out.println("Successfully created some sample items.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createAuctionItem(String name, float minVal, long closingTime) throws RemoteException {
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