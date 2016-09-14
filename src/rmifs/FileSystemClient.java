//package src.rmifs;
package rmifs;

/**
 * @author mpalumbo, cpatzek, soezdemir
 * @version 1.01
 * @date indefinitely
 */

import java.io.*;
import java.util.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.*;
import java.net.UnknownHostException.*;
import java.rmi.RemoteException;
import java.net.*;


public class FileSystemClient
{
	private VerwalterInterface vserver;  //Attribute zum Zugriff auf Verwalter Server Funktionen

	private enum MENUE { CLOSE, LIST, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME, FALSE }
	private String clientAddress = "";
	private String clientName = "";
	private String clientOS = "";

	/**
	* Hauptmethode der Demo
	* startet eine Menue
	* @param args übergeben Parameter erster Parameter ist der Port zum Server
	*/	
	public static void main(String args[]) 
	{
		FileSystemClient fsc = null;
		//FileSystemClient fsclient = new FileSystemClient(4712, "localhost");
		int serverPort = 0;
		int eingabe = -1;
		MENUE menue_eingabe = MENUE.FALSE;
		try 
		{
			serverPort = Integer.parseInt(args[0]);
			System.setProperty("java.rmi.server.hostname", "localhost");//192.168.0.101
			fsc = new FileSystemClient(serverPort, args[1]);
			while(menue_eingabe != MENUE.CLOSE)
			{
				eingabe = fsc.zeigeMenue();
				menue_eingabe = fsc.intToMenue(eingabe);
				switch(menue_eingabe)
				{
					case CLOSE: System.out.println("Programm wurde beendet!"); break;
					case LIST: fsc.list(); break;
					case BROWSE: fsc.browse(); break;
					case SEARCH: fsc.search(); break;
					case CREATE_DIR: fsc.createDir(); break;
					case CREATE_FILE: fsc.createFile(); break;
					case DELETE: fsc.delete(); break;
					case RENAME: fsc.rename(); break;
					case OS_NAME: fsc.osname(); break;
					default: System.out.println("Falsche Eingabe!"); break;
				}
			}	
		} 
		catch (Exception e) 
		{
			System.err.println("FileSystemClient exception:"); e.printStackTrace();
		}
		System.exit(0);
	} 
	
	public MENUE intToMenue(int eingabe)
	{
		MENUE menue_eingabe = MENUE.FALSE;
		switch(eingabe)
		{		
			case 0: menue_eingabe = MENUE.CLOSE; break;
			case 1: menue_eingabe = MENUE.LIST; break;
			case 2: menue_eingabe = MENUE.BROWSE; break;
			case 3: menue_eingabe = MENUE.SEARCH; break;
			case 4: menue_eingabe = MENUE.CREATE_DIR; break;
			case 5: menue_eingabe = MENUE.CREATE_FILE; break;
			case 6: menue_eingabe = MENUE.DELETE; break;
			case 7: menue_eingabe = MENUE.RENAME; break;
			case 8: menue_eingabe = MENUE.OS_NAME; break;
			default: menue_eingabe = MENUE.FALSE; break;
		}
		return menue_eingabe;
	}
	
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
	}
	
	/**
	* Führt die Browse-Methode der FileSystemServer-Klasse aus
	*/
	private void browse()
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
			System.out.println("Fehler: " + e.getMessage());	
		}
	}
	
	private void search()
	{
		String pfad = "";
		String startDir = "";
		String erg = "";
		String [] fileListe;
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Was soll gesucht werden?: ");
		pfad = eingabe.nextLine();
		System.out.print("Wo soll gesucht werden?: ");
		startDir = eingabe.nextLine();
		try
		{
			erg = this.vserver.search(pfad, startDir);
			if(erg.contains("Nicht gefunden,"))
			{
				System.out.println(erg);
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
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}			
	}

	private void createDir()
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
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}			
	}
	
	private void createFile()
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
	
	private void delete()
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
	
	private void rename()
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
	
	private void osname()
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
	* Funktion zeigt ein Auswahlmenue und liefert 
	* die Auswahl des Benutzers zurück
	*/
	private int zeigeMenue ()
	{
		//Scanner liste eingabe des Benutzers ein
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		toString();
		int eingabe = -1;
		while(eingabe < 0 || eingabe > 8)
		{
			//Auswahlmenue zeigen bis eingabe richtig
			try
			{
				//Rückgabeparameter für den Verwalter & FileSystemServer
				vserver.setClientAddress(getClientAddress());
				//System.out.print(toString());
				//Terminal Ausgabe Menue
				System.out.println("");
				System.out.println("---------------------");
				System.out.println("Menue:");
				System.out.println("0: Beenden");
				System.out.println("1: Server List");
				System.out.println("2: Browse");
				System.out.println("3: Search");
				System.out.println("4: Create Dir");
				System.out.println("5: Create File");
				System.out.println("6: Delete");
				System.out.println("7: Rename");
				System.out.println("8: OS-Name");
				System.out.println("---------------------");
				System.out.print("Was moechten Sie tun?: ");
				eingabe = Integer.parseInt(br.readLine());
			}
			catch(Exception e)
			{
				System.out.println("Fehlerhafte Eingabe!");
			}
		}
		System.out.println("");

		return eingabe;

	}

	/**
	 * Fragt die verfuegbaren VerwalterServer ab, also deren Name und IP
     */
	private void list()
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
	public void setClientAddress(String clientAddress) {
		System.out.println("***** Client: -> IP: " + clientAddress);
	}

	public String getClientName()
	{
		//String clientName = "";
		try {
			InetAddress clientMachine = Inet4Address.getLocalHost();
			clientName = clientMachine.getHostName();
			System.out.println("ClientName:\t" + clientName);

		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return clientName;
	}

	public String getClientAddress()
	{
		//String clientAddress = "";
		try {
			InetAddress clientIP = Inet4Address.getLocalHost();
			clientAddress = clientIP.getHostAddress();
			System.out.println("ClientIP:\t" + clientIP);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clientAddress;
	}

	public String getClientOS()
	{
		try {
		//System.out.println("Funktion: os.name, os.version, os.arch");
		String clientOS = System.getProperty("os.name") +
							", Version " + System.getProperty("os.version") +
							" on " + System.getProperty("os.arch") + " architecture.";
		System.out.println("ClientOS:\t" + clientOS);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return clientOS;
	}


	@Override
	public final String toString()
	{
			String output = "\nIP Address: " + clientAddress +
							" | Client: "  + getClientName() +
							" | OS Name: " + getClientOS();
			return output;
	}

 }

