package client;

/**
 * Created by justas on 04/11/16.
 */
import server.IAuctionServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by justas on 03/11/16.
 */
public class AuctionClient {

    private int port;
    private String host;

    protected AuctionClient() throws RemoteException {
        super();
    }

    protected AuctionClient(String host, int port) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        System.out.println("Enter t to test the system with some AuctionClientWorkers. Otherwise, press enter");
        //TODO: Continue where I left off - System.in.read()
        try {
            IAuctionServer auctionSrv = (IAuctionServer) Naming.lookup("rmi://"+host+":"+port+"/auction");
            for (int i = 0; i <5; i++) {
                new Thread(new AuctionClientWorker(auctionSrv)).start();
            }
        } catch (RemoteException e) {
            System.err.println("Unable to connect to server " + e);
            System.exit(1);
        } catch (MalformedURLException e) {
            System.err.println("Malformed url " + e);
            System.exit(1);
        } catch (NotBoundException e) {
            System.err.println("Unable to bind" + e);
            System.exit(1);
        }
    }

}
