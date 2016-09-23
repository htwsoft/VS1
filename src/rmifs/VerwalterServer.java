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
    public String[] fileServerNames = new String[10];
    private String clientIP = "*unknown*";
    private String timeStamp = "not set yet"; //ToDo String server
    private enum FUNKTIONALITAET{BROWSE_FILES, BROWSE_DIRS, SEARCH, CREATE_DIR, CREATE_FILE, DELETE,
        RENAME, GET_OS_NAME, GET_HOST_NAME, GET_HOST_ADDRESS}

    /**
     * Konstruktor, baut Verbindung zum lokalen(bzw. remote) FileServer auf
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
        fileServers = new HashMap<>();
        fileServers.put(6666, "192.168.0.26");
        fileServers.put(8888, "192.168.0.26");
        fileServers.put(startPort, startIp);
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
        return iterateFileSystems(FUNKTIONALITAET.SEARCH, startDir, file);
    }

    public String initialBrowseDirs(String dir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request initial browse");
        return iterateFileSystems(FUNKTIONALITAET.BROWSE_DIRS, dir, null);
    }
    public String initialBrowseFiles(String dir) throws RemoteException, NotBoundException
    {
        return iterateFileSystems(FUNKTIONALITAET.BROWSE_FILES, dir, null);
    }
    public String browseFiles(String dir, String server) throws RemoteException, NotBoundException
    {
        return performOperation(FUNKTIONALITAET.BROWSE_FILES, dir, null);
    }

    public String browseDirs(String dir, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request browse");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.BROWSE_DIRS, dir, null);
    }

    public boolean delete(String file, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request delete");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.DELETE, null, file).contains("true");
    }

    public boolean createFile(String file, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createFile");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.CREATE_FILE, null, file).contains("true");
    }

    public boolean createDir(String dir, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createDir");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.CREATE_DIR, dir, null).contains("true");
    }

    public boolean rename(String oldName, String newName, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request rename");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.RENAME, oldName, newName).contains("true");
    }

    public String getOSName(String server)throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request serverOSname");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.GET_OS_NAME, null, null);
    }

    /**
     * <br>Fragt nach allen Namen der FileServer, damit Client diese identifizieren kann</br>
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     */
    public String[] getAllHosts() throws RemoteException, NotBoundException
    {
        String[] serverNames = new String[10];
        int i = 0;
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
            serverNames[i] = fsserver.getHostName();
            i++;
        }
        return serverNames;
    }

    public String getHostName(String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request hostname");
        return performOperation(FUNKTIONALITAET.GET_HOST_NAME, null, null);
    }

    public String getHostAddress(String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request hostaddress");
        return performOperation(FUNKTIONALITAET.GET_HOST_ADDRESS, null, null);
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
     * @param dir Parameter fuer dirName/startDirName, abhaengig von n
     * @param file Parameter fuer fileName, abhaengig von n
     * @return gibt den Ergebnis String fuer den Methoden Aufruf n zurueck(alle Informationen aller Server sind hier enthalten!)
     * @throws RemoteException
     * @throws NotBoundException
     */
    private String iterateFileSystems(FUNKTIONALITAET n, String dir, String file)throws RemoteException, NotBoundException
    {
        int i = 0;
        String ergebnis="";
        String serverName="";
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
                case BROWSE_FILES:
                    ergebnis += "\n"+ fileServerNames[i]+":\n\t\t"+fsserver.browseFiles(dir);break;
                case BROWSE_DIRS:
                    serverName = fsserver.getHostName();
                    fileServerNames[i] = serverName;
                    ergebnis += "\n"+ fileServerNames[i]+":\n\t\t"+fsserver.browseDirs(dir);break;
                case SEARCH:
                    ergebnis += "\n"+ fileServerNames[i]+":\n\t\t"+fsserver.search(file, dir);break;
            }
            i++;
        }
        return ergebnis;
    }
    /**
     * Verbindet den Verwalter zum geforderten FileServer, um anschließend dort eine Operation
     * durchzufuehren
     * @param server der Name des Servers auf dem die Operation durchgefuehrt werden soll
     */
    private void connectServer(String server) throws RemoteException, NotBoundException
    {
        System.out.println("connectServer(), Server: "+server);
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry;
        Set set = fileServers.entrySet();
        Iterator iterator = set.iterator();
        Map.Entry mentry = (Map.Entry)iterator.next();
        switch(server)
        {
            case "Server1":
                registry = LocateRegistry.getRegistry((String)mentry.getValue(),(Integer)mentry.getKey());
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");break;
            case "Server2":
                mentry = (Map.Entry)iterator.next();
                registry = LocateRegistry.getRegistry((String)mentry.getValue(),(Integer)mentry.getKey());
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");break;
            case "Server3":
                mentry = (Map.Entry)iterator.next(); mentry = (Map.Entry)iterator.next();
                registry = LocateRegistry.getRegistry((String)mentry.getValue(),(Integer)mentry.getKey());
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");break;
        }
    }
    /**
     * Fuehrt die angegebene Operation auf dem derzeit verbundenen FileServer durch
     * @param n gibt an welche Funktion der FileServer aufgerufen wird
     * @param dir Parameter fuer dirName/startDirName/oldName, abhaengig von n
     * @param file Parameter fuer fileName/newName, abhaengig von n
     * @return gibt den Ergebnis String fuer den Methoden Aufruf n zurueck
     */
    private String performOperation(FUNKTIONALITAET n, String dir, String file)throws RemoteException, NotBoundException
    {
        String ergebnis = "";
        switch(n)
        {
            case BROWSE_DIRS:
                ergebnis += "\n"+"\n"+fsserver.browseDirs(dir);break;
            case BROWSE_FILES:
                ergebnis += "\n"+"\n"+fsserver.browseFiles(dir);break;
            case CREATE_DIR:
                ergebnis += "\n"+":\n"+fsserver.createDir(dir);break;
            case CREATE_FILE:
                ergebnis += "\n"+":\n"+fsserver.createFile(file);break;
            case DELETE:
                ergebnis += "\n"+":\n"+fsserver.delete(file);break;
            case RENAME:
                ergebnis += "\n"+":\n"+fsserver.rename(dir, file);break;
            case GET_OS_NAME:
                ergebnis += "\n\t\t"+": "+fsserver.getOSName()+" ";break;
            case GET_HOST_ADDRESS:
                ergebnis += "\n\t\t"+": "+fsserver.getHostAddress()+" ";break;
            case GET_HOST_NAME:
                ergebnis += "\n\t\t"+": "+fsserver.getHostName()+" ";break;
        }
        return ergebnis;
    }

    public Path [] getFileList() throws RemoteException{
        return fsserver.getFileList();
    }
}
