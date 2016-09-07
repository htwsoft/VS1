package rmifs;

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

/**
 * Created by Christian Patzek on 03.09.2016.
 * VerwalterServer ist gleichzeitig Client und Server. Zwischenstelle zwischen Client und FileServer.
 */
public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory {
    private FSInterface fsserver;
    /**
     * enthaelt die Liste aller verfuegbaren(remote) VerwalterServer und indirekt deren verbundenen FileServer
     */
    private static final String VERWALTER_LISTE = "Name: Server1 IP: xxx.xxx.xxx.xxx\n" +
                                                    "Name: Server2 IP: yyy.yyy.yyy.yyy\n" +
                                                    "Name: Server3 IP: zzz.zzz.zzz.zzz";
    /**
     * HOST entspricht der IP-Adresse des lokalen FileServers
     */
    private final static String HOST= null;

    /**
     * PORT_NR entspricht dem gebundenen Port des FileServers
     */
    private final static int PORT_NR = 4710;

    /**
     * Konstruktor, baut Verbindung zum lokalen FileServer auf
     * @throws RemoteException
     * @throws NotBoundException
     */
    private VerwalterServer() throws RemoteException, NotBoundException
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
    //ToDoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
    public String getHostName() throws RemoteException
    {
        return this.fsserver.getHostName();
    }

    public String getHostAdress() throws RemoteException{
        return this.fsserver.getHostAdress();
    }

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
        return fsserver.createDir(dir);
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
                System.setProperty("java.rmi.server.hostname","192.168.0.23"); //nötig für rmi client verbindung zum verwalter!!!!
                //Stellt das Objekt dem System zur Verfügung
                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, serverPort);
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry =  LocateRegistry.createRegistry(serverPort);
                //Objekt an registry binden
                registry.rebind("VerwalterServer", stub);
                System.out.println("Server bound ...");
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
