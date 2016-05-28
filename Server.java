/**
* Ein kleines Server-Programm in Java
* @author Marco Palumbo
* @version 1.0
*/
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class Server
{
	private static final int portnr = 12345;
	private String nachricht = "";
	/**
	* Funktion bearbeitet eine CLient-Anfrage
	* @param client Socket der die Verbindung zum anfragenden Client repräsentiert
	*/
	private void bearbeiteAnfrage(Socket client) throws IOException
	{
		Scanner anfrage  = new Scanner( client.getInputStream() );
		PrintWriter antwort = new PrintWriter(client.getOutputStream(), true);
		//Client-Text ausgeben
		nachricht = anfrage.nextLine();
		erstelleAusgabe(nachricht);
		antwort.println("Ok");
		client.close();
	}
	
	/**
	* Funktion zeigt den übergebenen text mit der aktuellen Zeit an
	*/
	private void erstelleAusgabe(String text)
	{
		//fromatter gibt die Formatierung für den Zeitstring an
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd 'at' HH:mm:ss ");
		//auslesen der Zeit
		Date jetzt = new Date();
		//Ausgabe der Zeit mit übergebenem text
		System.out.println(formatter.format(jetzt) + ": " + text);		
	}
	
	/**
	* Hauptmethode des Servers erwartet als übergabe Parameter den
	* Port des Servers
	* @param args ein Parameter für port des servers
	*/
	public static void main(String[] args)
	{
		try
		{
			Server newserver = new Server();
			ServerSocket ssocket = new ServerSocket(portnr);
			newserver.erstelleAusgabe("Server wurde gestartet!");
			while(true)
			{
				//Socket nimmt verbindungen an
				Socket client = ssocket.accept();
				newserver.bearbeiteAnfrage(client);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			ssocket.close();
			newserver.erstelleAusgabe("Server wurde beendet!");
		}
		System.exit(0);
	}
}
