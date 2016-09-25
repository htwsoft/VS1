package rmifs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author mpalumbo, cpatzek, soezdemir
 * @version 1.04
 * @date indefinitely
 */

public class FileSystemClient
{
	public ArrayList<String> fileServerNames = new ArrayList<>();
	public ArrayList<String> verwalterNames = new ArrayList<>();
	private int aktuellerServer = 0;
	private VerwalterInterface vserver;  //Attribute zum Zugriff auf Verwalter Server Funktionen
	private String clientAddress = "not set!";
	private String clientName = "not set!";
	private String clientOS = "not set!";
	private boolean initialBrowse = false;

	/**
	* Konstruktor 
	* erzeugt eine FileSystem-Klasse
	 * host und portNr lösen den Verwalter-Server des lokalen Systems auf
	*/
	public FileSystemClient(int portNr, String host) throws RemoteException, NotBoundException
	{
		if (System.getSecurityManager() == null) 
		{
			System.setSecurityManager(new SecurityManager());
		}
		startNew(portNr, host);
		//ToDo lookup für VerwalterServer & FileServer
	}

	private void startNew(int portNr, String host)throws RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry(host, portNr);
		this.vserver = (VerwalterInterface) registry.lookup("VerwalterServer");
	}

	/**
	* <br>Fuehrt die Browse-Methode der FileSystemServer-Klasse aus, bzw. initialBrowse</br>
	*/
	public String browseDirs(String pfad) throws RemoteException, NotBoundException
	{
			if (!initialBrowse)
			{
				return this.vserver.browseDirs(pfad, aktuellerServer);
			} else
			{
				return this.vserver.initialBrowseDirs(pfad);
			}
	}

	public String browseFiles(String pfad) throws RemoteException, NotBoundException
	{
		if(!initialBrowse)
		{
			return this.vserver.browseFiles(pfad, aktuellerServer);
		}
		else
		{
			return this.vserver.initialBrowseFiles(pfad);
		}
	}

	/**
	 * <br>fuehrt den ersten Browse durch, bei Start des Clients</br>
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	private void initialBrowse()throws RemoteException, NotBoundException
	{
		String erg = "";
		String pfad = "";
		String[] fileListe;
		String[] dirListe;

		erg = this.vserver.initialBrowseDirs(pfad);

		dirListe = erg.split("[;]");

		erg = this.vserver.initialBrowseFiles(pfad);

		fileListe = erg.split("[;]");

		System.out.println("File-Liste");
		System.out.println("---------------------------------------------------------------");
		for(int i=0; i<fileListe.length; i++)
		{
			System.out.println(fileListe[i]);
		}
		System.out.println("");
		System.out.println("Dir-Liste");
		System.out.println("---------------------------------------------------------------");
		for(int j=0; j<dirListe.length; j++)
		{
			System.out.println(dirListe[j]);
		}
		if(!erg.contains("Fehler"))
			initialBrowse = true;
	}

    public String search(String file, String startDir) throws RemoteException, NotBoundException
    {
		return this.vserver.search(file, startDir);
	}

	public boolean createDir(String dir) throws RemoteException, NotBoundException
	{
		String erg = "";
		erg = this.vserver.createDir(dir, aktuellerServer);
		return erg.contains("true");
	}
	
	public boolean createFile(String file) throws RemoteException, NotBoundException
	{
		String erg = "";
		erg = this.vserver.createFile(file, aktuellerServer);
		return erg.contains("true");
	}
	
	public boolean delete(String file) throws RemoteException, NotBoundException
	{
		String erg = "";
		erg = this.vserver.delete(file, aktuellerServer);
		return erg.contains("true");
	}
	
	public boolean rename(String oldName, String newName) throws RemoteException, NotBoundException
	{
		String erg = "";
		erg = this.vserver.rename(oldName,newName, aktuellerServer);
		return erg.contains("true");
	}
	
	public String getOSName() throws RemoteException, NotBoundException
	{
		return this.vserver.getOSName(aktuellerServer);
	}
	public String getHostName() throws RemoteException, NotBoundException
	{
		return this.vserver.getHostName(aktuellerServer);
	}

	public String getHostAddress() throws RemoteException, NotBoundException
	{
		return this.vserver.getHostAddress(aktuellerServer);
	}

	public String[] getAllVerwalterNames() throws RemoteException, NotBoundException
	{
		String[] verwalterListe;
		int i=0;
		this.getServerNames();
		verwalterListe = new String[verwalterNames.size()];
		while(!verwalterNames.get(i).contains("default"))
		{
			verwalterListe[i] = verwalterNames.get(i);
			i++;
		}
		return verwalterListe;
	}


	/**
	 * <br>Fragt die verfuegbaren VerwalterServer ab, also deren Name und IP</br>
     */
	public void list() throws RemoteException, NotBoundException
	{
		String serverListe = "";
		serverListe = vserver.getServerList();
		System.out.println(serverListe);
	}

	/**
	 * <br>Folgende Methoden liefern den Namen, IP-Adresse
	 * und den OS-Nammen eines Clients zurück</br>
	 * @return Host Name, IP-Adresse und OS des Clients
	 * @throws RemoteException
	 * @author soezdemir
	 */
	public void setClientAddress(String clientAddress) throws RemoteException, NotBoundException
    {
		this.clientAddress = clientAddress;
		sendClientAddress(clientAddress);
	}

	public void sendClientAddress(String clientAddress) throws RemoteException, NotBoundException
    {
		String erg = "";
		erg = vserver.sendClientAddress(clientAddress);
		if(erg.contains(""))
			System.out.println("\n***** Client: -> IP: [" + clientAddress + "] *****\n");
		else
			System.out.println(erg);
	}

	public String getClientAddress()
    {
		return this.clientAddress;
	}

	public void setClientOS(String clientOS)
    {
        this.clientOS = clientOS;
	}

	public String  getClientOS ()
	{
		return this.clientOS;
	}


	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("*clientaddress: " + clientAddress);

		return " ";  //sb.toString();

	}

	/**
	 * <br> Verbindet den Client zum neuen Verwalter</br>
	 * @param verwalter der ausgewaehlte Verwalter
	 */
	public boolean connectNewVerwalter(int verwalter) throws RemoteException, NotBoundException
	{
		FileServerListenElement tmp = new FileServerListenElement();
		tmp = vserver.getVerwalter(verwalter);
		startNew(tmp.getServerPort(), tmp.getServerIP());
		return true;
	}

	/**
	 * <br> Verbindet den Client zum neuen Verwalter</br>
	 * @param verwalter der ausgewaehlte Verwalter
	 */
	public FileServerListenElement getNewVerwalter(int verwalter)throws RemoteException, NotBoundException
	{
		return vserver.getVerwalter(verwalter);
	}
	/**
	 * <br>Bestimmt auf welchem Server ab jetzt gearbeitet werden soll</br>
	 * @param server Server der ausgewaehlt wurde
	 * @param laenge
	 */
	public void setServer(int server, int laenge)
	{
		if(server < laenge)
		{
			aktuellerServer = server;
			System.out.println("Sie arbeiten nun auf: "+ fileServerNames.get(server));
		}
		else
			System.out.println("\nVorgang abgebrochen!\n");
		/*switch(server)
		{
			case 0: System.out.println("Vorgang abgebrochen");break;
			case 1: aktuellerServer = fileServerNames[0];
				System.out.println("Sie arbeiten nun auf: "+fileServerNames[0]);break;
			case 2: aktuellerServer = fileServerNames[1];
				System.out.println("Sie arbeiten nun auf: "+fileServerNames[1]);break;
			case 3: aktuellerServer = fileServerNames[2];
				System.out.println("Sie arbeiten nun auf: "+fileServerNames[2]);break;
			default:
				System.out.println("Fehlerhafte Eingabe!");
		}*/
	}
	/**
	 * <br>Fordert die Namen der FileServer und der Verwalter an und speichert sie in dem Attribut fileServerNames
	 * bzw. in verwalterNames</br>
	 */
	public void getServerNames() throws RemoteException, NotBoundException
	{
		fileServerNames = vserver.getAllFileServerNames(0);
		verwalterNames = vserver.getAllVerwalterNames(0);
	}
}


