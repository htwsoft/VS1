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
import java.util.*;


public class VerwalterServer implements VerwalterInterface, RMIClientSocketFactory
{
    private static final String FEHLER_VERBINDUNG_MESSAGE = "\nFehler!\n\tDie Verbindung zu einem der File-Server ist unterbrochen!\n" +
            "Die angezeigten Informationen sind moeglicherweise lueckenhaft!\nBitte versuchen Sie es spaeter noch einmal!\n";
    private static final String FEHLER_AKTUELLER_SERVER = "\nFehler!\n\tDie Verbindung zu dem Server auf dem Sie arbeiten wollen ist " +
            "unterbrochen! Bitte versuchen Sie es spaeter noch einmal!\n";
    private static final String FEHLER_ALLE_SERVER = "\nFehler! Alle File-Server sind aktuell nicht erreichbar!\n" +
            "Versuchen Sie es spaeter erneut!\n";
    private ArrayList<FileServerListenElement> fileServerListe = new ArrayList<>();
    //private HashMap<Integer, String> fileServers;
    private FSInterface fsserver;
    //public String[] fileServerNames = new String[10];
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
        /*fileServers = new HashMap<>();
        fileServers.put(6666, "192.168.0.26");
        fileServers.put(8888, "192.168.0.26");
        fileServers.put(startPort, startIp);
        */
        fileServerListe.add(new FileServerListenElement(null, startIp, startPort));
        fileServerListe.add(new FileServerListenElement(null, "192.168.0.24", 6666));
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
    public String getServerList() throws RemoteException, NotBoundException
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
        return iterateFileSystems(FUNKTIONALITAET.SEARCH, file, startDir);
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

    public String browseDirs(String dir, String server) throws RemoteException, NotBoundException {
        log(" - Client [" + clientIP + "] request browse");
        if (!connectServer(server))
        {
            return FEHLER_AKTUELLER_SERVER;
        }
        return performOperation(FUNKTIONALITAET.BROWSE_DIRS, dir, null);
    }

    public String delete(String file, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request delete");
        if (!connectServer(server))
        {
            return FEHLER_AKTUELLER_SERVER;
        }
        return performOperation(FUNKTIONALITAET.DELETE, null, file);
    }

    public String createFile(String file, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createFile");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.CREATE_FILE, null, file);
    }

    public String createDir(String dir, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createDir");
        if (!connectServer(server))
        {
            return FEHLER_AKTUELLER_SERVER;
        }
        return performOperation(FUNKTIONALITAET.CREATE_DIR, dir, null);
    }

    public String rename(String oldName, String newName, String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request rename");
        if (!connectServer(server))
        {
            return FEHLER_AKTUELLER_SERVER;
        }
        return performOperation(FUNKTIONALITAET.RENAME, oldName, newName);
    }

