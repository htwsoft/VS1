package rmifs;

/**
 * VerwalterServer ist gleichzeitig Client und Server.
 * Zwischenstelle zwischen Client und FileServer.
 * @author cpatzek, soezdemir
 * @version 1.04
 * @date 2016-09-14
 */

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.text.SimpleDateFormat;
import java.util.Date;



public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory {

    private FSInterface fsserver;
    //public FileSystemClient client;//ToDo
    private String clientAddress = "not set yet";
    private String clientIP = "*unknown*";
    private String timeStamp = "not set yet"; //ToDo


    /**
     * Konstruktor, baut Verbindung zum lokalen FileServer auf
     * @throws RemoteException
     * @throws NotBoundException
     */
    public VerwalterServer(int port, String ip) throws RemoteException, NotBoundException
    {
        super();
        System.setProperty("java.security.policy", "policy/java.policy" );
        connectFileSystem(ip, port);
    }

    /**
     * Erstellt einen Socket fuer remote Verbindungen
     * (Funktion des Interface RMIClientSocketFactory)
     * @param host Adresse des Clients
     * @param port Port der Verbindung
     * @return Socket fuer den Client
     * @throws IOException
     */
    public Socket createSocket(String host, int port) throws IOException
    {
        return new Socket(host, port);
    }

    /**
     * //ToDo Funktion soll die Anbindung zu anderen VerwalterServer liefern
     * @return Name und IP-Adressen aller VerwalterServer
     */
    public String getServerList() throws RemoteException
    {
        log(" - Client [" + fsserver.getClientAddress() + "] request serverlist");
        return "\n VERWALTER_LISTE kommt";
    }

    /**
     * <br> Prueft ob eine Datei gefunden wurde und macht entsprechende Rueckgaben </br>
     * @param file Name der Datei
     * @param startDir Name des StartDirectories
     * @return Entweder die Angabe, dass keine Datei gefunden wurde, oder die Dateien die gefunden wurden
     *         und weitere Rueckgabe von Server Liste
     * @throws RemoteException
     */
    public boolean search(String file, String startDir) throws RemoteException
    {
        log(" - Client [" + clientIP + "] request search");
        return this.fsserver.search(file, startDir);
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
     * @throws RemoteException
     */
    public void sendClientAddress(String clientAddress) throws RemoteException
    {
        clientIP = clientAddress;
        log(" - Client [" + clientAddress + "] connected via RMI handshake");
        fsserver.sendClientAddress(clientAddress);
    }


    /** //ToDo timeStamp
     * ermittelt Systemzeit des Servers
     * @return timeStamp
     */
    private void setTime()
    {
        timeStamp = new SimpleDateFormat("yyyy-MM-dd / HH:mm:ss").format(new Date());
    }
    public String getTime()
    {
        return this.timeStamp;
    }

    public void log(String message) {
        setTime();
        System.out.println(getTime() + message);
    }


    private void connectFileSystem(String ip, int port)throws RemoteException, NotBoundException{

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry = LocateRegistry.getRegistry(ip, port);
        this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
    }

    public Path [] getFileList() throws RemoteException{
        return fsserver.getFileList();
    }


}
