/**
* RMI-Server für ein FileSystem
* @author Marco Palumbo
* @version 1.0
*/

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.util.*;

/**
* RMI-Server für das FileSystem
*/
public class FileSystemServer implements FSInterface
{
	private FileSystem fs = new FileSystem();
	public static FSInterface fsserver = new FileSystemServer();
	
	/**
	* Funktion sucht alle Ordner eines angegebenen Directory
	* @param dir Ordner der durchsucht werden soll 
	* @return einen String mita allen gefunden Ordner durch ";" getrennt
	*/
	public String browseDirs(String dir) throws RemoteException
	{
		Path [] dirListe = null;
		String ergListe = "";
		try
		{
			this.fs.browse(dir);
			dirListe = this.fs.getDirListe();
			for(int i=0; i<dirListe.length; i++)
			{
				if(i>0)
				{
					ergListe = ergListe + ";" + dirListe[i] ;
				}
				else
				{
					ergListe = ergListe + dirListe[i];
				}	
			}
			return ergListe;
		}
		catch(Exception e)
		{
			ergListe = "";
		}
		return ergListe;
	}

	/**
	* Funktion sucht alle Dateien eines angegebenen Directory
	* @param file Ordner der durchsucht werden soll 
	* @return einen String mit allen gefunden Dateien durch ";" getrennt
	*/	
	public String browseFiles(String dir) throws RemoteException
	{
		Path [] fileListe = null;
		String ergListe = "";
		try
		{
			this.fs.browse(dir);
			fileListe = this.fs.getFileListe();
			for(int i=0; i<fileListe.length; i++)
			{
				if(i>0)
				{
					ergListe = ergListe + ";" + fileListe[i] ;
				}
				else
				{
					ergListe = ergListe + fileListe[i];
				}		
			}
		}
		catch(Exception e)
		{
			ergListe = "";
		}
		return ergListe;
	}
	
	/**
	* Funktion sucht eine uebergeben Datei oder einen Ordner
	* @param file Ordner/Datei der/die gesucht werden soll
	* @return True wenn die Datei gefunden wurde
	*/
	public boolean search(String file) throws RemoteException
	{
		try
		{
			return this.fs.search(file);
		}
		catch(Exception e)
		{
			return false;	
		}	
	
	}
	
	/**
	* Funktion erstellt eine Datei
	* @param file Datei die erstellt werden soll
	* @return True wenn die Datei erstellt wurde
	*/	
	public boolean createFile(String file) throws RemoteException
	{
		try
		{
			return this.fs.create(file, "file");
		}
		catch(Exception e)
		{
			return false;	
		}	
	}
	
	/**
	* Funktion erstellt einen Ordner
	* @param dir Ordner der erstellt werden soll
	* @return True wenn der Ordner erstellt wurde
	*/		
	public boolean createDir(String dir) throws RemoteException
	{
		try
		{
			return this.fs.create(dir, "dir");
		}
		catch(Exception e)
		{
			return false;
		}		
	}
	
	/**
	* Funktion löscht einen Ordner oder eine Datei
	* @param file Ordner/Datei der/die gelöscht werden soll
	* @return True wenn der Ordner/die Datei geloescht wurde
	*/		
	public boolean delete(String file) throws RemoteException
	{
		try
		{
			this.fs.delete(file);
			return true;
		}
		catch(Exception e)
		{
			return false;	
		}		
	}
	
	/**
	* Funktion benennt einen Ordner oder eine Datei um
	* @param oldName aktueller Name der Datei oder des Ordners
	* @param newName neuer Name der Datei oder des Ordners
	* @return True wenn der Ordner/die Datei umbenannt werden konnte
	*/	
	public boolean rename(String oldName, String newName) throws RemoteException
	{
		try
		{
			return this.fs.rename(oldName, newName);
		}
		catch(IOException e)
		{
			return false;	
		}		
	}
	
	/**
	* Funktion liefert den Namen des OS zurück
	* @return Name des OS-Systems des FileSystems
	*/		
	public String getOSName()throws RemoteException
	{
		return this.fs.getOSName();
	}
	
	/**
	* Funktion liefert die Dateien der letzten suche (browse)
	* @return Dateien des Letzten Browse befehls
	*/
	public String getFileListe() throws RemoteException
	{
		Path [] fileListe = null;
		String ergListe = "";
		try
		{
			fileListe = this.fs.getFileListe();
			for(int i=0; i<fileListe.length; i++)
			{
				ergListe = ergListe + fileListe[i] + ";";	
			}
		}
		catch(Exception e)
		{
			ergListe = "";
		}
		return ergListe;
	}
	
	/**
	* Funktion liefert die Ordner der letzten suche (browse)
	* @return Ordner des Letzten Browse befehls
	*/	
	public String getDirListe() throws RemoteException
	{
		Path [] dirListe = null;
		String ergListe = "";
		try
		{
			dirListe = this.fs.getFileListe();
			for(int i=0; i<dirListe.length; i++)
			{
				ergListe = ergListe + dirListe[i] + ";";	
			}
		}
		catch(Exception e)
		{
			ergListe = "";
		}
		return ergListe;	
	}
	
	/**
	* Hauptmethode
	* Startet den Server
	* @param args[] Parameter beim Programm start. Erster Eintrag ist PortNr für Server
	*/
	public static void main(String args[])
	{
		try
		{
			if(args.length >= 1)
			{
				int serverPort = 0;
				serverPort = Integer.parseInt(args[0]);
				//Security Manager ermöglicht/regelt zugriff auf Klasse
				if (System.getSecurityManager() == null)
				{
					System.setSecurityManager ( new SecurityManager() );
				}
				//Stellt das Objekt dem System zur Verfügung
				FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsserver, serverPort);
				//Registry erstellen um Objekt ansprechen zu können
				Registry registry =  LocateRegistry.createRegistry(serverPort);
				//Objekt an registry binden
				registry.rebind("FileSystemServer", stub);
				System.out.println("Server bound ...");
			}
			else
			{
				System.out.println("Bitte Server-Port zum binden angeben!");
			}
		}
		catch(Exception e)
		{
			System.out.println( "Fehler: " + e.toString() );
		}	
	}

}