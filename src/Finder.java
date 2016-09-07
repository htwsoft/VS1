/**
* Klasse zum suchet eine Datei innerhalb eines Systems
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
*  Finder Klasse zum suchen einer Datei
*  13.07.2016: 12:06 - 12:40
*/
public class Finder extends SimpleFileVisitor<Path> {
	private final PathMatcher matcher; //Datei-/Ordnermuster nachdem gesucht wird 
	private Path [] foundFiles; //Dateien / Ordner auf die der Suchbegriff passt
	private int numMatches;
	
	/**
	* Konstruktor der Finder Klasse
	* @param pattern Name bzw. muster der Datei
	*/
	Finder(String file) 
	{
		matcher = FileSystems.getDefault().getPathMatcher("glob:" + file);
		this.foundFiles = null;
		this.numMatches = 0;
	}
	
	/**
	* Prozedur fuegt eine Datei in die foundFiles
	* @file datei die gefunden wurde
	*/	
	private void addFile(Path file)
	{
		Path [] newArray = new Path[this.numMatches + 1];
		//Neues Directory in Liste speicher
		newArray[this.numMatches] = file;
		//dirListe um 1 vergroessern
		if(this.numMatches > 0)
		{
			System.arraycopy(foundFiles, 0, newArray, 0, foundFiles.length);
		}
		foundFiles = newArray;
		this.numMatches++;		
	}
	
	/**
	* Compares the glob pattern against
	* the file or directory name
	* @param file Name der Datei nach der gesucht wird
	*/
	void find(Path file) 
	{
		Path name = file.getFileName();
		if (name != null && matcher.matches(name)) 
		{
			addFile(name);
		}
	}
	
	/**
	* Prints the total number of
	* matches to standard out.
	*/
	void done() 
	{
		System.out.println("Matched: " + numMatches);
	}
	
	/**
	* Prozedur wird ausgefuehrt wenn eine Datei gefunden wurde
	* @param file Datei die untersucht werden soll
	* @param attr Infos zu der Datei wie z.B. Aenderungsdatum
	* @return FileVisitResult gibt an ob der Ordner weiter durchlaufen werden soll oder nicht
	*/
	@Override public FileVisitResult visitFile(Path file,
		BasicFileAttributes attrs) 
	{
		find(file);
		return CONTINUE;
	}
	
	/**
	* Prozedur wird ausgefuehrt wenn eine Ordner gefunden wurde
	* @param file Datei die untersucht werden soll
	* @param attr Infos zu der Datei wie z.B. Aenderungsdatum
	* @return FileVisitResult gibt an ob der Ordner weiter durchlaufen werden soll oder nicht
	*/
	@Override public FileVisitResult preVisitDirectory(Path dir,
		BasicFileAttributes attrs) 
	{
		//find(dir);
		return CONTINUE;
	}

	@Override public FileVisitResult visitFileFailed(Path file,
		IOException exc) 
	{
            //System.err.println(exc);
            return CONTINUE;
    }
	/**
	* Funktion liefert die Anzahl der gefundenen Treffer 
	* die zu der uebergebenen Datei passen zurueck
	*/
	public int getNumMatches()
	{
		return this.numMatches;
	}
	
	/**
	* Funktion liefert die gefundenen Matches/Treffer zurueck 
	*/
	public Path [] getFoundFiles()
	{
		return this.foundFiles;
	}
}