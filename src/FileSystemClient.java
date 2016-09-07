import java.io.*;
import java.util.*;
import java.nio.*;
import java.nio.file.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class FileSystemClient
{
	private FSInterface fsserver;
	enum MENUE { CLOSE, FALSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME }
	/**
	* Hauptmethode der Demo
	* startet eine Menue
	* @param args übergeben Parameter erster Parameter ist der Port zum Server
	*/	
	public static void main(String args[]) 
	{
		FileSystemClient fsc = null;
		int serverPort = 0;
		int eingabe = -1;
		MENUE meue_eingabe = MENUE.FALSE;
		try 
		{
			System.setProperty("java.security.policy","C:\\Program Files (x86)\\Java\\jre1.8.0_101\\lib\\security\\java.policy");
			serverPort = Integer.parseInt(args[0]);	
			fsc = new FileSystemClient(serverPort);		
			while(meue_eingabe != MENUE.CLOSE)
			{
				eingabe = fsc.zeigeMenue();
				meue_eingabe = fsc.intToMenue(eingabe);
				switch(meue_eingabe)
				{
					case CLOSE: System.out.println("Programm wurde beendet!"); break;
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
		MENUE meue_eingabe = MENUE.FALSE;
		switch(eingabe)
		{		
			case 0: meue_eingabe = MENUE.CLOSE; break;
			case 1: meue_eingabe = MENUE.BROWSE; break;
			case 2: meue_eingabe = MENUE.SEARCH; break;
			case 3: meue_eingabe = MENUE.CREATE_DIR; break;
			case 4: meue_eingabe = MENUE.CREATE_FILE; break;
			case 5: meue_eingabe = MENUE.DELETE; break;
			case 6: meue_eingabe = MENUE.RENAME; break;
			case 7: meue_eingabe = MENUE.OS_NAME; break;
			default: meue_eingabe = MENUE.FALSE; break;
		}
		return meue_eingabe;
	}
	
	/**
	* Konstruktor 
	* erzeugt eine FileSystem-Klasse
	*/
	public FileSystemClient(int portNr) throws RemoteException, NotBoundException
	{
		String ip = "//192.168.0.105:2222/";
		//String ip = "//10.9.41.43:2222/";
		//String ip = "//10.9.40.229:1500/";
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
		Registry registry = LocateRegistry.getRegistry(portNr);
		//this.fsserver = (FSInterface) registry.lookup("FileSystemServer");

		try
		{
			this.fsserver = (FSInterface)Naming.lookup(ip + "FileSystemServer");
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	/**
	* Führt die Brwows-Methode der FileSystemServer-Klasse aus
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
			erg = this.fsserver.browseDirs(pfad);
			dirListe = erg.split("[;]");		
			
			erg = this.fsserver.browseFiles(pfad);
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
			erg = this.fsserver.search(pfad, startDir);
			fileListe = erg.split("[;]");
			System.out.println("Found-Files");
			System.out.println("---------------------------------------------------------------");
			for(int i=0; i<fileListe.length; i++)
			{
				System.out.println(fileListe[i]);
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
			if( this.fsserver.createDir(pfad) )
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
			if( this.fsserver.createFile(pfad) )
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
			if( this.fsserver.delete(pfad) )
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
			if( this.fsserver.rename(oldName, newName) )
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
			System.out.println("| Verwendetes OS: " + this.fsserver.getOSName());
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
	private int zeigeMenue()
	{
		//Scanner liste eingabe des Benutzers ein
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		int eingabe = -1;
		while(eingabe < 0 || eingabe > 7)
		{
			//Auswahlmenue zeigen bis eingabe richtig
			try
			{
				System.out.println("");
				System.out.println("---------------------");
				System.out.println("Menue:");
				System.out.println("0: Beenden");
				System.out.println("1: Browse");
				System.out.println("2: Search");
				System.out.println("3: Create Dir");
				System.out.println("4: Create File");
				System.out.println("5: Delete");
				System.out.println("6: Rename");
				System.out.println("7: OS-Name");
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
}