//package src.rmifs;
package rmifs;

/**
 * VerwalterServer ist gleichzeitig Client und Server.
 * Zwischenstelle zwischen Client und FileServer.
 * @author cpatzek & soezdemir
 * @version 1.04
 * @date 2016-09-14
 */

import java.lang.String;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.UnicastRemoteObject;



public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory {

    private final static String SERVER_HOST_IP_1 = "192.168.0.11";
    private final static String SERVER_HOST_IP_2 = "192.168.0.23";
    private final static String SERVER_HOST_IP_3 = "192.168.0.24";
    private final static String SERVER_HOST_FGVT = "172.19.1.209"; //localhost in fgvt


    private FSInterface fsserver;
    //public FileSystemClient client;//ToDo
    private String clientAddress = "not set yet";
    private String clientIP = "*unknown*";
    private String timeStamp = "not set yet"; //ToDo
    /**
     * enthaelt die Liste aller verfuegbaren(remote) VerwalterServer
     * und indirekt deren verbundenen FileServer (Beispiel IP-Adressen)
     */
    private static final String VERWALTER_LISTE =   "Name: BspServer[1] IP: 192.168.0.11\n" +
                                                    "Name: BspServer[2] IP: 192.168.0.23\n" +
                                                    "Name: BspServer[3] IP: 192.168.0.24";
    /**
     * HOST entspricht der IP-Adresse des lokalen FileServers
     */
    private final static String HOST = SERVER_HOST_IP_1; //192.168.0.11 //192.168.0.23 //192.168.0.24
                                                         //"172.19.1.209" fgvt
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
     * Erstellt einen Socket für remote Verbindungen
     * (Funktion des Interface RMIClientSocketFactory)
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
     * Legt Port für Verbindung fest und baut Verbindung zum FileServer auf(siehe Konstruktor)
     * @param args IP und Port des VerwalterServers //ToDo Konstanten stattdessen verwenden?
     */
    public static void main(String args[])
    {
        String timeStamp = ""; //ToDo timeStamp

        //**** regelt RMI Kommunikation ***** muss anfang der main bleiben
        System.setProperty("java.security.policy", "policy/java.policy" );

        try
        {
            VerwalterServer verwalterServer = new VerwalterServer();
            if(args.length >= 1)
            {
                int serverPort = 0;//Clientaufruf mit 4711
                serverPort = Integer.parseInt(args[0]);

                //Noetig für RMI Client Anbindung zum VerwalterServer z.B. 192.168.0.11 Port 4711
                System.setProperty("java.rmi.server.hostname", SERVER_HOST_IP_1); //"172.19.1.209" fgvt
                //Stellt das Objekt dem System zur Verfügung
                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, serverPort);
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry =  LocateRegistry.createRegistry(serverPort); //ToDo lookup für VerwalterServer & FileServer
                //Objekt an registry binden
                registry.rebind("VerwalterServer", stub);

                //Terminalausgaben am VerwalterServer
                verwalterServer.log(" : Systemzeit ");
                System.out.println("\nServer bound ...\tPort open at " + serverPort);
            }
            else
            {
                System.out.println("Bitte Server-Port zum binden angeben!");
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch (NotBoundException nbe)
        {
            nbe.printStackTrace();
        }

    }

    /**
     *
     * @return Name und IP-Adressen aller VerwalterServer
     */
    public String getServerList() throws RemoteException
    {
        log(" - Client [" + fsserver.getClientAddress() + "] request serverlist");
        return VERWALTER_LISTE;
    }

    /**
     * <br> Prüft ob eine Datei gefunden wurde und macht entsprechende Rückgaben </br>
     * @param file Name der Datei
     * @param startDir Name des StartDirectories
     * @return Entweder die Angabe, dass keine Datei gefunden wurde, oder die Dateien die gefunden wurden
     *         und weitere Rueckgabe von Server Liste
     * @throws RemoteException
     */
    public String search(String file, String startDir) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request search");
        String erg = this.fsserver.search(file, startDir);
        if(erg.equals(""))
        {
            return ("Nicht gefunden, pruefen Sie andere Server!\n" + getServerList());
        }
        else
            return erg;
    }

    public String browseFiles(String dir) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request browse");
        return this.fsserver.browseFiles(dir);
    }

    public String browseDirs(String dir) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request browseDir");
        return this.fsserver.browseDirs(dir);
    }

    public boolean delete(String file) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request delete");
        return this.fsserver.delete(file);
    }

    public boolean createFile(String file) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request createFile");
        return this.fsserver.createFile(file);
    }

    public boolean createDir(String dir) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request createDir");
        return this.fsserver.createDir(dir);
    }

    public boolean rename(String oldName, String newName) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request rename");
        return this.fsserver.rename(oldName, newName);
    }

    public String getOSName()throws RemoteException
    {
        log(" - Client [" + clientIP + "] request serverOSname");
        return this.fsserver.getOSName();
    }

    public String getHostName() throws RemoteException
    {
        log(" - Client [" + clientIP + "] request hostname");
        return this.fsserver.getHostName();
    }

    public String getHostAddress() throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request hostaddress");
        return this.fsserver.getHostAddress();
    }

    public String getClientAddress() throws RemoteException {
        return fsserver.getClientAddress();
    }

    /**
     * holt sich die IPv4 Adresse des verbundenen Clients
     */
    public void sendClientAddress(String clientAddress) throws RemoteException
    {

        clientIP = clientAddress;
        log(" - Client [" + clientAddress + "] connected via RMI handshake");
        fsserver.sendClientAddress(clientAddress);
    }


    /** //ToDo timeStamp @soezdemir
     * ermittelt Systemzeit des Servers
     * @return timeStamp
     */
    private void setTime()
    {
        timeStamp = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss").format(new Date());
        this.timeStamp = timeStamp;
        //SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        //Timestamp time = new Timestamp(System.currentTimeMillis());
        //return String time = sdf.format(time);
    }
    public String getTime()
    {
        return this.timeStamp;
    }

    private void log(String message) {
        setTime();
        System.out.println(getTime() + message);
    }
}
