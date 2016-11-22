package test;

import client.ConnectionLayer;
import client.IAuctionClient;
import server.IAuctionServer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class AuctionClientWorker implements Runnable, IAuctionClient {

    private static ArrayList<String> itemNames = new ArrayList<>(Arrays.asList("Shoes made from potatoes",
            "Crocodile tears", "Burbonic plague", "Illegal substance", "Fake passport", "Dog hair",
            "That guy", "Depression", "Political immunity", "Curse", "Stolen bike", "Disappointment"));

    private ConnectionLayer connection;
    private String name;
    private long end;

    public AuctionClientWorker(ConnectionLayer connection, String name, long runtime) {
        this.connection = connection;
        this.name = name;
        this.end = System.currentTimeMillis() + runtime;
    }

    @Override
    public void run() {
        //System.out.println(this.name + " is running");
        Random rg = new Random();
        int timer = rg.nextInt(5000);
        while (System.currentTimeMillis() < end) {
            try {
                IAuctionServer srv = connection.getServer();
                int rndInt = rg.nextInt(itemNames.size());
                float rndFloat = rg.nextFloat() * 100;

                Set<Integer> openAuctionIds = srv.getOpenAuctionIds();
                if (openAuctionIds.size() < 40) {
                    srv.createAuctionItem(this, itemNames.get(rndInt) + name, rndFloat, rndInt * 60);
                } else {
                    int rndId = rg.nextInt(openAuctionIds.size());
                    srv.bid(this, rndId, rndFloat * 2);
                }
                Thread.sleep(100);
            } catch (RemoteException e) {
                System.err.println("Could not contact the server " + e);
                System.exit(1);
            } catch (InterruptedException e) {
                System.err.println("Unable to sleep thread - " + e);
            }
        }
    }

    @Override
    public String getName() throws RemoteException { return name; }

    @Override
    public void callback(String message) throws RemoteException {
        // Disable so console doesn't get flooded
        //System.out.println(name + " got message - " + message);
    }
}
