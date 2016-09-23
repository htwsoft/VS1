package rmifs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * <br>HtwSoftVerwalter-Klasse zum starten eines VerwalterServers</br>
 * und zur Initialisierung einer RMI Verbindung fuer FileSystemClients
 * @author soezdemir
 * @version 1.03
 * @date 2016-09-16
 */
public class HtwSoftVerwalter {

    public final static String SYSTEM_HOST_IP   = "192.168.0.26";
    public final static int SYSTEM_PORT         = 4711;
    public final static int VERWALTER_PORT      = 4712;

    /**
     * SYSTEM-HOST-IP Adresse des lokalen FileServers
     */
    private final static String HOST = SYSTEM_HOST_IP;

    /**
     * PORT_NR des lokalen FileServers
     */
    private final static int PORT_NR = SYSTEM_PORT;

    /**
     * VPORT_NR des lokalen VerwalterServers
     */
    private final static int VPORT_NR = VERWALTER_PORT;


    public static void main(String args[])
    {
        init();
        start();
    }

    private static void init()
    {
        System.setProperty("policy/java.security.policy", "policy/java.policy" );
        System.setProperty("java.rmi.server.hostname", HOST);
    }


    //ToDo lookup fuer VerwalterServer & FileServer
    /**
     * Erzeugt VerwalterServer
     * Methode zur Initialisierung einer RMI Verbindung
    *  System.setProperty noetig fuer RMI Client Anbindung zum VerwalterServer
    *  UnicastRemoteObject stellt das Objekt dem Client zur Verfügung
     * LocateRegistry.createRegistry macht Objekte ansprechbar
     * registry.rebind zum binden an Registry
     **/
    private static void start()
    {

        try
        {
            VerwalterServer verwalterServer = new VerwalterServer(PORT_NR, HOST);

            VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, VPORT_NR);
            Registry registry       =  LocateRegistry.createRegistry(VPORT_NR);
            registry.rebind("VerwalterServer", stub);

            verwalterServer.log("\nServer bound ...\tPort open at " + ((PORT_NR)+1));
        }
        catch (RemoteException rex)
        {
            rex.printStackTrace();
        }
        catch (NotBoundException nbe)
        {
            nbe.printStackTrace();
        }
    }


}//ENDE
