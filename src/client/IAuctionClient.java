package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAuctionClient extends Remote {
    /**
     * Gets client name
     * @return client name
     * @throws RemoteException
     */
    String getName() throws RemoteException;

    /**
     * Callback used for communication by the server
     * @param message
     * @throws RemoteException
     */
    void callback(String message) throws RemoteException;
}
