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
	/**
	* Funktion bearbeitet eine CLient-Anfrage
	* @param client Socket der die Verbindung zum anfragenden Client repräsentiert
	*/
	private boolean bearbeiteAnfrage(Socket client, Server newserver) throws IOException
	{
		String nachricht = "";
		Scanner anfrage  = new Scanner( client.getInputStream() );
		PrintWriter antwort = new PrintWriter(client.getOutputStream(), true);
		boolean ende = true; //gibt an ob der server beendet werden soll
		//Client-Text ausgeben
		nachricht = anfrage.nextLine();
		if(nachricht.equals("exit"))
		{
			newserver.erstelleAusgabe(nachricht);
			antwort.println("Ok");
			ende = true;
		}
		else
		{
			newserver.erstelleAusgabe(nachricht);
			antwort.println("Ok");
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
	* Hauptmethode des Servers erwartet als übergabe Parameter den
	* Port des Servers
	* @param args ein Parameter für port des servers
	*/
	public static void main(String[] args) throws IOException
	{
		/*
		* prüfen ob Port übergeben wurde
		*/
		if(args.length == 2)
		{
			boolean ende = false;
			Server newserver = new Server();
			//Port wurde mitgeliefert
			int portnr = Integer.parseInt(args[0]);
			ServerSocket ssocket = new ServerSocket(portnr);
			newserver.erstelleAusgabe("Server wurde gestartet!");
			while(!ende)
			{
				//Socket nimmt verbindungen an
				Socket client = ssocket.accept();
				ende = newserver.bearbeiteAnfrage(client, newserver);
				
			}
			ssocket.close();
			newserver.erstelleAusgabe("Server wurde beendet!");
		}
		else
		{
			System.out.println("Zu viele oder zu wenige Parameter!");	
		}
		System.exit(0);
	}
	
}
