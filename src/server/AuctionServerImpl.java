package server;

import client.IAuctionClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// REPORT: All error checking is done in the server
public class AuctionServerImpl extends UnicastRemoteObject implements IAuctionServer {

    private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // 60min
    private class LifecycleAuctionItemTask extends TimerTask {
        private int id;
        public LifecycleAuctionItemTask(int id) {
            this.id = id;
        }
        @Override
        public void run() {
            // If the auction is open - close and create a new task for removing it from closed
            AuctionItem expiredAuction;
            if ((expiredAuction = auctionItems.remove(id)) != null) {
                closedAuctionItems.put(id, expiredAuction);
                timer.schedule(new LifecycleAuctionItemTask(id), CLOSED_ITEM_CLEANUP_PERIOD);
                // TODO: Notify
            } else {
                // Remove the closed auction permanently after cleanup period
                closedAuctionItems.remove(id);
            }
        }
    }

    private Timer timer;
    private Map<Integer, AuctionItem> auctionItems, closedAuctionItems;
    private List<Integer> activeAuctionItemQ;

    public AuctionServerImpl() throws RemoteException {
        super();
        auctionItems = new HashMap<>();
        closedAuctionItems = new HashMap<>();
        timer = new Timer();
    }
    private void sampleData(){
        ArrayList<String> items = new ArrayList<>(Arrays.asList(
                "Shoe made from potatoes", "Caged goat", "Rock", "Movie II", "Movie III", "Jungle Bubble",
                "Curse", "Potato", "Crocodile tears", "Lobster", "Dust", "TV", "Burbonic plague", "Glowing mushroom"));

        Random rg = new Random();
        try {
            for (int i = 0; i <5; i++) {
                long endTime = System.currentTimeMillis() + 60 * 1000 * (10 + rg.nextInt(50));
                // TODO: Replace null
                this.createAuctionItem(null, items.get(rg.nextInt(items.size())), rg.nextFloat() * 100, endTime);
            }
            System.out.println("Successfully created some sample items.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createAuctionItem(IAuctionClient owner, String name, float minVal, long closingTime) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        if (name == null) return ErrorCodes.NAME_NULL.MESSAGE;
        if (name.length() == 0) return ErrorCodes.NAME_EMPTY.MESSAGE;
        if (minVal < 0) return ErrorCodes.NEGATIVE_MINVAL.MESSAGE;
        if (closingTime < 0) return ErrorCodes.NEGATIVE_CLOSING_TIME.MESSAGE;

        AuctionItem item = new AuctionItem(owner, name, minVal, closingTime);
        auctionItems.put(item.getId(), item);
        return ErrorCodes.ITEM_CREATED.MESSAGE;
    }

    @Override
    public String bid(IAuctionClient owner, int auctionItemId, float amount) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        AuctionItem item = auctionItems.get(auctionItemId);
        if (item == null) return ErrorCodes.AUCTION_DOES_NOT_EXIST.MESSAGE;
        if (item.getOwner() == owner) return ErrorCodes.BID_ON_OWN_ITEM.MESSAGE;
        Bid b = new Bid(owner, amount);
        return item.makeBid(b);
    }

    @Override
    public String getOpenAuctions() throws RemoteException {
        StringBuilder result = new StringBuilder();
        for (AuctionItem item : auctionItems.values()) {
            result.append(item.toString());
        }
        return result.toString();
    }

    @Override
    public String getClosedAuctions() throws RemoteException {
        StringBuilder result = new StringBuilder();
        for (AuctionItem item : closedAuctionItems.values()) {
            result.append(item.toString());
        }
        return result.toString();
    }
}