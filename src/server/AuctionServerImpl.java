package server;

import client.IAuctionClient;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuctionServerImpl extends UnicastRemoteObject implements IAuctionServer {
    static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuctionServerImpl.class.getName());


    private class LifecycleAuctionItemTask extends TimerTask implements Serializable {
        static final long serialVersionUID = 1L;

        private final long CLOSED_ITEM_CLEANUP_PERIOD = 60 * (60 * 1000); // 60min

        private int id;
        private long started;

        public LifecycleAuctionItemTask(int id) {
            this.id = id;
            this.started = System.currentTimeMillis();
        }

        /**
         * How much time this task has left to run
         * @return
         */
        public long getTimeLeft() {
            return System.currentTimeMillis() - started;
        }
        @Override
        public void run() {
            // If the auction is open - close and create a new task for removing it from closed.
            // If the auction is already closed - remove it.
            AuctionItem expiredAuction;
            synchronized(this) {
                if ((expiredAuction = auctionItems.remove(id)) != null) {
                    // Move auction to closed
                    closedAuctionItems.put(id, expiredAuction);
                    // Schedule cleanup task
                    LifecycleAuctionItemTask t = new LifecycleAuctionItemTask(id);
                    timer.schedule(t, CLOSED_ITEM_CLEANUP_PERIOD);
                    timerTasks.put(t, CLOSED_ITEM_CLEANUP_PERIOD);
                    // Notify observers
                    StringBuilder m = new StringBuilder("Auction for item " + expiredAuction.getName() + " has ended. ");
                    if (expiredAuction.getCurrentBid() != null) {
                        m.append("Winning bid - ").append(expiredAuction.getCurrentBid().getAmount());
                        m.append(" by ").append(expiredAuction.getCurrentBid().getOwnerName());
                    } else {
                        m.append("No winner!");
                    }
                    expiredAuction.notifyObservers(m.toString());
                    LOGGER.info(m.toString());
                } else {
                    // Remove the closed auction permanently after cleanup period
                    closedAuctionItems.remove(id);
                    LOGGER.info("Removed auction ID #" + id);
                }
                // Remove this task from map
                timerTasks.remove(this);
            }
        }
    }

    private transient Timer timer;
    private Map<LifecycleAuctionItemTask, Long> timerTasks;
    private Map<Integer, AuctionItem> auctionItems, closedAuctionItems;

    public AuctionServerImpl() throws RemoteException {
        super();
        auctionItems = new ConcurrentHashMap<>();
        closedAuctionItems = new HashMap<>();
        timer = new Timer();
        timerTasks = new HashMap<>();
        LOGGER.setLevel(Level.OFF);
    }

    /**
     * Reloads timer when loading auction from file
     */
    public void reloadTimer() {
        timer = new Timer();
        for (Map.Entry<LifecycleAuctionItemTask, Long> t: timerTasks.entrySet()) {
            // Reschedule task to initial value subtracted how much has already elapsed
            long timeLeft = t.getValue() - t.getKey().getTimeLeft();
            timer.schedule(t.getKey(), timeLeft < 0 ? 0 : timeLeft);
        }
    }

    @Override
    public String createAuctionItem(IAuctionClient owner, String name, float minVal, long closingTime) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        LOGGER.info("Client " + owner.getName() + " is trying to create an auction item");
        if (name == null) return ErrorCodes.NAME_NULL.MESSAGE;
        if (name.length() == 0) return ErrorCodes.NAME_EMPTY.MESSAGE;
        if (minVal < 0) return ErrorCodes.NEGATIVE_MINVAL.MESSAGE;
        if (closingTime < 0) return ErrorCodes.NEGATIVE_CLOSING_TIME.MESSAGE;
        // Further validation in AuctionItem's constructor
        AuctionItem item = new AuctionItem(owner, name, minVal, closingTime);
        auctionItems.put(item.getId(), item);
        // Timer for ending the auction
        LifecycleAuctionItemTask t = new LifecycleAuctionItemTask(item.getId());
        timer.schedule(t, closingTime * 1000);
        timerTasks.put(t, closingTime * 1000);

        LOGGER.info("Auction ID #" + item.getId() + " - " + item.getName() + " created");
        return ErrorCodes.ITEM_CREATED.MESSAGE;
    }

    @Override
    public String bid(IAuctionClient owner, int auctionItemId, float amount) throws RemoteException {
        if (owner == null) return ErrorCodes.OWNER_NULL.MESSAGE;
        // Get owner name so we don't have to query multiple times and risk a RemoteException
        String ownerName = owner.getName();
        LOGGER.info("Client " + ownerName + " is trying to bid on item ID #" + auctionItemId);
        // Validate item exists and doesn't belong to the bidder
        AuctionItem item = auctionItems.get(auctionItemId);
        if (item == null) return ErrorCodes.AUCTION_DOES_NOT_EXIST.MESSAGE;
        if (item.getOwner() == owner) return ErrorCodes.BID_ON_OWN_ITEM.MESSAGE;
        // Make the bid (further validation is in Bid's constructor)
        Bid b = new Bid(owner, ownerName, amount);
        String result = item.makeBid(b);

        LOGGER.info(result + " - " + owner.getName() + ", item ID #" + auctionItemId);
        return result;
    }

    @Override
    public String getOpenAuctions() throws RemoteException {
        if (auctionItems.size() == 0) return "No available auctions";
        StringBuilder result = new StringBuilder();
        String separator = "-----------------------\n";
        for (AuctionItem item : auctionItems.values()) {
            result.append(item.toString());
            result.append(separator);
        }
        result.delete(result.length() - separator.length(), result.length());
        return result.toString();
    }

    public ArrayList<Integer> getOpenAuctionIds() throws RemoteException {
        return new ArrayList<Integer>(auctionItems.keySet());
    }

    @Override
    public String getClosedAuctions() throws RemoteException {
        if (closedAuctionItems.size() == 0) return "No historical auctions";
        StringBuilder result = new StringBuilder();
        String separator = "-----------------------\n";
        for (AuctionItem item : closedAuctionItems.values()) {
            result.append(item.toString());
            result.append(separator);
        }
        result.delete(result.length() - separator.length(), result.length());
        return result.toString();
    }

    @Override
    public void probe() throws RemoteException { }
}