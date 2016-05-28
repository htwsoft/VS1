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
	//Port und IP des (Verwalter-)Servers
	private static final int SERVER_PORT = 6666;
	private static final String SERVER_IP = "localhost";
	//Nachricht die an den Server gesendet werden soll
	private String Nachricht = "";
	//Dient zum einlesen der nachricht-eingabe des Benutzers ein
	//private static InputStreamReader isr = new InputStreamreader(System.in);
	//private static BufferedReader br = new BufferedReader(isr);
	
	/**
	* Funktion zeigt ein Auswahlmenue und liefert 
	* die Auswahl des Benutzers zurück
	*/
	private void verbindungsaufbau() throws IOException
	{
		//Verbindung zu Server aufbauen
		Socket server = new Socket(SERVER_IP, SERVER_PORT);
		nachrichtuebertragen(server);
	}
	
	private void auswahlRealisieren(int eingabe) throws IOException;
	{
		switch(eingabe)
		{
			case 1:
				verbindungsaufbau();
				break;
			case 2:
				Socket server = new Socket(SERVER_IP, SERVER_PORT);
				Scanner antwort  = new Scanner( server.getInputStream() );
				//Schreibkanal zum Server aufbauen
				PrintWriter anfrage = new PrintWriter(server.getOutputStream(), true);		
				//server soll beendet werden
				anfrage.println("exit");
				erstelleAusgabe("Server wurde beendet!");
				anfrage.close();
				server.close();
				break;
			default:
				System.out.println("Fehlerhafte Eingabe!");
		}
	}
	
	private void nachrichtUebertragen(Socket server) throws IOException
	{
		InputStreamReader isr = new InputStreamreader(System.in);
		BufferedReader br = new BufferedReader(isr);
		Scanner antwort  = new Scanner( server.getInputStream() );
		//Schreibkanal zum Server aufbauen
		PrintWriter anfrage = new PrintWriter(server.getOutputStream(), true);		
		//nachricht soll an server gesendet werden
		System.out.print("Nachricht eingeben: ");
		nachricht = br.readLine();
		anfrage.println(nachricht);
		newclient.erstelleAusgabe(antwort.nextLine());
		isr.close();
		br.close();
		server.close();
		anfrage.close();
	}
	
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
				auswahlRealisieren(eingabe);
			}
			catch(IOException e)
			{
				ioe.printStackTrace();
			}
			finally
			{
				br.close();
				isr.close();
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
	* @param args ein Parameter für ip und port des server
	*/
	public static void main(String[] args) throws UnknownHostException
	{
		Client newclient = new Client();
		while(newclient.zeigeMenue == 1)
		{
		}
		newclient.erstelleAusgabe("Client wurde beendet!");	
		System.exit(0);
	}
}
