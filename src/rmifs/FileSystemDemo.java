/**
* Kleine Demo zu Demonstrieren der eigens geschriebenen FileSystem-Klasse
* @author Marco Palumbo
* @version 1.0
*/
package rmifs;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.file.*;

/**
* Klasse zum Demostrieren der FileSystem-Klasse
*/
public class FileSystemDemo
{
	private FileSystem fs; //FileSystem-Klasse
	
	/**
	* Konstruktor 
	* erzeugt eine FileSystem-Klasse
	*/
	public FileSystemDemo()
	{
		this.fs = new FileSystem();
	}
	
	/**
	* Führt die Brwows-Methode der FileSystem-Klasse aus
	*/
	private void browse()
	{
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll untersucht werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			//Liste um später die gefundenen Ordner auszugeben
			Path [] dliste = null;
			//Liste um später die gefundenen Dateien auszugeben
			Path [] fliste = null;
			//Durchlaufen des Ordners
			fs.browse(pfad);
			//Auslesend er gefundenen Dateien
			fliste = fs.getFileListe();
			//auslesen der gefundenen ordner
			dliste = fs.getDirListe();
			//ausgabe der gefundenen Dateien
			System.out.println("|--------------- Files ---------------------------");
			for(int i=0; i<fliste.length; i++)
			{
				System.out.println("| " + fliste[i]);
			}
			//ausgabe der gefundenen Ordner
			System.out.println("|-------------------------------------------------");
			System.out.println("");
			System.out.println("|--------------- Dirs ----------------------------");
			for(int j=0; j<dliste.length; j++)
			{
				System.out.println("| " + dliste[j]);
			}
			System.out.println("|-------------------------------------------------");		  
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}
	}
	
	private void search()
	{
	
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Was soll gesucht werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			this.fs.search(pfad, "/");
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}			
	}

	private void create()
	{
		String pfad = "";
		Scanner eingabe = new Scanner(System.in);
		System.out.print("Welcher Ordner soll erstellt werden?: ");
		pfad = eingabe.nextLine();
		try
		{
			this.fs.create(pfad, "dir");
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
			this.fs.delete(pfad);
		}
		catch(Exception e)
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
			this.fs.rename(oldName, newName);
		}
		catch(IOException e)
		{
			System.out.println("Fehler: " + e.getMessage());	
		}	
	}	
	
	private void osname()
	{
		System.out.println("|-------------------------------------------------");
		System.out.println("| Verwendetes OS: " + this.fs.getOSName());
		System.out.println("|-------------------------------------------------");
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
		while(eingabe < 0 || eingabe > 6)
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
				System.out.println("3: Create");
				System.out.println("4: Delete");
				System.out.println("5: Rename");
				System.out.println("6: OS-Name");
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
	* Hauptmethode der Demo
	* startet eine Menue
	* @param args übergeben Parameter nicht ausgewertet
	*/
	public static void main(String args[])
	{
		FileSystemDemo fsd = new FileSystemDemo();
		int eingabe = -1;
		while(eingabe != 0)
		{
			eingabe = fsd.zeigeMenue();
			switch(eingabe)
			{
				case 0: System.out.println("Programm wurde beendet!"); break;
				case 1: fsd.browse(); break;
				case 2: fsd.search(); break;
				case 3: fsd.create(); break;
				case 4: fsd.delete(); break;
				case 5: fsd.rename(); break;
				case 6: fsd.osname(); break;
				default: System.out.println("Falsche Eingabe!"); break;
			}
		}
		System.exit(0);
	}
	
}