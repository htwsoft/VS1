/**
* Ein kleines Client-Programm in Java
* @author Marco Palumbo
* @version 1.0
*/
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class Client
{
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
		while(eingabe < 0 || eingabe > 2)
		{
			//Auswahlmenue zeigen bis eingabe richtig
			try
			{
				System.out.println("");
				System.out.println("---------------------");
				System.out.println("Menue:");
				System.out.println("0: Client beenden");
				System.out.println("1: Nachricht senden");
				System.out.println("2: Server beenden");
				System.out.println("---------------------");
				System.out.print("Was moechten Sie tun?: ");
				eingabe = Integer.parseInt(br.readLine());
			}
			catch(Exception e)
			{
				System.out.println("Fehlerhafte Eingabe!");
			}
		}
		return eingabe;
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
	* IP und Port des Servers
	* @param args ein Parameter für ip und port des servers
	*/
	public static void main(String[] args) throws IOException, UnknownHostException
	{
		/*
		* prüfen ob Port übergeben wurde
		*/
		if(args.length == 2)
		{
			//Dient zum einlesen der nachricht-eingabe des Benutzers ein
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			//Benutzeringabe der Menuauswahl
			int eingabe = 1;
			//Nachricht die an den Server gesendet werden soll
			String nachricht = "";
			//
			Client newclient = new Client();
			//Port wurde mitgeliefert
			String serverip = args[0];
		    //IP wurde mitgeliefert
			int portnr = Integer.parseInt(args[1]);
      		//Client mit Server socket verbinden
			//Dem Server eine Nachricht senden
			while( eingabe == 1 )
			{
			
				eingabe = newclient.zeigeMenue();
				if(eingabe == 1)
				{
					Socket server = new Socket(serverip, portnr);
					Scanner antwort  = new Scanner( server.getInputStream() );
					//Schreibkanal zum Server aufbauen
					PrintWriter anfrage = new PrintWriter(server.getOutputStream(), true);		
					//nachricht soll an server gesendet werden
					System.out.print("Nachricht eingeben: ");
					nachricht = br.readLine();
					anfrage.println(nachricht);
					newclient.erstelleAusgabe(antwort.nextLine());
					//Schreibkanal schliessen
					anfrage.close();
					//verbindung zu Server wieder trennen
					server.close();
				}
				else
				if(eingabe == 2)
				{
					Socket server = new Socket(serverip, portnr);
					Scanner antwort  = new Scanner( server.getInputStream() );
					//Schreibkanal zum Server aufbauen
					PrintWriter anfrage = new PrintWriter(server.getOutputStream(), true);		
					//server soll beendet werden
					anfrage.println("exit");
					newclient.erstelleAusgabe("Server wurde beendet!");
				}
			}
			newclient.erstelleAusgabe("Client wurde beendet!");	
		}
		else
		{
			System.out.println("Zu viele oder zu wenige Parameter!");	
		}
		System.exit(0);
	}
	
}