    public String getOSName(String server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request serverOSname");
        if (!connectServer(server))
        {
            return FEHLER_AKTUELLER_SERVER;
        }
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
        ListIterator<FileServerListenElement> iterator = fileServerListe.listIterator();
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try
        {
            while (iterator.hasNext())
            {
                FileServerListenElement tmp = iterator.next();
                Registry registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
                serverNames[i] = fsserver.getHostName();
                i++;
            }
            return serverNames;
        }
        catch(RemoteException rex)
        {
            serverNames[i] += FEHLER_AKTUELLER_SERVER+rex.getMessage();
            return serverNames;
        }
        catch(NotBoundException nex)
        {
            serverNames[i] += FEHLER_AKTUELLER_SERVER+nex.getMessage();
            return serverNames;
        }
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

    public String getClientAddress() throws RemoteException
    {
        return fsserver.getClientAddress();
    }

    /**
     * holt sich die IPv4 Adresse des verbundenen Clients
     * @throws RemoteException
     */
    public String sendClientAddress(String clientAddress) throws RemoteException, NotBoundException
    {
        clientIP = clientAddress;
        log(" - Client [" + clientAddress + "] connected via RMI handshake");
        try
        {
            fsserver.sendClientAddress(clientAddress);
        }
        catch(RemoteException rex)
        {
            return FEHLER_AKTUELLER_SERVER+rex.getMessage();
        }
        return "";
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

    public void log(String message)
    {
        setTime();
        System.out.println(getTime() + message);
    }

    private void connectFileSystem()throws RemoteException, NotBoundException
    {

        ListIterator<FileServerListenElement> iterator = fileServerListe.listIterator();
        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        FileServerListenElement tmp = iterator.next();
        Registry registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
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
    private String iterateFileSystems(FUNKTIONALITAET n, String dir, String file)
    {
        int i = 0;
        FileServerListenElement tmp;
        String ergebnis = "";
        String serverName = "";
        ListIterator<FileServerListenElement> iterator = fileServerListe.listIterator();
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        try
        {
            while (iterator.hasNext())
            {
                tmp = iterator.next();
                Registry registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
                switch (n)
                {
                    case BROWSE_FILES:
                        ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.browseFiles(dir);
                        break;
                    case BROWSE_DIRS:
                        serverName = fsserver.getHostName();
                        tmp.setServerName(serverName);
                        fileServerListe.set(i, tmp);
                        ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.browseDirs(dir);
                        break;
                    case SEARCH:
                        ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.search(dir, file);
                        break;
                }
                i++;
            }
        }
        catch(RemoteException rex)
        {
            String zwischenErgebnis = "";
            if(i<1)
            {
                zwischenErgebnis = handleIterateException(n, dir, file);
                if(!zwischenErgebnis.contains("Fehler"))
                    return FEHLER_VERBINDUNG_MESSAGE+zwischenErgebnis;

                else
                    return FEHLER_ALLE_SERVER;

            }
            else
                return ergebnis+"\n"+FEHLER_VERBINDUNG_MESSAGE+rex.getMessage();
        }
        catch(NotBoundException nex)
        {
            String zwischenErgebnis = "";
            if(i<1)
            {
                zwischenErgebnis = handleIterateException(n, dir, file);
                if(!zwischenErgebnis.contains("Fehler"))
                    return FEHLER_VERBINDUNG_MESSAGE+zwischenErgebnis;

                else
                    return FEHLER_ALLE_SERVER;

            }
            else
                return ergebnis+"\n"+FEHLER_VERBINDUNG_MESSAGE+nex.getMessage();
        }
        return ergebnis;
    }
    /**
     * <br> Ueberprueft ob der erste oder zweite Server der Liste nicht verbunden ist und reagiert entsprechend
     * darauf</br>
     * @param n gibt an welche Funktion der FileServer aufgerufen wird
     * @param dir Parameter fuer dirName/startDirName, abhaengig von n
     * @param file Parameter fuer fileName, abhaengig von n
     * @return gibt entweder an das alle Server nicht up sind oder das einer der beiden down ist und gibt das ergebnis
     *         dessen der up ist weiter
     */
    private String handleIterateException( FUNKTIONALITAET n, String dir, String file)
    {
        int i = 1;
        String ergebnis = "";
        String serverName;
        try
        {
            ListIterator<FileServerListenElement> iterator = fileServerListe.listIterator();
            FileServerListenElement tmp = iterator.next(); tmp = iterator.next();
            Registry registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
            this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            switch (n)
            {
                case BROWSE_FILES:
                    ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.browseFiles(dir);
                    break;
                case BROWSE_DIRS:
                    serverName = fsserver.getHostName();
                    tmp.setServerName(serverName);
                    fileServerListe.set(i, tmp);
                    ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.browseDirs(dir);
                    break;
                case SEARCH:
                    ergebnis += "\n" + tmp.serverName + ":\n" + fsserver.search(dir, file);
                    break;
            }
        }
        catch(RemoteException rex)
        {
            return FEHLER_ALLE_SERVER;
        }
        catch(NotBoundException nex)
        {
            return FEHLER_ALLE_SERVER;
        }
        return ergebnis;
    }
    /**
     * Verbindet den Verwalter zum geforderten FileServer, um anschließend dort eine Operation
     * durchzufuehren
     * @param server der Name des Servers auf dem die Operation durchgefuehrt werden soll
     */
    private boolean connectServer(String server)
    {
        System.out.println("connectServer(), Server: "+server);

        if (System.getSecurityManager() == null)
        {
            System.setSecurityManager(new SecurityManager());
        }
        Registry registry;
        ListIterator<FileServerListenElement> iterator = fileServerListe.listIterator();
        FileServerListenElement tmp = iterator.next();
        try
        {
            if (server.equals(tmp.serverName))
            {
                registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            }
            else if (server.equals(tmp.serverName))
            {
                tmp = iterator.next();
                registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            }
            else if (server.equals(tmp.serverName))
            {
                tmp = iterator.next();tmp = iterator.next();
                registry = LocateRegistry.getRegistry(tmp.serverIP, tmp.serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
            }
        }
        catch(RemoteException rex)
        {
            return false;
        }
        catch(NotBoundException nex)
        {
            return false;
        }
        return true;
    }
    /**
     * Fuehrt die angegebene Operation auf dem derzeit verbundenen FileServer durch
     * @param n gibt an welche Funktion der FileServer aufgerufen wird
     * @param dir Parameter fuer dirName/startDirName/oldName, abhaengig von n
     * @param file Parameter fuer fileName/newName, abhaengig von n
     * @return gibt den Ergebnis String fuer den Methoden Aufruf n zurueck
     */
    private String performOperation(FUNKTIONALITAET n, String dir, String file)
    {
        String ergebnis = "";
        try
        {
            switch (n)
            {
                case BROWSE_DIRS:
                    ergebnis += "\n" + "\n" + fsserver.browseDirs(dir);
                    break;
                case BROWSE_FILES:
                    ergebnis += "\n" + "\n" + fsserver.browseFiles(dir);
                    break;
                case CREATE_DIR:
                    ergebnis += "\n" + ":\n" + fsserver.createDir(dir);
                    break;
                case CREATE_FILE:
                    ergebnis += "\n" + ":\n" + fsserver.createFile(file);
                    break;
                case DELETE:
                    ergebnis += "\n" + ":\n" + fsserver.delete(file);
                    break;
                case RENAME:
                    ergebnis += "\n" + ":\n" + fsserver.rename(dir, file);
                    break;
                case GET_OS_NAME:
                    ergebnis += "\n\t\t" + ": " + fsserver.getOSName() + " ";
                    break;
                case GET_HOST_ADDRESS:
                    ergebnis += "\n\t\t" + ": " + fsserver.getHostAddress() + " ";
                    break;
                case GET_HOST_NAME:
                    ergebnis += "\n\t\t" + ": " + fsserver.getHostName() + " ";
                    break;
            }
        }
        catch(RemoteException rex)
        {
            return ergebnis+FEHLER_AKTUELLER_SERVER+rex.getMessage();
        }
        return ergebnis;
    }

    public Path [] getFileList() throws RemoteException
    {
        return fsserver.getFileList();
    }
}
