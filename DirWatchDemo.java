/**
* Kleine Demo zu Demonstrieren der eigens geschriebenen DirWatch-Klasse
* @author Marco Palumbo
* @version 1.0
*/

import java.io.*;
import java.nio.*;
import java.nio.file.*;


/**
* Klasse zum Demostrieren der DirWatch-Klasse
*/
public class DirWatchDemo
{
	/**
	* Hauptprogramm durchsucht den übergebenen Ordner und 
	* gibt die gefundenen Ordner / Dateien aus
	* @param args[] Pfad der untersucht werden soll NUR EINER erlaubt
	*/
	public static void main(String args[])
	{		
		//Auselsend des verwendeten Betriebsystems
		System.out.println("");
		System.out.println("|-------------------------------------------------");
		System.out.println("| Verwendetes OS: " + System.getProperty("os.name"));
		System.out.println("|-------------------------------------------------");
		//Path objekt mit der übergebenen Datei oder des Ordners erstellen
		Path path = Paths.get(args[0]);
		//eigene Klasse DirWatcher um übergebenen Ordner auszulesen
		DirWatcher dw = new DirWatcher();
		//Liste um später die gefundenen Ordner auszugeben
		Path [] dliste = null;
		//Liste um später die gefundenen Dateien auszugeben
		Path [] fliste = null;
		try
		{
			//Intitialisieren des durchlaufs. Nötig das sonst alle Ordner
			//und derren unterordner durchsucht werden
			dw.initWalkFileTree();
			//Durchlaufen des Ordners
			Files.walkFileTree(path, dw);
			//Auslesend er gefundenen Dateien
			fliste = dw.getFileListe();
			//auslesen der gefundenen ordner
			dliste = dw.getDirListe();
			//ausgabe der gefundenen Dateien
			System.out.println("|-------------------------------------------------");
			System.out.println("| Anzahl Dateien: " + dw.getAnzFiles());
			System.out.println("|-------------------------------------------------");
			for(int i=0; i<fliste.length; i++)
			{
				System.out.println("| " + fliste[i]);
			}
			//ausgabe der gefundenen Ordner
			System.out.println("|-------------------------------------------------");
			System.out.println("| Anzahl Ordner: " + dw.getAnzDirs());
			System.out.println("|-------------------------------------------------");
			for(int j=0; j<dliste.length; j++)
			{
				System.out.println("| " + dliste[j]);
			}
			System.out.println("|-------------------------------------------------");
		}
        catch(IOException e) 
		{
            System.out.println("Fehler beim bearbeiten des Pfades!");
        }
		System.exit(0);		
	}
	
}