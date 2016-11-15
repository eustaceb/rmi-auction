package client;

import server.IAuctionServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Created by justas on 14/11/16.
 */
public class ClientServlet {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 1099;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        AuctionClient client = null;
        try {
            client = new AuctionClient(host, port);
        } catch (RemoteException e) {
            System.err.println("Unable to init client " + e);
            System.exit(1);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            IAuctionServer auctionSrv = (IAuctionServer) Naming.lookup("rmi://"+host+":"+port+"/auction");

            System.out.println("What is your username?");
            client.setName(br.readLine());
            boolean end = false;
            while (!end) {
                System.out.println("Choose an option");
                System.out.println("l - List items");
                System.out.println("n - New listing");
                System.out.println("b - Bid");
                System.out.println("t - Test system with some AuctionClientWorkers");
                System.out.println("q - Quit");
                String response = "";
                switch (br.readLine().toLowerCase()) {
                    case "l":
                        response = auctionSrv.getOpenAuctions();
                        break;
                    case "n":
                        System.out.print("Item name: ");
                        String name = br.readLine();
                        System.out.print("\nStarting price: ");
                        float startPrice = Float.valueOf(br.readLine());
                        System.out.print("\nEnd auction in x seconds: ");
                        long endTime = Long.valueOf(br.readLine());
                        response = auctionSrv.createAuctionItem(client, name, startPrice, endTime);
                        break;
                    case "b":
                        System.out.print("Acution item ID: ");
                        int auctionItemId = Integer.valueOf(br.readLine());
                        System.out.print("\nAmount: ");
                        float bidAmount = Float.valueOf(br.readLine());
                        response = auctionSrv.bid(client, auctionItemId, bidAmount);
                        break;
                    case "t":
                        System.out.print("How many? ");
                        int noOfWorkers = Integer.valueOf(br.readLine());
                        for (int i = 0; i < noOfWorkers; i++) {
                            new Thread(new AuctionClientWorker(auctionSrv)).start();
                        }
                        break;
                    case "q":
                        end = true;
                        break;
                    default:
                        break;
                }
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Unable to parse your input " + e);
            System.exit(2);
        } catch (NotBoundException e) {
            System.err.println("Unable to access the server object " + e);
            System.exit(3);
        }
    }
}
