/**
* Klasse zum durchlaufen und untersuchen der Directories
* wurde abgeleitet von der Java-Klasse "SimpleFileVisitor"
* @author Marco Palumbo
* @version 1.0
*/

import static java.nio.file.FileVisitResult.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

/**
* Klasse DirWatcher durchläuft einen Ordner
* und speichert Informationen über die Anzahl 
* der gefundenen Ordner Und Dateien und speichert
* diese in einer Liste
*/
public class DirWatcher extends SimpleFileVisitor<Path>
{
	private int anzFiles; //Variable gibt die Anzahl der gefundenen Dateien an
	private int anzDirs; //Variable gibt die Anzahl der gefundenen Ordner an
	private Path [] fileListe; //Liste mit gefundenen Dateien
	private Path [] dirListe; //Liste mit gefundenen Ordnern
	private boolean istStartOrdner; //gibt an ob das der erste Ordner beim durchlauf ist,
					//da mit dem übergebenen Ordner angefangen wird.
					//Benötigt da sonst alle Unterordner voll durchlaufen werden	
	/**
	* Konstruktor der DirWatch Klasse
	* Initialisiert die internen Variablen
	*/
	public DirWatcher()
	{
		this.anzDirs = 0;
		this.anzFiles = 0;
		this.istStartOrdner = true;
		fileListe = null;
		dirListe = null;
	}
	
	/**
	* Prozedur fügt eine Datei in die FileListe
	* @file Datei die gefunden wurde
	*/
	private void addFile(Path file)
	{
		Path [] newArray = new Path[this.anzFiles + 1];
		//Neue Datei in Liste speicher
		newArray[this.anzFiles] = file;
		//fileListe um 1 vergrößern
		if(this.anzFiles > 0)
		{
			System.arraycopy(fileListe, 0, newArray, 0, fileListe.length);
		}
		fileListe = newArray;
		this.anzFiles++;		
	}
	
	/**
	* Prozedur fügt einen Ordner in die DirListe
	* @dir Ordner der gefunden wurde
	*/	
	private void addDir(Path dir)
	{
		Path [] newArray = new Path[this.anzDirs + 1];
		//Neues Directory in Liste speicher
		newArray[this.anzDirs] = dir;
		//dirListe um 1 vergrößern
		if(this.anzDirs > 0)
		{
			System.arraycopy(dirListe, 0, newArray, 0, dirListe.length);
		}
		dirListe = newArray;
		this.anzDirs++;		
	}
	
	/**
	* Prozedur initilisiert vor einem WalkingTree
	* aufruf die variable istStartOrdner, um zu
	* vermeiden das alle Unterordner auch durchlaufen werden
	*/
	public void initWalkFileTree()
	{
		this.anzDirs = 0;
		this.anzFiles = 0;
		fileListe = null;
		dirListe = null;
		this.istStartOrdner = true;
	}
	
	/**
	* Prozedur wird ausgeführt wenn es sich um eine File handelt
	* @param file Datei die untersucht werden soll
	* @param attr Infos zu der Datei wie z.B. Änderungsdatum
	* @return FileVisitResult gibt an ob der Ordner weiter durchlaufen werden soll oder nicht
	*/
    @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attr) 
	{
       /* if (attr.isSymbolicLink()) 
		{
            System.out.format("| Symbolic link: %s \n", file);
        } else if (attr.isRegularFile()) 
		{
            System.out.format("| Regular file: %s \n", file);
        } else 
		{
            System.out.format("| Other: %s \n", file);
        }*/
		this.addFile(file);
        return CONTINUE;
    }

	/**
	* Prozedur wird ausgeführt wenn es sich um ein Directory handelt
	* für richtige ausführung muss vorher Methode "initWalkFileTree"
	* ausgeführt werden
	* @param dir Datei die untersucht werden soll
	* @param attrs Infos zu der Datei wie z.B. Änderungsdatum
	* @return FileVisitResult gibt an ob der Ordner weiter durchlaufen werden soll oder nicht
	*/
    @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) 
	{
		if(this.istStartOrdner)
		{
			//Als erstes wird immer der übergeben Ordner
			//aufgerufen. Dieser soll natürlich vollständig
			//durchlaufen werden
			this.istStartOrdner = false;
			return CONTINUE;
		}
		else
		{
			//Ordner innerhalb des Zielordners
			//Gibt an das der Inhalt des besuchten Ordners nicht untersucht werden soll
			//System.out.format("| Directory: %s%n", dir);
			this.addDir(dir);
			return SKIP_SUBTREE;
		}
    }

	/**
	* Prozedur wird bei einem Fehler aufgerufen
	* @param file Datei bei der der Fehler auftrat
	* @param exc Fehler der aufgetreten ist
	* @return FileVisitResult gibt an ob der Ordner weiter durchlaufen werden soll oder nicht	
	*/
    @Override public FileVisitResult visitFileFailed(Path file, IOException exc) 
	{
		//System.err.println(exc);
		return CONTINUE;
	}		
	
	/**
	* Prozedur liefert die Anzahl der gefundenen Dateien zurück
	* @return Anzahl gefundener Dateien
	*/
	public int getAnzFiles()
	{
		return this.anzFiles;
	}
	
	/**
	* Prozedur liefert die Anzahl der gefundenen Ordner zurück
	* @return Anzahl gefundener Ordner
	*/	
	public int getAnzDirs()
	{
		return this.anzDirs;
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