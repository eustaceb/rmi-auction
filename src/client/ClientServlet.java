package client;

import server.IAuctionServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;

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

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            IAuctionServer auctionSrv = (IAuctionServer) Naming.lookup("rmi://"+host+":"+port+"/auction");
            FailureDetector failureDetector = new FailureDetector(auctionSrv, 5000); // probe every 5s
            
            System.out.print("What is your username? ");
            AuctionClient client = new AuctionClient(br.readLine());
            System.out.println("Choose an option");
            System.out.println("l - List items");
            System.out.println("n - New listing");
            System.out.println("b - Bid");
            System.out.println("h - History");
            System.out.println("t - Check server load (average turnaround in ms)");
            System.out.println("q - Quit");

            boolean end = false;
            while (!end) {
                String response = "";
                switch (br.readLine().toLowerCase()) {
                    case "l":
                        response = auctionSrv.getOpenAuctions();
                        break;
                    case "n":
                        try {
                            System.out.print("Item name: ");
                            String name = br.readLine();
                            if (name.equals("")) throw new NumberFormatException();
                            System.out.print("Starting price: ");
                            float startPrice = Float.valueOf(br.readLine());
                            System.out.print("End auction in x seconds: ");
                            long endTime = Long.valueOf(br.readLine());
                            response = auctionSrv.createAuctionItem(client, name, startPrice, endTime);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Incorrect input format. Please try again.");
                        }
                        break;
                    case "b":
                        try {
                            System.out.print("Acution item ID: ");
                            int auctionItemId = Integer.valueOf(br.readLine());
                            System.out.print("Amount: ");
                            float bidAmount = Float.valueOf(br.readLine());
                            response = auctionSrv.bid(client, auctionItemId, bidAmount);
                        } catch (NumberFormatException nfe) {
                            System.err.println("Incorrect input format. Please try again.");
                        }
                        break;
                    case "h":
                        response = auctionSrv.getClosedAuctions();
                        break;
                    case "t":
                        response = "Average turnaround - " + failureDetector.determineLoad() + "ms";
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
