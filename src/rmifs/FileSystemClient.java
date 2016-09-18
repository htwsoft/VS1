package rmifs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * @author mpalumbo, cpatzek, soezdemir
 * @version 1.03
 * @date indefinitely
 */
//ToDo dringend aufräumen

public class FileSystemClient
{
	private final static String SERVER_HOST_IP_1 = "192.168.0.24";
	private final static String SERVER_HOST_IP_2 = "192.168.0.23";
	private final static String SERVER_HOST_IP_3 = "192.168.0.11";
	private final static String SERVER_HOST_FGVT = "172.19.1.209"; //localhost der fgvt
	private final static int SERVER_PORT = 4712; //ToDo variable Ports und IPs

	private VerwalterInterface vserver;  //Attribute zum Zugriff auf Verwalter Server Funktionen
	private String clientAddress = "not set!";
	private String clientName = "not set!";
	private String clientOS = "not set!";

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
	* Führt die Browse-Methode der FileSystemServer-Klasse aus
	*/
	public void browse()
	{
		String pfad = "";
		String erg = "";
		String [] dirListe;
		String [] fileListe;	
		
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll untersucht werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			erg = this.vserver.browseDirs(pfad);
			dirListe = erg.split("[;]");		
			
			erg = this.vserver.browseFiles(pfad);
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
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean search() throws RemoteException
	{
		String file = "";
		String dir = "";
		return vserver.search(file, dir);

	}

	public void createDir()
	{
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll erstellt werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			if( this.vserver.createDir(pfad) )
			{
				System.out.println("Ordner wurde erstellt!");	
			}
			else
			{
				System.out.println("Ordner konnte NICHT erstellt werden!");
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}			
	}
	
	public void createFile()
	{
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welche Datei soll erstellt werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			if( this.vserver.createFile(pfad) )
			{
				System.out.println("Datei wurde erstellt!");	
			}
			else
			{
				System.out.println("Datei konnte NICHT erstellt werden!");
			}
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}			
	}
	
	public void delete()
	{
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll gelöscht werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			if( this.vserver.delete(pfad) )
			{
				System.out.println("Ordner oder Datei wurde geloescht!");
			}
			else
			{
				System.out.println("Ordner oder Datei konnte NICHT geloescht werden!");
			}
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}	
	}
	
	public void rename()
	{
		String oldName = "";
		String newName = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll umbenannt werden?: ");
		oldName = eingabe.nextLine();
		System.out.print("Welcher Zielname?: ");
		newName = eingabe.nextLine();
		try
		{
			if( this.vserver.rename(oldName, newName) )
			{
				System.out.println("Ordner oder Datei wurde umbenannt!");
			}
			else
			{
				System.out.println("Ordner oder Datei konnte NICHT umbenannt werden!");
			}
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}	
	}	
	
	public void osname()
	{
		try
		{
			System.out.println("|-------------------------------------------------");
			System.out.println("| Verwendetes OS:  " + this.vserver.getOSName());
			System.out.println("| Name des Hosts:  " + this.vserver.getHostName());//ToDo
			System.out.println("| IP des Hosts	:  " + this.vserver.getHostAddress());//ToDo
			System.out.println("|-------------------------------------------------");

		}
		catch(Exception e)
		{
			System.out.println("Fehler: " + e.getMessage());
		}
		
	}	
	

	/**
	 * Fragt die verfuegbaren VerwalterServer ab, also deren Name und IP
     */
	public void list() throws RemoteException
	{
		String serverListe;
		try
		{
			serverListe = vserver.getServerList();
			System.out.println(serverListe);
		}
		catch(Exception e)
		{
			System.out.println("Fehler: "+ e.getMessage());
		}
	}



	//ToDo --> noch in Bearbeitung durch soezdemir
	/**
	 * Folgende Methoden liefern den Namen, IP-Adresse
	 * und den OS-Nammen eines Clients zurück
	 * @return Host Name, IP-Adresse und OS des Clients
	 * @throws RemoteException
	 * @author soezdemir
	 */
	public void setClientAddress(String clientAddress)throws RemoteException{
		this.clientAddress = clientAddress;
		sendClientAddress(clientAddress);
	}

	public void sendClientAddress(String clientAddress) throws RemoteException {
		vserver.sendClientAddress(clientAddress);
		System.out.println("\n***** Client: -> IP: [" + clientAddress + "] *****\n");
	}


	public String getClientAddress(){
		return this.clientAddress;
	}

	public void setClientOS(String clientOS){this.clientOS = clientOS;
	}

	public String  getClientOS ()
	{
		//vserver.sendClientAddress(clientAddress);
		return this.clientOS;
	}


	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("*clientaddress: " + clientAddress);

		return " ";  //sb.toString();

	}



}

