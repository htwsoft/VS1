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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;



public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory {

    private HashMap<Integer, String> fileServers;
    private FSInterface fsserver;
    private String clientIP = "*unknown*";
    private String timeStamp = "not set yet"; //ToDo
    private enum FUNKTIONALITAET{BROWSE_FILES, BROWSE_DIRS, SEARCH, CREATE_DIR, CREATE_FILE, DELETE,
        RENAME, GET_OS_NAME };

    /**
     * Konstruktor, baut Verbindung zum lokalen FileServer auf
     * @throws RemoteException
     * @throws NotBoundException
     */
    public VerwalterServer(int port, String ip) throws RemoteException, NotBoundException
    {
        System.setProperty("java.security.policy", "policy/java.policy" );
        fileServersInit(port, ip);
        connectFileSystem();
    }

    /**
     * Legt die Informationen der FileServer an, zu denen der Verwalter sich verbinden wird,
     * um Anfragen zu bearbeiten
     */
    private void fileServersInit(int startPort, String startIp)
    {
        fileServers = new HashMap<Integer, String>();
        fileServers.put(startPort, startIp);
        fileServers.put(6666, "192.168.0.26");
        fileServers.put(8888, "192.168.0.26");
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
    public String search(String file, String startDir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request search");
        return iterateFileSystems(4, startDir, file);
    }

    public String browseFiles(String dir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request browse");
        return iterateFileSystems(2, dir, null);
    }

    public String browseDirs(String dir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request browseDir");
        return iterateFileSystems(3, dir, null);
    }

    public String delete(String file) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request delete");
        return iterateFileSystems(7, null,  file);
    }

    public String createFile(String file) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createFile");
        return iterateFileSystems(6, null, file);
    }

    public String createDir(String dir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createDir");
        return iterateFileSystems(5, dir, null);
    }

    public String rename(String oldName, String newName) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request rename");
        return iterateFileSystems(8, oldName, newName);
    }

    public String getOSName()throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request serverOSname");
        return iterateFileSystems(9, null, null);
    }

    public String getHostName() throws RemoteException, NotBoundException
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

    private void connectFileSystem()throws RemoteException, NotBoundException
    {
        Set set = fileServers.entrySet();
        Iterator iterator = set.iterator();
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        Map.Entry mentry = (Map.Entry)iterator.next();
        Registry registry = LocateRegistry.getRegistry((String)mentry.getValue(), (Integer)mentry.getKey());
        this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
    }

    /**
     * <br> Verbindet abwechselnd zu jedem FileServer und fordert die benoetigten Informationen an</br>
     * @param n gibt an welche Funktion der FileServer aufgerufen wird
     * @param dir Parameter fuer dirName/startDirName/oldName, abhaengig von n
     * @param file Parameter fuer fileName/newName, abhaengig von n
     * @return gibt den Ergebnis String fuer den Methoden Aufruf n zurueck(alle Informationen aller Server sind hier enthalten!)
     * @throws RemoteException
     * @throws NotBoundException
     */
    private String iterateFileSystems(int n, String dir, String file)throws RemoteException, NotBoundException
    {

        String ergebnis="";
        Set set = fileServers.entrySet();
        Iterator iterator = set.iterator();
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        while(iterator.hasNext())
        {
            Map.Entry mentry = (Map.Entry)iterator.next();
            Registry registry = LocateRegistry.getRegistry((String)mentry.getValue(), (Integer)mentry.getKey());
            this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            switch(n)
            {
                case 2:
                    ergebnis += this.fsserver.browseFiles(dir);break;
                case 3:
                    ergebnis += this.fsserver.browseDirs(dir);break;
                case 4:
                    ergebnis += this.fsserver.search(file, dir);break;
                case 5:
                    ergebnis += this.fsserver.createDir(dir);break;
                case 6:
                    ergebnis += this.fsserver.createFile(file);break;
                case 7:
                    ergebnis += this.fsserver.delete(file);break;
                case 8:
                    ergebnis += this.fsserver.rename(dir, file);break;
                case 9:
                    ergebnis += this.fsserver.getOSName()+" ";break;
            }
        }
        return ergebnis;
    }

    public Path [] getFileList() throws RemoteException{
        return fsserver.getFileList();
    }
}
