package server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by justas on 04/11/16.
 */
public class AuctionItem implements Serializable {
    private static final long serialVersionUID = 1L;

    protected enum Codes {
        AUCTION_CLOSED(-1, "The auction is closed"),
        LOW_BID(0, "The bid is too low"),
        SUCCESS_BID(1, "The bid was successful.");
        public final int ID;
        public final String MESSAGE;
        Codes(int id, String msg) { this.ID = id; this.MESSAGE = msg; }
        @Override
        public String toString() {
            return "Error " + ID + ": " + MESSAGE;
        }
    }

    private static int idCounter = 0;
    private int id;

    private LinkedList<Bid> bids;
    private String name;
    private float minBid;
    private Date startDate, closingDate;

    public AuctionItem(String name, float minBid, long closingTime) {
        this.startDate = new Date(System.currentTimeMillis());
        this.closingDate = new Date(closingTime);
        this.id = idCounter;
        idCounter += 1;
        this.name = name;
        this.bids = new LinkedList<Bid>();
        this.minBid = minBid;
    }

    public synchronized int makeBid(Bid b) {
        if (closingDate.getTime() - startDate.getTime() < 0) {
            return Codes.AUCTION_CLOSED.ID;
        } else if (getCurrentBid() != null && b.getAmount() <= getCurrentBid().getAmount()) {
            return Codes.LOW_BID.ID;
        } else if (b.getAmount() <= minBid) {
            return Codes.LOW_BID.ID;
        }
        bids.push(b);
        return Codes.SUCCESS_BID.ID;
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
            } else {
                timeLeftStr = String.valueOf(timeDiff / 1000 / 60 / 60) + "h " + (timeDiff / 1000 / 60) % 60 + "min";
            }

            StringBuilder result = new StringBuilder("Auction Item #");
            result.append(id).append(": ").append(name).append("\n");
            result.append("Minimum bid: ").append(minBid).append("\n");
            result.append("Current bid: ").append(currentBid == null ? "none" : currentBid).append("\n");
            result.append("Start date: ").append(dF.format(startDate)).append("\n");
            result.append("Closing date: ").append(dF.format(closingDate)).append("\n");
            result.append("Time left: ").append(timeLeftStr).append("\n");
            return result.toString();
        }
    }

}
