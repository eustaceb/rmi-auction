package server;

import client.IAuctionClient;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by justas on 04/11/16.
 */
public class Bid implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idCounter = 0;
    private int id;

    private final IAuctionClient owner;
    private final float amount;
    private final long timestamp;

    public Bid(IAuctionClient owner, float amount) {
        this.owner = owner;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.id = idCounter;
        idCounter++;
    }

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
        StringBuilder sb = new StringBuilder("Bid ID:");
        sb.append(id).append(" Amount:").append(amount).append(" - by ");
        sb.append(owner).append(" @ ").append(dF.format(timestamp));
        return sb.toString();
    }
}
