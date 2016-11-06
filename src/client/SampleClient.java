package client;

/**
 * Created by justas on 04/11/16.
 */
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 * Created by justas on 03/11/16.
 */
public class SampleClient extends UnicastRemoteObject implements Runnable {

    private int port;
    private String host;

    protected SampleClient() throws RemoteException {
        super();
    }

    protected SampleClient(String host, int port) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
    }

    public static void main(String[] args) {
        String reg_host = "localhost";
        int reg_port = 1099;

        /*if (args.length == 1) {
            reg_port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            reg_host = args[0];
            reg_port = Integer.parseInt(args[1]);
        }*/

        try {
            for (int i = 0; i <5; i++) {
                new Thread(new SampleClient(reg_host, reg_port)).start();
            }
        } catch (RemoteException e) {
            System.err.println("Unable to run thread " + e);
            System.exit(1);
        }
    }

    //@Override
    public void callback(String s) throws RemoteException {
        System.out.println(s);
    }

    @Override
    public void run() {
        //RMISrvInterface rmiServer = (RMISrvInterface) Naming.lookup("rmi://"+host+":"+port+"/intSink");
        Random rg = new Random();
        int timer = rg.nextInt(5000);
        String name = Thread.currentThread().getName();
        System.out.format("%s - Requesting callback from server in %d seconds\n", name, timer/1000);
        //rmiServer.registerObject(this, name, timer);
    }
}
