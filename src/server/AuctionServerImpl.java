package server;

import client.AuctionClient;
import client.IAuctionClient;
import javafx.util.Pair;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
// REPORT: All error checking is done in the server
// TODO: Comments
public class AuctionServerImpl extends UnicastRemoteObject implements IAuctionServer {
    static final long serialVersionUID = 1L;

    private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // 60min
    private long stopped;

    private class LifecycleAuctionItemTask extends TimerTask implements Serializable {
        static final long serialVersionUID = 1L;

        private int id;
        private long started;

        public LifecycleAuctionItemTask(int id) {
            this.id = id;
            this.started = System.currentTimeMillis();
        }

        public long getElapsed() {
            return stopped - this.started;
        }
        @Override
        public void run() {
            // If the auction is open - close and create a new task for removing it from closed. Notify observers.
            AuctionItem expiredAuction;
            if ((expiredAuction = auctionItems.remove(id)) != null) {
                closedAuctionItems.put(id, expiredAuction);

                LifecycleAuctionItemTask t = new LifecycleAuctionItemTask(id);
                timer.schedule(t, CLOSED_ITEM_CLEANUP_PERIOD);
                timerTasks.add(new Pair<>(t, CLOSED_ITEM_CLEANUP_PERIOD));

                StringBuilder m = new StringBuilder("Auction for item " + expiredAuction.getName() + " has ended. ");
                m.append("Winning bid - ").append(expiredAuction.getCurrentBid().getAmount());
                m.append(" by ").append(expiredAuction.getCurrentBid().getOwnerName());
                expiredAuction.notifyObservers(m.toString());
                System.out.println(m);
            } else {
                // Remove the closed auction permanently after cleanup period
                closedAuctionItems.remove(id);
                System.out.println("Removed auction ID #" + id);
            }
        }
    }

    private transient Timer timer;
    private List<Pair<LifecycleAuctionItemTask, Long>> timerTasks;
    private Map<Integer, AuctionItem> auctionItems, closedAuctionItems;

    public AuctionServerImpl() throws RemoteException {
        super();
        auctionItems = new HashMap<>();
        closedAuctionItems = new HashMap<>();
        timer = new Timer();
        timerTasks = new LinkedList<>();
    }

    /**
     * Reloads timer when loading auction from file
     */
    public void stopTimer() {
        stopped = System.currentTimeMillis();
    }
    /**
     * Reloads timer when loading auction from file
     */
    public void reloadTimer() {
        timer = new Timer();
        for (Pair<LifecycleAuctionItemTask, Long> t: timerTasks) {
            // TODO: Bug - auction end condition different
            // Reschedule task to initial value subtracted how much has already elapsed
            timer.schedule(t.getKey(), t.getValue() - t.getKey().getElapsed());
        }
    }

    @Override
    public String createAuctionItem(IAuctionClient owner, String name, float minVal, long closingTime) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        System.out.println("Client " + owner.getName() + " is trying to create an auction item");
        if (name == null) return ErrorCodes.NAME_NULL.MESSAGE;
        if (name.length() == 0) return ErrorCodes.NAME_EMPTY.MESSAGE;
        if (minVal < 0) return ErrorCodes.NEGATIVE_MINVAL.MESSAGE;
        if (closingTime < 0) return ErrorCodes.NEGATIVE_CLOSING_TIME.MESSAGE;
        // Further validation in AuctionItem's constructor
        AuctionItem item = new AuctionItem(owner, name, minVal, closingTime);
        auctionItems.put(item.getId(), item);
        // Timer task
        LifecycleAuctionItemTask t = new LifecycleAuctionItemTask(item.getId());
        timer.schedule(t, closingTime * 1000);
        timerTasks.add(new Pair<>(t, closingTime * 1000));

        System.out.println("Auction ID #" + item.getId() + " - " + item.getName() + " created");
        return ErrorCodes.ITEM_CREATED.MESSAGE;
    }

    @Override
    public String bid(IAuctionClient owner, int auctionItemId, float amount) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        // Get owner name so we don't have to query multiple times and risk a RemoteException
        String ownerName = owner.getName();
        System.out.println("Client " + ownerName + " is trying to bid on item ID #" + auctionItemId);
        // Validate item exists and doesn't belong to the bidder
        AuctionItem item = auctionItems.get(auctionItemId);
        if (item == null) return ErrorCodes.AUCTION_DOES_NOT_EXIST.MESSAGE;
        if (item.getOwner() == owner) return ErrorCodes.BID_ON_OWN_ITEM.MESSAGE;
        // Make the bid (further validation is in Bid's constructor)
        Bid b = new Bid(owner, ownerName, amount);
        String result = item.makeBid(b);

        System.out.println(result + " - " + owner.getName() + ", item ID #" + auctionItemId);
        return result;
    }

    @Override
    public String getOpenAuctions() throws RemoteException {
        if (auctionItems.size() == 0) return "No available auctions";
        StringBuilder result = new StringBuilder();
        for (AuctionItem item : auctionItems.values()) {
            result.append(item.toString());
            result.append("-----------------------\n");
        }
        return result.toString();
    }

    @Override
    public String getClosedAuctions() throws RemoteException {
        if (closedAuctionItems.size() == 0) return "No historical auctions";
        StringBuilder result = new StringBuilder();
        for (AuctionItem item : closedAuctionItems.values()) {
            result.append(item.toString());
            result.append("-----------------------\n");
        }
        return result.toString();
    }
    @Override
    public void probe() throws RemoteException { }
}