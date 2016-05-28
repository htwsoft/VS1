/**
* Ein kleines Verwalter-Programm in Java
* das eine Clientanfrage annimmt, an einen Server weiterleitet
* und die Antwort des Servers an den Client weiterleitet
* @author Marco Palumbo
* @version 1.0
*/
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class Verwalter
{
	/*
	 *lokale Verbindung fürs erste zum testen. anpassung für mehrere machinen möglich
	 */
	private const int serverport = 12345;
	private const String serverip = "localhost";
	private const int verwalterport = 6666;
	/**
	* Funktion leitet die Nachricht des Clients an den Hauptserver weiter
	*/
	private String nachrichtWeiterleiten(String nachricht, int serverport, String serverip) throws IOException
	{
		String serverantwort = "";
		Socket server = new Socket(serverip, serverport);
		//antwort liest die Antwort des Servers
		Scanner antwort  = new Scanner( server.getInputStream() );
		//Schreibkanal zum Server aufbauen
		//nachricht soll an Hauptserver gesendet werden
		anfrage.println(nachricht);
		//antwort des Servers einlesen
		serverantwort = antwort.nextLine();
		//Schreibkanal schliessen
		anfrage.close();
		//verbindung zu Server wieder trennen
		server.close();
		return serverantwort;
	}
	/**
	* Funktion bearbeitet eine CLient-Anfrage und leitet Sie an den Hauptserver weiter
	* @param client Socket der die Verbindung zum anfragenden Client repräsentiert
	*/
	private boolean bearbeiteAnfrage(Socket client, Verwalter newverwalter, int serverport, String serverip) throws IOException 
	{
		String nachricht = "";
		String serverantwort = "";
		//Anfrage beinhaltet die CLientanfrage
		Scanner anfrage  = new Scanner( client.getInputStream() );
		//Antwort sendt ok an den client 
		PrintWriter antwort = new PrintWriter(client.getOutputStream(), true);
		boolean ende = true; //gibt an ob der server beendet werden soll
		//Client-Text ausgeben
		nachricht = anfrage.nextLine();
		if(nachricht.equals("exit"))
		{
			newverwalter.erstelleAusgabe(nachricht);
			serverantwort = newverwalter.nachrichtWeiterleiten(nachricht, serverport, serverip);
			antwort.println(serverantwort);
			ende = true;
		}
		else
		{
			newverwalter.erstelleAusgabe(nachricht);
			serverantwort = newverwalter.nachrichtWeiterleiten(nachricht, serverport, serverip);
			antwort.println(serverantwort);
			ende = false;
		}
		client.close();
		return ende;
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
	* Hauptprogramm des Verwalters
	* Startet einen Verwalter Server fuer
	* Client-Anfragen
	* @param args benoetigt 3 Parameter verwalterport serverip serverport
	*/
	public static void main(String[] args) throws IOException
	{
		
		boolean ende = false;
		Verwalter newverwalter = new Verwalter();
		String nachricht = "";
		ServerSocket ssocket = new ServerSocket(verwalterport);
		newverwalter.erstelleAusgabe("Server wurde gestartet!");
		while(!ende)
		{
			//Socket nimmt verbindungen an
			Socket client = ssocket.accept();
			ende = newverwalter.bearbeiteAnfrage(client, newverwalter, serverport, serverip);	
		}
		ssocket.close();
		newverwalter.erstelleAusgabe("Server wurde beendet!");
	
		System.exit(0);	
	}

}
