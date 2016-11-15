package client;

import java.rmi.Remote;
import java.util.Observer;

/**
 * Auction Client Data Model
 */
// REPORT: Thought about having this as an observer to observe AuctionItem but that lets clients modify items.
public interface IAuctionClient extends Remote{

    public void callback(String message);

}
