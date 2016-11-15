package server;

import client.IAuctionClient;
import javafx.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by justas on 04/11/16.
 */
public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idCounter = 0;
    private int id;

    private IAuctionClient owner;
    private LinkedList<Bid> bids;
    private Set<IAuctionClient> observers;
    private String name;
    private float minBid;
    private Date startDate, closingDate;

    public AuctionItem(IAuctionClient owner, String name, float minBid, long closingTime) {
        this.owner = owner;
        this.startDate = new Date(System.currentTimeMillis());
        this.closingDate = new Date(System.currentTimeMillis() + 1000 * closingTime);
        this.id = idCounter;
        idCounter += 1;
        this.name = name;
        this.bids = new LinkedList<>();
        this.observers = new HashSet<>();
        this.minBid = minBid;
    }

    public synchronized String makeBid(Bid b) {
        Bid currentBid = getCurrentBid();
        if (closingDate.getTime() - startDate.getTime() < 0) {
            return ErrorCodes.AUCTION_CLOSED.MESSAGE;
        } else if (b.getAmount() <= minBid) {
            return ErrorCodes.LOW_BID.MESSAGE;
        } else if (currentBid != null) {
            if (b.getAmount() <= currentBid.getAmount()) {
                return ErrorCodes.LOW_BID.MESSAGE;
            } else if (b.getOwner() == currentBid.getOwner()) {
                return ErrorCodes.ALREADY_MAX_BIDDER.MESSAGE;
            }
        }
        bids.push(b);
        observers.add(b.getOwner());
        // Notify clients about the new bid
        for (IAuctionClient client : observers) {
            if (client == b.getOwner()) {
                client.callback("You're the max bidder with " + b.getAmount());
            } else {
                client.callback("You've been outbid on " + this.getName());
            }
        }
        return ErrorCodes.SUCCESS_BID.MESSAGE;
    }

    public synchronized Bid getCurrentBid() {
        if (bids.size() > 0) {
            return bids.peek();
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public IAuctionClient getOwner() {
        return owner;
    }

    public void setOwner(IAuctionClient owner) {
        this.owner = owner;
    }

    public LinkedList<Bid> getBids() {
        return bids;
    }

    public void setBids(LinkedList<Bid> bids) {
        this.bids = bids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMinBid() {
        return minBid;
    }

    public void setMinBid(float minBid) {
        this.minBid = minBid;
    }

    public long getClosingTime() {
        return this.closingDate.getTime();
    }

    public void setClosingTime(long closingTime) {
        this.closingDate = new Date(closingTime);
    }

    public long getStartTime() {
        return this.startDate.getTime();
    }

    public void setStartTime(long startTime) {
        this.startDate = new Date(startTime);
    }

    public String getBidListStr() {
        StringBuilder result = new StringBuilder("Bids so far:\n==================\n");
        synchronized(bids) {
            for (Bid b : bids) {
                result.append(b.toString()).append("\n");
            }
        }
        return result.toString();
    }
    @Override
    public String toString() {
        synchronized(this) {
            Bid currentBid = getCurrentBid();
            SimpleDateFormat dF = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

            String timeLeftStr = "has ended";
            long timeDiff = closingDate.getTime() - startDate.getTime();
            if (timeDiff > 0 && timeDiff < 60 * 1000) {
                timeLeftStr = String.valueOf(timeDiff / 1000) + "s";
            } else if (timeDiff >= 60 * 1000 && timeDiff < 60 * 60 * 1000) {
                timeLeftStr = String.valueOf(timeDiff / 1000 / 60) + "min " + (timeDiff / 1000) % 60 + "s";
            } else if (timeDiff >= 60 * 60 * 1000) {
                timeLeftStr = String.valueOf(timeDiff / 1000 / 60 / 60) + "h " + (timeDiff / 1000 / 60) % 60 + "min";
            }

            StringBuilder result = new StringBuilder("Auction Item #");
            result.append(id).append(": ").append(name).append("\n");
            result.append("Minimum bid: ").append(minBid).append("\n");
            result.append("Current bid: ").append(currentBid == null ? "none" : currentBid).append("\n");
            result.append("Start date: ").append(dF.format(startDate)).append("\n");
            result.append("Closing date: ").append(dF.format(closingDate)).append("\n");
            result.append("Time left: ").append(timeLeftStr);
            return result.toString();
        }
    }

}
