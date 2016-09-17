package rmifs;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by soez on 17.09.16.
 */
public class HtwSoftServer {

    private final static String SERVER_HOST_IP = "192.168.0.24";
    private final static int SERVER_PORT_NR = 4711;

    public static void main(String args [])
    {
        init();
        start();
    }

    private static void init()
    {
        System.setProperty("java.security.policy", "java.policy" );
        System.setProperty("java.rmi.server.hostname", SERVER_HOST_IP);
    }

    private static void start()
    {
        try {
            //Security Manager ermöglicht/regelt zugriff auf Klasse
            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }
            FileSystemServer fsServer = new FileSystemServer();
            //Registry erstellen um Objekt ansprechen zu können
            Registry registry = LocateRegistry.createRegistry(SERVER_PORT_NR);
            //Stellt das Objekt dem System zur Verfügung
            FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsServer, SERVER_PORT_NR);

            //Objekt an registry binden
            registry.rebind("FileSystemServer", stub);
            System.out.println("Server bound ...\nPort now open at " + SERVER_PORT_NR);
            System.out.print("\nServer Name: " + fsServer.getHostName()
                    + "\nServer IP: " + fsServer.getHostAddress()
                    + "\nServer runs on " + fsServer.getOSName());
        }
        catch (RemoteException rex)
        {
            rex.printStackTrace();
        }
    }
}
