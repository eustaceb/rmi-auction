package server;

import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AuctionServlet {

    public static void main(String args[]) {
        String host = "localhost";
        int port = 1099;

        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        try {
            LocateRegistry.createRegistry(port);
            IAuctionServer auction = new AuctionServerImpl();
            Registry reg = LocateRegistry.getRegistry(host, port);
            reg.rebind("auction", auction);
            System.out.println("Server ready.");
        }
        catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }
}