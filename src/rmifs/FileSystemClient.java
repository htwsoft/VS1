package rmifs;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * @author mpalumbo, cpatzek, soezdemir
 * @version 1.04
 * @date indefinitely
 */

public class FileSystemClient
{
	public String[] fileServerNames = new String[10];
	public String[] verwalterNames = new String[10];
	private  String aktuellerServer = "Server1";
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
		Registry registry = LocateRegistry.getRegistry(host, portNr);
		this.vserver = (VerwalterInterface) registry.lookup("VerwalterServer");
		//ToDo lookup für VerwalterServer & FileServer
	}
	
	/**
	* <br>Fuehrt die Browse-Methode der FileSystemServer-Klasse aus, bzw. initialBrowse</br>
	*/
	public void browse() throws RemoteException, NotBoundException
	{
		String pfad;
		String erg = "";
		String [] dirListe;
		String [] fileListe;	

		if(initialBrowse)
		{
			Scanner eingabe = new Scanner(System.in);
			System.out.print("Welcher Ordner soll untersucht werden?: ");
			pfad = eingabe.nextLine();

			erg = this.vserver.browseDirs(pfad, aktuellerServer );

			dirListe = erg.split("[;]");

			erg = this.vserver.browseFiles(pfad, aktuellerServer);

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
		}
		else
		{
			getServerNames();
			initialBrowse();
			System.out.println("Sie arbeiten momentan auf: "+fileServerNames[0]);
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

    public void search()throws RemoteException, NotBoundException
    {
        String pfad;
        String startDir;
        String erg = "";
        String [] fileListe;
        Scanner eingabe = new Scanner(System.in);
        System.out.print("Was soll gesucht werden?: ");
        pfad = eingabe.nextLine();
        System.out.print("Wo soll gesucht werden?: ");
        startDir = eingabe.nextLine();
		erg = this.vserver.search(pfad, startDir);

		if(erg.contains("Nicht gefunden,")||erg.contains("unterbrochen"))
        {
			fileListe = erg.split("[;]");
			System.out.println("Found-Files");
			System.out.println("---------------------------------------------------------------");

			for(int i=0; i<fileListe.length; i++)
			{
				System.out.println(fileListe[i]);
			}
        }
        else
        {
            fileListe = erg.split("[;]");
            System.out.println("Found-Files");
            System.out.println("---------------------------------------------------------------");

            for(int i=0; i<fileListe.length; i++)
            {
                System.out.println(fileListe[i]);
            }
        }
	}

	public void createDir()throws RemoteException, NotBoundException
	{
		String erg = "";
		String pfad;
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll erstellt werden?: ");
		pfad = eingabe.nextLine();
		erg = this.vserver.createDir(pfad, aktuellerServer);

		if(erg.contains("true"))
			System.out.println("Ordner wurde erstellt!");
		else if(erg.contains("false"))
			System.out.println("Ordner konnte NICHT erstellt werden!");
		else
			System.out.println(erg);
	}
	
	public void createFile() throws RemoteException, NotBoundException
	{
		String pfad;
		String erg = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welche Datei soll erstellt werden?: ");
		pfad = eingabe.nextLine();
		erg = this.vserver.createFile(pfad, aktuellerServer);

		if(erg.contains("true"))
			System.out.println("Datei wurde erstellt!");
		else if(erg.contains("false"))
			System.out.println("Datei konnte NICHT erstellt werden!");
		else
			System.out.println(erg);
	}
	
	public void delete() throws RemoteException, NotBoundException
	{
		String pfad;
		String erg = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll gelöscht werden?: ");
		pfad = eingabe.nextLine();
		erg = this.vserver.delete(pfad, aktuellerServer);

		if(erg.contains("true"))
			System.out.println("Ordner oder Datei wurde geloescht");

		else if(erg.contains("false"))
			System.out.println("Ordner oder Datei konnte NICHT geloescht werden!");

		else
			System.out.println(erg);
	}
	
	public void rename() throws RemoteException, NotBoundException
	{
		String oldName;
		String newName;
		String erg = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll umbenannt werden?: ");
		oldName = eingabe.nextLine();
		System.out.print("Welcher Zielname?: ");
		newName = eingabe.nextLine();
		erg = this.vserver.rename(oldName,newName, aktuellerServer);

		if(erg.contains("true"))
			System.out.println("Ordner oder Datei wurde umbenannt!");
		else if(erg.contains("false"))
			System.out.println("Ordner oder Datei konnte nicht umbenannt werden!");
		else
			System.out.println(erg);
	}
	
	public void osname() throws RemoteException, NotBoundException
	{
		System.out.println("|-------------------------------------------------");
		System.out.println("| Verwendetes OS:  " + this.vserver.getOSName(aktuellerServer));
		System.out.println("| Name des Hosts:  " + this.vserver.getHostName(aktuellerServer));//ToDo
		System.out.println("| IP des Hosts	:  " + this.vserver.getHostAddress(aktuellerServer));//ToDo
		System.out.println("|-------------------------------------------------");
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
	public void connectNewVerwalter(int verwalter)throws RemoteException, NotBoundException
	{
		FileServerListenElement tmp = vserver.getVerwalter(verwalter);
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		Registry registry = LocateRegistry.getRegistry(tmp.getServerIP(), tmp.getServerPort());
		this.vserver = (VerwalterInterface) registry.lookup("VerwalterServer");
	}
	/**
	 * <br>Bestimmt auf welchem Server ab jetzt gearbeitet werden soll</br>
	 * @param server Server der ausgewaehlt wurde
	 */
	public void setServer(int server)
	{
		switch(server)
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
		}
	}
	/**
	 * <br>Fordert die Namen der FileServer und der Verwalter an und speichert sie in dem Attribut fileServerNames</br>
	 */
	public void getServerNames() throws RemoteException, NotBoundException
	{
		fileServerNames = vserver.getAllFileServerNames();
		verwalterNames = vserver.getAllVerwalterNames();
	}
}


