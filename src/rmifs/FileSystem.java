/**
* Klasse zum Darstellen eines Dateisystems
* @author Marco Palumbo, Nadine Breitenstein
* @version 1.01
*/
//package src.rmifs;
package rmifs;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.net.*;
import static java.nio.file.StandardCopyOption.*;

/**
* Klasse Filesystem dient zur leichteren Kommunikation/ Darstellung
* mit einem beliebigen FileSystem
*/
public class FileSystem
{	
	private String osname; //Name des Betriebsystems
	private String hostname; // Name der Machine
	private String hostAddress; // IP der Machine
	private Path [] dirListe; //Liste der gefundenen Ordner bei Browse
	private Path [] fileListe; //Liste der gefundenen Dateien bei Browse
	
	/**
	* Konstruktor der FileSystem Klasse
	* Initialisiert die internen variablen
	*/
	public FileSystem()
	{
		this.osname = System.getProperty("os.name");

		try {
			this.hostname = InetAddress.getLocalHost().getHostName();
			this.hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		}
		this.dirListe = null;
		this.fileListe = null;

	}
	
	/**
	* Prozedur durchsucht einen Ordner auf dessen Inhalt
	* und speichert die Daten in einer Dir- und FileListe
	* Wenn keine Datei oder Ordner gefunden wurden wird der übergebene Pfad gespeichert
	* @param dir mit dem Ordnernamen der Untersucht werden soll (vollständiger Pfad)
	*/
	public void browse(String dir) throws IOException
	{
		DirWatcher dw = new DirWatcher(); //Eigene Klasse DirWatcher zum durchlaufen des Ordners
		Path path = Paths.get(dir); //Ordner der durchsucht werden soll
		//Iintialisieren des Durchlaufs
		dw.initWalkFileTree();
		//Pfad durchlaufen
		Files.walkFileTree(path, dw);
		//prüfen ob Unterordner gefunden wurden
		if(dw.getAnzDirs() > 0)
		{
			this.dirListe = dw.getDirListe();
		}
		else
		{
			//Keine Unterordner gefunden. Indem Fall wird der übergebene
			//Pfad zurückgeliefert
			this.dirListe = new Path[1]; 
			this.dirListe[0] = path;
		}
		//prüfen ob Dateien gefunden wurden
		if(dw.getAnzFiles() > 0)
		{
			this.fileListe = dw.getFileListe();
		}
		else
		{
			//Keine Dateien gefunden. Indem Fall wird der übergebene
			//Pfad zurückgeliefert
			this.fileListe = new Path[1]; 
			this.fileListe[0] = path;
		}
	}
	/**
	* Funktion sucht nach der übergebenen Datei ab dem angegebenen Ordner
	* @param file Datei nach der gesucht werden soll
	* @param startDir Ordner ab dem die Datei gesucht werden soll
	* @return true wenn mindestens eine Datei gefunden wurde sonst false
	*/
	public boolean search(String file, String startDir) throws IOException
	{
		Finder finder = new Finder(file); //Eigene Klasse DirWatcher zum durchlaufen des Ordners
		Path startPath = Paths.get(startDir); //Ordner der durchsucht werden soll
		
		//Pfad durchlaufen
		Files.walkFileTree(startPath, finder);
		//Prüfen ob Dateien gefunden wurde
		if(finder.getNumMatches() > 0 )
		{
			//Alle Datein die gefunden wurde in die FileListe schreiben
			this.fileListe = finder.getFoundFiles();
			return true;
		}
		else
		{
			//Keine Dateien gefunden. Indem Fall wird der übergebene
			//Pfad zurückgeliefert
			this.fileListe = new Path[1]; 
			this.fileListe[0] = startPath;
			return false;
		}
	}
	
	/**
	* Funktion erstellt einen Ordner oder eine Datei in dem übergebenen Ordner
	* @param dir Datei- oder Ordnername
	* @param typ Angabe ob Ordner oder Datei erstellt werden soll "dir" oder "file"
	* @return true wenn das erstellen erfolgreich war
	*/
	public boolean create(String dir, String typ) throws IOException
	{
		boolean returnWert = false;
		Path path = Paths.get(dir); //Ordner der durchsucht werden soll
		if( Files.exists(path, LinkOption.NOFOLLOW_LINKS))
		{
			returnWert = false;
		
		}
		else
		{
			if(typ.equalsIgnoreCase("dir"))
			{
				Files.createDirectory(path);
				returnWert = true;
			}
			else
			if(typ.equalsIgnoreCase("file"))
			{
				Files.createFile(path);
				returnWert = true;
			}
			else
			{
				returnWert = false;
			}
			
		}
		return returnWert;
	}
	/**
	* Funktion löscht die übergebene Datei oder den übergebenen Ordner
	* @param dir Datei oder Ordner der geloescht werden soll
	* @return true wenn das Loeschen erfolgreich war	
	*/
	public boolean delete(String dir)
	{
		try
		{
			Path path = Paths.get(dir); //Ordner der durchsucht werden soll
			if( Files.exists(path, LinkOption.NOFOLLOW_LINKS) )
			{
				Files.delete(path);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/**
	* Funktion benennt eine Datei oder einen Ordner um
	* @param oldName aktueller Name
	* @param newName neuer Name 
	* @return true wenn das Umbennen erfolgreich war
	*/
	public boolean rename(String oldName, String newName) throws IOException
	{
		Path pathOld = Paths.get(oldName); //Ordner indem der zu ändernde Ordnder oder Datei liegt
		
		if( !Files.exists(pathOld, LinkOption.NOFOLLOW_LINKS))
		{
			return false;
		
		}
		else
		{
			Path pathNew = Paths.get(newName);
			//Aktueller Name
			File fileOld = new File(oldName);
			//Neuer name
			File fileNew = new File(newName);
			//Prüfen ob neuer Name schon vergeben ist
			if (fileNew.exists())
			{
				//neuer Dateiname existiert bereits
				return false;				
			}
			else
			{
				//Datei umbenennen
				return fileOld.renameTo(fileNew);
			}
		}

	
	}	
	
	/**
	* Funktion liefert den Betriebsystemnamen,
	* sowie Maschinenname und IP-Adresse des
	* FileSystems zurück
	* @return osname, hostname, hostAddress
	*/
	public String getOSName()
	{
		return this.osname;
	}

	public String getHostName() {return this.hostname;}

	public String getHostAddress() {return this.hostAddress;}

	/**
	* Prozedur liefert die gefundenen Dateien zurück
	* @return liste der gefundenen Dateien
	*/		
	public Path[] getFileListe()
	{
		return this.fileListe;
	}
	
	/**
	* Prozedur liefert die gefundenen Ordner zurück
	* @return liste der gefundenen Dateien
	*/			
	public Path[] getDirListe()
	{
		return this.dirListe;
	}	
}