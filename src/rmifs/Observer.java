package rmifs;

/**
 * Klasse zum ueberwachen eines Ordners auf Aenderungen
 * >>> die Klasse sollte eigentlich WatchDog heissen,
 * oder Deutscher Schäferhund ;-)
 * @author Marco Palumbo
 * @version 1.01
 * @date 2016-09-16
 */

import java.lang.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.*;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;



/**
* Observer-Klasse zum ueberwachen eines Ordners auf Aenderungen
*/
public class Observer
{
	private WatchService watcher; //WatchService 
	private Path pfad; //Ordner der ueberwacht werden soll
	private String modifiedObject; //Namen einer gaenderten Datei oder eines Ordners
	
	/**
	* Kontruktor der Observer-Klasse
	* @param dir Ordner der überwacht werden soll
	*/
	public Observer(final String dir) throws IOException
	{
		//Erzeugen einer WatchServiceKlasse zur Ueberwachung eines Ordners
		this.watcher = FileSystems.getDefault().newWatchService();
		//Ordner der untersucht werden soll
		this.pfad = Paths.get(dir);
		//Registrieren des Watchers für den übergebenen pfad.
		//ENTRY_CREATE (fuer neu Anlagen), ENTRY_DELETE (für Änderungen), ENTRY_MODIFY(für Loeschungen)
		this.pfad.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY); 
		this.modifiedObject = "";
	}
	
	/**
	* Funktion wartet auf Aenderung innerhalb eines Ordners
	* @return liefert true zurueck wenn kein Fehler bei der Pruefung aufgetreten ist
	*/
	public boolean warteAufAenderung()
	{
		boolean valid = false; //Gibt an ob die Aenderungsart festgestellt werden konnte
		WatchKey key; //Element behinhaltet die Information einer Aenderung
		this.modifiedObject = "";
		try
		{
			key = this.watcher.take();
			//Abfangen der verschiedenen Aenderungsevents
			//Beim Aendern eines Dateinamens z.B. tritt ein ENTRY_CREATE und ein ENTRY_MODIFY Event auf
			for (WatchEvent<?> event: key.pollEvents()) 
			{
				//Event.kind gibt die Art der Aenderung an (Loeschung, neu Anlage, Aenderung)
				WatchEvent.Kind kind = event.kind();
				if (kind == OVERFLOW) 
				{
					//Fehler beim pruefen der Aenderung
					continue;
				}
				//Auslesen des Namens der geaenderten Datei oder des Ordners
                @SuppressWarnings("unchecked") //noetig um Warnings waehrend des compilens zu unterdrücken
				WatchEvent<Path> ev = (WatchEvent<Path>)event;
				this.modifiedObject =ev.context().toString();
			}
			//Reset gibt Event wieder frei um auf neue Aenderungen zu reagieren
			valid = key.reset();
			//Wenn valid = false existiert der registrierte Watcher nicht mehr
			//neues Objekte sollte dann erzeugt werden
			return valid;			
		}
		catch(Exception e) 
		{
			return false;
		}
	}

	/**
	* Funktion liefert den Namen der gaenderten Datei oder des Ordners zurück
	* @return Namen der gaenderten Datei oder des Ordners
	*/	
	public String getModifiedObject()
	{
		return this.modifiedObject;
	}
}