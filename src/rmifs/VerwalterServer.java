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
    private static final int ARRAY_GRENZE = 100;
    private ArrayList<String> verwalterNames = new ArrayList<>();
    private ArrayList<String> serverNames = new ArrayList<>();
    private ArrayList<FileServerListenElement> fileServerListe = new ArrayList<>();
    //private HashMap<Integer, String> fileServers;
    private FSInterface fsserver;
    private ArrayList<FileServerListenElement> verwalterListe= new ArrayList<>();
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
        connect(0);
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
        verwalterListe.add(new FileServerListenElement("RemoteVerwalter1", "192.168.0.26", 4713));
        verwalterListe.add(new FileServerListenElement("RemoteVerwalter2", "192.168.0.26", 4714));
        //verwalterListe.add(new FileServerListenElement("Verwalter2", "192.168.0.24", HtwSoftVerwalter.VERWALTER_PORT));
        fileServerListe.add(new FileServerListenElement(null, startIp, startPort));
        fileServerListe.add(new FileServerListenElement(null, "192.168.0.26", 1111));
        fileServerListe.add(new FileServerListenElement(null, "192.168.0.26", 2222));
        fileServerListe.add(new FileServerListenElement(null, "192.168.0.26", 3333));
        fileServerListe.add(new FileServerListenElement(null, "192.168.0.26", 4444));
        for(int i = 0; i < ARRAY_GRENZE; i++)
        {
            serverNames.add("default"+i);
            verwalterNames.add("default"+i);
        }

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
        String ergebnis = "";
        return iterateFileSystems(FUNKTIONALITAET.SEARCH, file, startDir, 0, ergebnis);
    }
    public String initialBrowseDirs(String dir) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request initial browse");
        String ergebnis = "";
        return iterateFileSystems(FUNKTIONALITAET.BROWSE_DIRS, dir, null, 0, ergebnis);
    }
    public String initialBrowseFiles(String dir) throws RemoteException, NotBoundException
    {
        String ergebnis = "";
        return iterateFileSystems(FUNKTIONALITAET.BROWSE_FILES, dir, null, 0, ergebnis);
    }
    public String browseFiles(String dir, int server) throws RemoteException, NotBoundException
    {
        return performOperation(FUNKTIONALITAET.BROWSE_FILES, dir, null);
    }

    public String browseDirs(String dir, int server) throws RemoteException, NotBoundException {
        log(" - Client [" + clientIP + "] request browse");
        if (!connectServer(server))
            return FEHLER_AKTUELLER_SERVER;
        return performOperation(FUNKTIONALITAET.BROWSE_DIRS, dir, null);
    }

    public String delete(String file, int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request delete");
        if (!connectServer(server))
            return FEHLER_AKTUELLER_SERVER;
        return performOperation(FUNKTIONALITAET.DELETE, null, file);
    }

    public String createFile(String file, int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createFile");
        connectServer(server);
        return performOperation(FUNKTIONALITAET.CREATE_FILE, null, file);
    }

    public String createDir(String dir, int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request createDir");
        if (!connectServer(server))
            return FEHLER_AKTUELLER_SERVER;
        return performOperation(FUNKTIONALITAET.CREATE_DIR, dir, null);
    }

    public String rename(String oldName, String newName, int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request rename");
        if (!connectServer(server))
            return FEHLER_AKTUELLER_SERVER;
        return performOperation(FUNKTIONALITAET.RENAME, oldName, newName);
    }

    public String getOSName(int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request serverOSname");
        if (!connectServer(server))
            return FEHLER_AKTUELLER_SERVER;
        return performOperation(FUNKTIONALITAET.GET_OS_NAME, null, null);
    }

    /**
     * <br>Fragt nach allen Namen der Verwalter, damit der Client diese identifizieren kann</br>
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     * @param index
     */
    public ArrayList<String> getAllVerwalterNames(int index) throws RemoteException, NotBoundException
    {
        while(index < verwalterListe.size())
        {
            verwalterNames.set(index, verwalterListe.get(index).getServerName());
            index++;
        }
        return verwalterNames;
    }

    /**
     * <br> Uebergibt die Verbindungsinformation des angeforderten Verwalters
     * @param verwalter angeforderter Verwalter
     * @return alle Verbindungsinformationen des angeforderten Verwalters(IP,NAME,PORT)
     * @throws RemoteException
     * @throws NotBoundException
     */
    public FileServerListenElement getVerwalter(int verwalter) throws RemoteException, NotBoundException
    {
        System.out.println("Angeforderter Verwalter:\n IP: "+verwalterListe.get(verwalter).getServerIP()+
                    "\t Port: "+verwalterListe.get(verwalter).getServerPort());
        return verwalterListe.get(verwalter);
    }
    /**
     * <br>Fragt nach allen Namen der FileServer, damit der Client diese identifizieren kann</br>
     * @return
     * @throws RemoteException
     * @throws NotBoundException
     */
    public ArrayList<String> getAllFileServerNames(int index) throws RemoteException, NotBoundException
    {
        try
        {
            while (index < fileServerListe.size())
            {
                connect(index);
                serverNames.set(index, fsserver.getHostName()+"(online)");
                index++;
            }
        }
        catch(RemoteException rex)
        {
            serverNames.set(index, fileServerListe.get(index).getServerName()+"(offline)");
            getAllFileServerNames(index+1);
        }
        catch(NotBoundException nex)
        {
            serverNames.set(index, fsserver.getHostName()+"(offline)");
            getAllFileServerNames(index+1);
        }
        return serverNames;
    }

    public String getHostName(int server) throws RemoteException, NotBoundException
    {
        log(" - Client [" + clientIP + "] request hostname");
        return performOperation(FUNKTIONALITAET.GET_HOST_NAME, null, null);
    }

    public String getHostAddress(int server) throws RemoteException, NotBoundException
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
        try{
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

    /**
     * <br> Verbindet abwechselnd zu jedem FileServer und fordert die benoetigten Informationen an</br>
     * @param n gibt an welche Funktion der FileServer aufgerufen wird
     * @param dir Parameter fuer dirName/startDirName, abhaengig von n
     * @param file Parameter fuer fileName, abhaengig von n
     * @return gibt den Ergebnis String fuer den Methoden Aufruf n zurueck(alle Informationen aller Server sind hier enthalten!)
     * @throws RemoteException
     * @throws NotBoundException
     */
    private String iterateFileSystems(FUNKTIONALITAET n, String dir, String file, int index, String ergebnis)
    {
        String serverName;
        try
        {
            while (index < fileServerListe.size())
            {
                connect(index);
                switch (n)
                {
                    case BROWSE_FILES:
                        ergebnis += "\n" + fileServerListe.get(index).getServerName() + ":\n" + fsserver.browseFiles(dir);
                        break;
                    case BROWSE_DIRS:
                        serverName = fsserver.getHostName();
                        fileServerListe.get(index).setServerName(serverName);
                        ergebnis += "\n" + fileServerListe.get(index).getServerName() + ":\n" + fsserver.browseDirs(dir);
                        break;
                    case SEARCH:
                        ergebnis += "\n" + fileServerListe.get(index).getServerName() + ":\n" + fsserver.search(dir, file);
                        break;
                }
                index++;
            }
        }
        catch(RemoteException rex)
        {

            ergebnis += "\n" + fileServerListe.get(index).getServerName()+": "+FEHLER_VERBINDUNG_MESSAGE;
            iterateFileSystems(n, dir, file, index, ergebnis);
        }
        catch(NotBoundException nex)
        {
            ergebnis += "\n" + fileServerListe.get(index).getServerName()+": "+FEHLER_VERBINDUNG_MESSAGE;
            iterateFileSystems(n, dir, file, index, ergebnis);
        }
        return ergebnis;
    }

    /**
     * Verbindet den Verwalter zum geforderten FileServer, um anschließend dort eine Operation
     * durchzufuehren
     * @param server der Name des Servers auf dem die Operation durchgefuehrt werden soll
     */
    private boolean connectServer(int server)
    {
        boolean isConnected = false;
        System.out.println("connectServer(), Server: "+fileServerListe.get(server).getServerName());
        try
        {
            connect(server);
            isConnected = true;
        }
        catch(RemoteException rex)
        {
            rex.printStackTrace();
        }
        catch(NotBoundException nex)
        {
            nex.printStackTrace();
        }
        return isConnected;
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

    private void connect(int index)throws RemoteException, NotBoundException
    {
        if(System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
        Registry registry = LocateRegistry.getRegistry(fileServerListe.get(index).getServerIP(),
                fileServerListe.get(index).getServerPort());
        this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
    }

    public Path [] getFileList() throws RemoteException
    {
        return fsserver.getFileList();
    }
}
