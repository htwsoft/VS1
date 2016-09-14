//package src.rmifs;
package rmifs;

/**
 * VerwalterServer ist gleichzeitig Client und Server.
 * Zwischenstelle zwischen Client und FileServer.
 * @author cpatzek, soezdemir
 * @version 1.02
 * @date 2016-09-03
 */

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.net.InetAddress;
import java.net.UnknownHostException;



public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory {
    private FSInterface fsserver;
    //public FileSystemClient fsclient = new FileSystemClient();//ToDo
    /**
     * enthaelt die Liste aller verfuegbaren(remote) VerwalterServer
     * und indirekt deren verbundenen FileServer (Beispiel IP-Adressen)
     */
    private static final String VERWALTER_LISTE =   "Name: Server[1] IP: 192.168.0.101\n" +
                                                    "Name: Server[2] IP: 192.168.0.102\n" +
                                                    "Name: Server[3] IP: 192.168.0.103";
    /**
     * HOST entspricht der IP-Adresse des lokalen FileServers
     */
    private final static String HOST = null; //192.168.0.101

    /**
     * PORT_NR entspricht dem gebundenen Port des FileServers
     */
    private final static int PORT_NR = 4711;

    /**
     * Konstruktor, baut Verbindung zum lokalen FileServer auf
     * @throws RemoteException
     * @throws NotBoundException
     */
    public VerwalterServer() throws RemoteException, NotBoundException
    {
        super();
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry = LocateRegistry.getRegistry(HOST, PORT_NR);
        this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
    }

    /**
     * Erstellt einen Socket für remote Verbindungen(Funktion des Interface RMIClientSocketFactory)
     * @param host Adresse des Clients
     * @param port Port der Verbindung
     * @return Den Socket für den Client
     * @throws IOException
     */
    public Socket createSocket(String host, int port) throws IOException
    {
        return new Socket(host, port);
    }

    /**
     *
     * @return Name und IP-Adressen aller VerwalterServer
     */
    public String getServerList()
    {
        System.out.println("serverlist");
        return VERWALTER_LISTE;
    }

    public boolean rename(String oldName, String newName) throws RemoteException
    {
        return this.fsserver.rename(oldName, newName);
    }

    public String getOSName()throws RemoteException
    {   System.out.println("osname");
       return this.fsserver.getOSName();
    }

    public String getHostName() throws RemoteException
    {   System.out.println("hostname");
        return this.fsserver.getHostName();
    }

    public String getHostAddress() throws RemoteException
    {   System.out.println("hostaddress");
        return this.fsserver.getHostAddress();
    }
    /** wird erledigt durch soezdemir
     * Methoden sollen Informationen über
     * einen verbundenen Client zurückgeben
    //ToDo
    public String getClientOS() throws RemoteException
    {
        System.out.println("client osname");
        return this.fsclient.getClientOS();
    }*/

    public void setClientAddress(String clientAddress) throws RemoteException
    {
        System.out.println("clientaddress");
        fsserver.setClientAddress(clientAddress);
    }

    /** //ToDo
    public String getClientAddress() throws RemoteException
    {
        System.out.println("clientaddress");
        return this.fsclient.getClientAddress();
    }

    //ToDo
    public String getClientName() throws RemoteException
    {
        System.out.println("clientname");
        return this.fsclient.getClientName();
    }*/

    public boolean delete(String file) throws RemoteException
    {
        return this.fsserver.delete(file);
    }

    public boolean createFile(String file) throws RemoteException
    {
        return this.fsserver.createFile(file);
    }

    public boolean createDir(String dir) throws RemoteException
    {
        return this.fsserver.createDir(dir);
    }

    /**
     * Prüft ob eine Datei gefunden wurde und macht entsprechende Rückgaben
     * @param file Name der Datei
     * @param startDir Name des StartDirectories
     * @return Entweder die Angabe, dass keine Datei gefunden wurde, oder die Dateien die gefunden wurden
     *         und weitere Rueckgabe von Server Liste
     * @throws RemoteException
     */
    public String search(String file, String startDir) throws RemoteException
    {
        String erg = this.fsserver.search(file, startDir);
        if(erg.equals(""))
        {
            return ("Nicht gefunden, pruefen Sie andere Server!" + getServerList());
        }
        else
            return erg;
    }

    public String browseFiles(String dir) throws RemoteException
    {
        return this.fsserver.browseFiles(dir);
    }

    public String browseDirs(String dir) throws RemoteException
    {
        return this.fsserver.browseDirs(dir);
    }

    /**
     * Legt Port für Verbindung fest und baut Verbindung zum FileServer auf(siehe Konstruktor)
     * @param args IP und Port des VerwalterServers(Konstanten stattdessen verwenden?)
     */
    public static void main(String args[])
    {
        try
        {
            VerwalterServer verwalterServer = new VerwalterServer();
            if(args.length >= 1)
            {
                int serverPort = 0;//Clientaufruf mit 4711
                serverPort = Integer.parseInt(args[0]);
                //Noetig für RMI Client Anbindung zum VerwalterServer z.B. 192.168.0.101 Port 4711
                System.setProperty("java.rmi.server.hostname", "localhost");
                //Stellt das Objekt dem System zur Verfügung
                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, serverPort);
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry =  LocateRegistry.createRegistry(serverPort);
                //Objekt an registry binden
                registry.rebind("VerwalterServer", stub);
                System.out.println("Server bound ...\n Port open at " + serverPort);
            }
            else
            {
                System.out.println("Bitte Server-Port zum binden angeben!");
            }
        }
        catch(Exception e)
        {
            System.out.println( "Fehler: " + e.toString() );
        }
    }

}
