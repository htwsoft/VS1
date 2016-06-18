/**
* Klasse zum Darstellen eines Dateisystems
* @author Marco Palumbo
* @version 1.0
*/

import java.io.*;
import java.nio.*;
import java.nio.file.*;

/**
* Klasse Filesystem dient zur leichteren Kommunikation/ Darstellung
* mit einem beliebigen FileSystem
*/
public class FileSystem
{	
	private String osname; //Name des Betriebsystems
	private Path [] dirListe; //Liste der gefundenen Ordner bei Browse
	private Path [] fileListe; //Liste der gefundenen Dateien bei Browse
	
	/**
	* Konstruktor der FileSystem Klasse
	* Initialisiert die internen variablen
	*/
	public FileSystem()
	{
		this.osname = System.getProperty("os.name");
		this.dirListe = null;
		this.fileListe = null;
	}
	
	/**
	* Prozedur durchsucht einen Ordner auf dessen Inhalt
	* und speichert die Daten in einer Dir- und FileListe
	* Wenn keine Datei oder Ordner gefunden wurden wird der übergebene Pfad gespeichert
	* @param String mit dem Ordnernamen der Untersucht werden soll (vollständiger Pfad)
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
	
	public void search()
	{
		System.out.println(" Noch nicht implementiert!");
	}
	
	public void create()
	{
		System.out.println(" Noch nicht implementiert!");
	}
	
	public void delete()
	{
		System.out.println(" Noch nicht implementiert!");
	}
	
	public void rename()
	{
		System.out.println(" Noch nicht implementiert!");
	}	
	
	/**
	* Funktion liefert den Betriebsystemname des 
	* FileSystems zurück
	* @return OS-Name
	*/
	public String getOSName()
	{
		return this.osname;
	}
	
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