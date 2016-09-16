//package src.rmifs;
package rmifs;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
* RMI-Server für ein FileSystem
* @author Marco Palumbo
* @version 1.03
*/


/**
* RMI-Server für das FileSystem
*/
public class FileSystemServer implements FSInterface
{
	private final static String SERVER_HOST_IP_1 = "192.168.0.11";
	private final static String SERVER_HOST_IP_2 = "192.168.0.23";
	private final static String SERVER_HOST_IP_3 = "192.168.0.24";
	private final static String SERVER_HOST_FGVT = "172.19.1.209"; //localhost der fgvt


	//ToDo FileSystemListe für mehrere FileSystems
	private FileSystem fs = new FileSystem();
	private String clientAddress;

	/**
	 * Hauptmethode
	 * Startet den FileSystem-Server
	 * @param args Portnummer des FileSystemServers
	 */
	public static void main(String args[])
	{
		//**** regelt RMI Kommunikation ***** muss Anfang der main bleiben
		System.setProperty("java.security.policy", "java.policy" );
		System.setProperty("java.rmi.server.hostname", SERVER_HOST_IP_1); //"172.19.1.209" fgvt
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
				FileSystemServer fsServer = new FileSystemServer();
				//Registry erstellen um Objekt ansprechen zu können
				Registry registry =  LocateRegistry.createRegistry(serverPort);
				//Stellt das Objekt dem System zur Verfügung
				FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsServer, serverPort);

				//Objekt an registry binden
				registry.rebind("FileSystemServer", stub);
				System.out.println("Server bound ...\nPort now open at " + serverPort);
				System.out.print("\nServer Name: " + fsServer.getHostName()
									+ "\nServer IP: " + fsServer.getHostAddress()
									+ "\nServer runs on " + fsServer.getOSName());
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

