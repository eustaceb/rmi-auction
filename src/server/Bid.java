package server;

import client.IAuctionClient;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by justas on 04/11/16.
 */
public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;

    private final IAuctionClient owner;
    private final String ownerName;
    private final float amount;
    private final long timestamp;

    public Bid(IAuctionClient owner, String ownerName, float amount) {
        this.owner = owner;
        // REPORT: Discrepancy between owner/ownerName
        this.ownerName = ownerName;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
    }

    public String getOwnerName() { return ownerName; }
    public IAuctionClient getOwner() {
        return owner;
    }

    public float getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        SimpleDateFormat dF = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder("Amount - ");
        sb.append(amount).append(" @ ").append(dF.format(timestamp));
        return sb.toString();
    }
}
