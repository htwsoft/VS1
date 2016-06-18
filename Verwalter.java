import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.io.*;



public class Verwalter
{
	private static final int VERWALTER_PORT = 6666;



	public static void main(String[] args) throws IOException {

		boolean bound = false;
		Verwalter newverwalter = new Verwalter();
		NachrichtenDienst nachrichtenDienst = new NachrichtenDienst();
		String nachricht = "Blub";

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		Registry registry = LocateRegistry.getRegistry(VERWALTER_PORT);

		for (int i = 0; !bound && i < 2; i++) {
			try {
				registry.rebind(nachricht, nachrichtenDienst);
				bound = true;
				System.out.println(nachricht + " bound to registry, port " +
						VERWALTER_PORT + ".");
			} catch (IOException ioe) {
				System.out.println("Rebinding " + nachricht + " failed, " +
						"retrying ...");
				registry = LocateRegistry.createRegistry(VERWALTER_PORT);
				System.out.println("Registry started on port " + VERWALTER_PORT + ".");
			}
		}
		System.exit(0);
	}
}







/**
* Ein kleines Verwalter-Programm in Java
* das eine Clientanfrage annimmt, an einen Server weiterleitet
* und die Antwort des Servers an den Client weiterleitet
* @author mpalumbo
* @version 1.01
* @refact cpatzek / soezdemir //
*
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public class Verwalter
{

	//lokale Verbindung fürs erste zum testen. anpassung für mehrere machinen möglich


	private static final int SERVER_PORT = 12345;
	private static final String SERVER_IP = "localhost";
	private static final int VERWALTER_PORT = 6666;
	/**
	* Funktion leitet die Nachricht des Clients an den Hauptserver weiter

	private String nachrichtWeiterleiten(String nachricht, int serverport, String serverip) throws IOException
	{
		String serverantwort = " ";
		Socket server = new Socket(serverip, serverport);
		//antwort liest die Antwort des Servers
		Scanner antwort  = new Scanner( server.getInputStream() );
		//Schreibkanal zum Server aufbauen
		PrintWriter anfrage = new PrintWriter(server.getOutputStream(),true);
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
	* @param client Socket der die Verbindung zum anfragenden Client repraesentiert

	private boolean bearbeiteAnfrage(Socket client, Verwalter newverwalter, int serverport, String serverip) throws IOException 
	{
		String nachricht = "nothing";
		String serverantwort = "nothing from server";
		//Anfrage beinhaltet die CLientanfrage
		Scanner anfrage  = new Scanner(client.getInputStream());
		//Antwort sendt ok an den client
		PrintWriter antwort = new PrintWriter(client.getOutputStream(), true);
		boolean ende = true; //gibt an ob der server beendet werden soll
		//Client-Text ausgeben
		nachricht = anfrage.nextLine();
		if(nachricht.equals("exit"))
		{
			erstelleAusgabe(nachricht);
			serverantwort = nachrichtWeiterleiten(nachricht, serverport, serverip);
			antwort.println(serverantwort);
			ende = true;
		}
		else
		{
			erstelleAusgabe(nachricht);
			serverantwort = nachrichtWeiterleiten(nachricht, serverport, serverip);
			antwort.println(serverantwort);
			ende = false;
		}
		client.close();
		return ende;
	}
	
	/**
	* Funktion zeigt den übergebenen text mit der aktuellen Zeit an

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

	public static void main(String[] args) throws IOException
	{
		
		boolean bound = false;
		Verwalter newverwalter = new Verwalter();
		NachrichtenDienst nachrichtenDienst = new NachrichtenDienst();
		String nachricht = "Blub";

		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager (new RMISecurityManager());
		}

		Registry registry = LocateRegistry.getRegistry(VERWALTER_PORT);
		newverwalter.erstelleAusgabe("Verwalter wurde gestartet!");

		for (int i = 0; ! bound && i < 2; i++)
		{
			try
			{
				registry.rebind(nachricht, nachrichtenDienst);
				bound = true;
				System.out.println(nachricht + " bound to registry, port " +
						           VERWALTER_PORT + ".");
			}
			catch(IOException ioe)
			{
				System.out.println ("Rebinding " + nachricht + " failed, " +
						"retrying ...");
				registry = LocateRegistry.createRegistry (VERWALTER_PORT);
				System.out.println ("Registry started on port " + VERWALTER_PORT +
						".");
			}
		}
		System.exit(0);
	}

}
*/