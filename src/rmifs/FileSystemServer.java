
package rmifs;

import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;


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
	//ToDo FileSystemListe für mehrere FileSystems
	private FileSystem fs = new FileSystem();
	private String clientAddress;
	private List<FileSystem> fileSystems;

	/**
	* Funktion sucht alle Ordner eines angegebenen Directory
	* dir Ordner der durchsucht werden soll
	* @return einen String mit allen gefunden Ordner durch ";" getrennt
	*/
	public FileSystemServer(){
		super();
		fileSystems = new LinkedList<FileSystem>();
		fileSystems.add(fs);
	}

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
	public boolean search(String file, String startDir) throws RemoteException {
		boolean isFound = false;
		for (FileSystem fs : fileSystems) {
			try {
				isFound = fs.search(file, startDir);
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return isFound;
	}

	/**
	* <br> Funktion erstellt eine Datei </br>
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
	public Path [] getFileList() throws RemoteException {
		return fs.getFileListe();
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