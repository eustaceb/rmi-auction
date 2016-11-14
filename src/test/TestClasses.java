package test;

import server.AuctionItem;
import server.Bid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by justas on 04/11/16.
 */
public class TestClasses {
    public static void main(String args[]) {
        AuctionItem auctionItem1 = new AuctionItem("Boxers", 5.0f, System.currentTimeMillis() + 55*1000);
        AuctionItem auctionItem2 = new AuctionItem("Boxers", 5.0f, System.currentTimeMillis() + 35*22*1000);
        AuctionItem auctionItem3 = new AuctionItem("Boxers", 5.0f, System.currentTimeMillis() + 50*44*60*1000);
        System.out.println(auctionItem1);
        System.out.println(auctionItem2);
        System.out.println(auctionItem3);

        ArrayList<String> names = new ArrayList<>(Arrays.asList("John", "Alex", "Bender", "Jess", "Chris", "Alberto", "Xin", "Jack", "Spencer", "Mark", "Lorenzo", "Peter", "Miranda"));
        for (String name : names) {
            // TODO: Replace null
            auctionItem1.makeBid(new Bid(null, new Random().nextFloat() * 100));
        }
        System.out.println(auctionItem1);
        System.out.println(auctionItem1.getBidListStr());
        //auctionItem1.makeBid()
    }
}
