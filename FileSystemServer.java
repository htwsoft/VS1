import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.*;

/**
* Server der mit der Klasse FileSystem interagiert
* mit Thread 
*/
public class FileSystemServer
{
	/**
	* Hauptmethode des Servers wir beim Start aufgerufen
	* @param args[] erwartet eine Portnummer als Übergabe-Wert
	*/
	public static void main(String args[])
	{
		ServerSocket server = null; //Server der Anfragen annehmen soll
		Socket client = null; //Client der eine Anfrage an Server stellt
		int portnr = 0; //Portnr an die der Server gebunden werden soll
		ThreadWorker tw = null;
		//Prüfen ob portnr mitgeben wurde an die der Server
		//gebunden werden soll
		if(args.length == 1)
		{
			System.out.println("Parameter: " + args[0]);
			portnr = Integer.parseInt(args[0]);
			try
			{
				server = new ServerSocket(portnr);
				System.out.println("Server wurde an Port \"" + portnr + "\" gebunden ...");
				System.out.println("Server gestartet ...");
				while(true)
				{
					//Auf Anfrage warten
					client = server.accept();
					System.out.println("");
					System.out.println("Anfrage von IP \"" + client.getInetAddress().getHostName() 
						+ "\" mit Port " + client.getPort());
					//einen neuen Thread erstellen
					tw = new ThreadWorker(client);
					//Anfrage bearbeiten
					tw.start();
				}
			}
			catch(Exception e)
			{
				System.out.println("Fehler: " + e.getMessage());
			}
		}
		else
		{
			if(args.length > 1)
			{
				System.out.println("Es ist nur die Portnummer erlaubt!");
			}
			else
			{
				System.out.println("Bitte eine Portnummer angeben!");
			}
		}
	}
	
	// Diese Methode print() dient dazu, dass die beiden Threads
	// MainThread und WorkerThread beim konkurrierenden Zugriff auf 
	// die Konsole mit System.out.println() synchronisiert werden.
	/**
	* Methode wird von Threadworker zur System-Ausgabe benutzt
	* @param nachricht Text der auf dem Schirm angezeigt werden soll
	*/
	public static synchronized void print (String nachricht)
	{
		System.out.println (nachricht);
	}
}

/**
* Klasse ThreadWorker bearbeitet Anfrage eines Clients
*/
class ThreadWorker extends Thread
{
	private Socket client;
	
	private String fileListToString(FileSystem fs)
	{
		String result = ""; //Enthällt alle gefundenen Dateien in einem String
		Path [] fileListe = null;
		fileListe = fs.getFileListe();
		for(int i=0; i<fileListe.length; i++)
		{
			if(i>0)
			{
				result = result + ";" + fileListe[i] ;
			}
			else
			{
				result = result + fileListe[i];
			}		
		}
		return result;
	}
	
	private String dirListToString(FileSystem fs)
	{
		String result = ""; //Enthällt alle gefundenen Dateien in einem String
		Path [] dirListe = null;
		dirListe = fs.getDirListe();
		for(int i=0; i<dirListe.length; i++)
		{
			if(i>0)
			{
				result = result + ";" + dirListe[i];
			}
			else
			{
				result = result + dirListe[i];
			}
			
		}
		return result;
	}
	
	/**
	* Konstruktor der Klasse 
	* @param client Client dessen Anfrage bearbeitet werden soll
	*/
	public ThreadWorker(Socket client)
	{
		this.client = client;
	}
	
	/**
	* Methode bearbeitet die Anfrage des Clients
	*/
	public void run()
	{
		String antwort = "Ok";
		String anfrage = "";
		String pfad = "";
		String fileListe = "";
		String dirListe = "";
		//InputStream anfrage = null;
		//OutputStream output = null;
		FileSystem fs = new FileSystem();
		Scanner input = null;
		PrintWriter output = null;
		try
		{
			input = new Scanner(client.getInputStream());
			output = new PrintWriter(client.getOutputStream(), true);
			//Speichern der Anfrage in buffer
			anfrage = input.nextLine();
			FileSystemServer.print("Anfrage: " + anfrage);
			//Den angefragten Ordner durchsuchen
			fs.browse(anfrage);
			//eine Antwort an den Client senden
			//Senden der gefundenen Files
			fileListe = fileListToString(fs);	
			output.println(fileListToString(fs));
			//Senden der gefundenen Ordner
			dirListe = dirListToString(fs);	
			output.println(dirListToString(fs));			
			//Verbindung zu Client schliessen
			client.close();
			FileSystemServer.print("Anfrage bearbeitet!");
		}
		catch(Exception e)
		{
			FileSystemServer.print("Fehler beim bearbeiten der Anfrage: " + e.getMessage());
		}
	}
}
