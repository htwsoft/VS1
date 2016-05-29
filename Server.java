/**
* Ein kleines Server-Programm in Java
* @author mpalumbo
* @version 1.01
* @refact cpatzek / soezdemir //
*/
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class Server
{
	private static final int PORT_NR = 12345;
	private String nachricht;
	/**
	* Funktion bearbeitet eine CLient-Anfrage
	* @param client Socket der die Verbindung zum anfragenden Client repräsentiert
	*/
	private void bearbeiteAnfrage(Socket client) throws IOException
	{
		Scanner anfrage  = new Scanner(client.getInputStream());
		PrintWriter antwort = new PrintWriter(client.getOutputStream(), true);
		//Client-Text ausgeben
		erstelleAusgabe(anfrage.nextLine());
		antwort.println("Ok vom Server // Serverantwort");
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
		boolean isrunning = false;
		try
		{
			Server newserver = new Server();
			ServerSocket ssocket = new ServerSocket(PORT_NR);
			newserver.erstelleAusgabe("Server wurde gestartet!");
			isrunning = true;
			while(isrunning)
			{
				//Socket nimmt verbindungen an
				Socket client = ssocket.accept();
				newserver.bearbeiteAnfrage(client);
			}
			ssocket.close();
			newserver.erstelleAusgabe("Server wurde beendet!");
			isrunning = false;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
}