	/**
	* Funktion sucht alle Ordner eines angegebenen Directory
	* @param dir Ordner der durchsucht werden soll 
	* @return einen String mit allen gefunden Ordner durch ";" getrennt
	*/
	public FileSystemServer(){super();}
	public String browseDirs(String dir) throws RemoteException
	{
		Path [] dirListe = null;
		String ergListe = "";
		System.out.println("Funktion: browseDirs - Param: " + dir);
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
			//prüfen ob ein Ordner gefunden wurde
			//wenn nicht ist ergListe = dir
			if( ergListe.equals(dir) )
			{
				ergListe = "";
			}
		}
		catch(Exception e)
		{
			ergListe = "";
			System.out.println("Funktion: " + e.toString());
		}
		System.out.println("Return: \"" + ergListe + "\"");
		return ergListe;
	}

	/**
	* Funktion sucht alle Dateien eines angegebenen Directory
	* @param file Ordner der durchsucht werden soll 
	* @return einen String mit allen gefunden Dateien durch ";" getrennt
	*/	
	public String browseFiles(String file) throws RemoteException
	{
		Path [] fileListe = null;
		String ergListe = "";
		System.out.println("Funktion: browseFiles - Param: " + file);
		try
		{
			this.fs.browse(file);
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
			//prüfen ob eine Datei gefunden wurde
			//wenn nicht ist ergListe = file
			if( ergListe.equals(file) )
			{
				ergListe = "";
			}
		}
		catch(Exception e)
		{
			ergListe = "";
			System.out.println("Fehler: " + e.toString());
		}
		System.out.println("Return: \"" + ergListe + "\"");
		return ergListe;
	}
	
	/**
	* Funktion sucht nach der übergebenen Datei ab dem angegebenen Ordner
	* @param file Datei nach der gesucht werden soll
	* @param startDir Ordner ab dem die Datei gesucht werden soll
	* @return Liste mit Dateien die auf den Such-String passen mit ";" getrennt
	*/
	public String search(String file, String startDir) throws RemoteException
	{
		System.out.println("Funktion: search - Params: " + file + ", " + startDir);
		
		Path [] fileListe = null;
		String ergListe = "";
		try
		{	
			//search liefert true zurueck wenn mindestens eine Datei 
			//gefunden wurde
			if( this.fs.search(file, startDir) )
			{
				//Gefundene Dateien speichern und als String
				//zurück liefern
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
		}
		catch(Exception e)
		{
			ergListe = "";
			System.out.println("Fehler: " + e.toString());
		}
		System.out.println("Return: \"" + ergListe + "\"");
		return ergListe;
	}
	
	/**
	* Funktion erstellt eine Datei
	* @param file Datei die erstellt werden soll
	* @return True wenn die Datei erstellt wurde
	*/	
	public boolean createFile(String file) throws RemoteException
	{
		boolean fileCreated;
		System.out.println("Funktion: createFile - Param: " + file);
		try
		{
			fileCreated = this.fs.create(file, "file");
		}
		catch(Exception e)
		{
			System.out.println("Fehler: " + e.toString());
			fileCreated = false;
			
		}
		System.out.println("Return: " + fileCreated);
		return fileCreated;
	}
	
	/**
	* Funktion erstellt einen Ordner
	* @param dir Ordner der erstellt werden soll
	* @return True wenn der Ordner erstellt wurde
	*/		
	public boolean createDir(String dir) throws RemoteException
	{
		boolean dirCreated;
		System.out.println("Funktion: createDir - Param: " + dir);
		try
		{
			dirCreated = this.fs.create(dir, "dir");
		}
		catch(Exception e)
		{
			System.out.println("Fehler: " + e.toString());
			dirCreated = false;
			
		}
		System.out.println("Return: \"" + dirCreated + "\"");
		return dirCreated;
	}
	
	/**
	* Funktion löscht einen Ordner oder eine Datei
	* @param file Ordner/Datei der/die gelöscht werden soll
	* @return True wenn der Ordner/die Datei geloescht wurde
	*/		
	public boolean delete(String file) throws RemoteException
	{
		boolean fileDeleted;
		System.out.println("Funktion: delete - Param: " + file);
		try
		{
			fileDeleted = this.fs.delete(file);
		}
		catch(Exception e)
		{
			System.out.println("Fehler: " + e.toString());
			fileDeleted = false;	
		}
		System.out.println("Return: \"" + fileDeleted + "\"");
		return fileDeleted;
	}
	
	/**
	* Funktion benennt einen Ordner oder eine Datei um
	* @param oldName aktueller Name der Datei oder des Ordners
	* @param newName neuer Name der Datei oder des Ordners
	* @return True wenn der Ordner/die Datei umbenannt werden konnte
	*/	
	public boolean rename(String oldName, String newName) throws RemoteException
	{
		boolean fileRenamed;
		System.out.println("Funktion: rename - Params: " + oldName + ", " + newName);
		try
		{
			fileRenamed = this.fs.rename(oldName, newName);
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.toString());
			fileRenamed = false;
			
		}
		System.out.println("Return: \"" + fileRenamed + "\"");
		return fileRenamed;
	}
	
	/**
	* Funktion liefert den Namen des OS zurück
	* @return Name des OS-Systems des FileSystems
	*/		
	public String getOSName()throws RemoteException
	{
		System.out.println("Funktion: getOSName");
		String osName;
		osName = fs.getOSName();
		System.out.println("System runs on \"" + osName + "\"");
		return osName;
	}

	/**
	 * Funktion liefert den Namen, IP & OS eines Hosts zurück
	 * @return Host Name des FileSystems
	 * @throws RemoteException
	 * @author soezdemir
     */
	public String getHostName() throws RemoteException
	{
		System.out.println("Funktion: getHostName");
		String hostName;
		hostName = fs.getHostName();
		System.out.println("Hostname is \"" + hostName + "\"");
		return hostName;
	}
	public String getHostAddress() throws RemoteException
	{
		System.out.println("Funktion: getHostAddress");
		String hostAddress;
		hostAddress = fs.getHostAddress();
		System.out.println("Server IP is \"" + hostAddress + "\"");
		return hostAddress;
	}

	public void sendClientAddress(String clientAddress) throws RemoteException
	{
		this.clientAddress = clientAddress;
		System.out.println("\n " + clientAddress + " is connected to Server " + getHostAddress());
	}

	public String getClientAddress() throws  RemoteException{
		return this.clientAddress;
	}

	/**
	* Funktion liefert die Dateien der letzten suche (browse)
	* @return Dateien des Letzten Browse befehls
	*/
	public String getFileListe() throws RemoteException
	{
		System.out.println("Funktion: getFileListe");
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
			System.out.println("Fehler: " + e.toString());
		}
		System.out.println("Return: \"" + ergListe + "\"");
		return ergListe;
	}
	
	/**
	* Funktion liefert die Ordner der letzten suche (browse)
	* @return Ordner des Letzten Browse befehls
	*/	
	public String getDirListe() throws RemoteException
	{
		System.out.println("Funktion: getDirListe");
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
			System.out.println("Fehler: " + e.toString());
		}
		System.out.println("Return: \"" + ergListe + "\"");
		return ergListe;	
	}


}