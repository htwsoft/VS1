package rmifs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * <br>HtwSoftClient-Klasse zum starten eines FileSystemClients</br>
 * @author soezdemir
 * @version 1.02
 * @date 2016-09-16
 */
public class HtwSoftClient
{
    private static final String FEHLER_VERBINDUNG_VERWALTER =
            "Die Verbindung zum lokalen Verwalter ist verloren gegangen!" +
            "\n Erneuter Verbindungsversuch in 5 Sekunden.";
    private static final String FEHLER_VERBINDUNG_VERWALTER_STARTUP =
            "Die Verbindung zum lokalen Verwalter konnte nicht hergestellt" +
            " werden bzw. sie wurde unterbrochen!\n Erneuter Verbindungsversuch in 5 Sekunden.";
    private static final String FEHLER_VERBINDUNG_VERWALTER_REMOTE =
            "\nSie sind entweder bereits mit diesem Verwalter verbunden " +
            "oder der angeforderte/lokale Verwalter ist aktuell offline!\n" +
            "Sollte das Problem bestehen bleiben, starten Sie bitte das Programm neu.\n";
    private static final String FEHLER_NUMBER_OF_ATTEMPTS_EXCEEDED =
            "Es kann keine Verbindung aktuell hergestellt werden!\n" +
            "Bitte starten Sie das Programm neu und versuchen Sie es erneut, falls das Problem bestehen bleibt," +
            "wenden Sie sich bitte an einen Administrator";
    private static final String FEHLER_KRITISCH = "Kritischer Fehler, das Programm beendet sich nun!\n";
    private static final String FEHLER_EINGABE = "Fehlerhafte Eingabe! Bitte ueberpruefen Sie ihre Eingabe!\n";
    private enum MENUE { CLOSE, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE,
                         RENAME, OS_NAME, SERVER_WAHL, VERWALTER_WAHL, FALSE }
    private static  String verwalterIp = "192.168.0.26";
    private static int verwalterPortNr = 4712;
    private static FileServerListenElement verwalterDaten = new FileServerListenElement("LocalVerwalter", verwalterIp, verwalterPortNr);
    private static int numberOfAttempts = 0;
    private static FileSystemClient client;

    /**
     * <br>Main() Funktion
     * initialisiert System Security Einstellungen und startet Anschließend den FileSystemClient</br>
     * @param args
     */
    public static void main(String args [])
    {
        init();
        start();
    }

    private static void init()
    {
        System.setProperty("java.security.policy", "policy/java.policy" );
        System.setProperty("java.rmi.server.hostname", verwalterIp);
    }

    private static void start()
    {
        try
        {
            client = new FileSystemClient(verwalterDaten.getServerPort(), verwalterDaten.getServerIP());
            NetworkController nc = new NetworkController(client);
            numberOfAttempts = 0;
            System.out.println(nc);
            System.out.println(client);
            client.browse();
            menue();
        }
        catch (RemoteException rex)
        {
            startupExceptionHandling();
        }
        catch (NotBoundException nbe)
        {
            startupExceptionHandling();
        }
    }

    /**
     * Bearbeitet Exceptions die beim start des Clients auftreten koennen
     */
    public static void startupExceptionHandling()
    {
        if(numberOfAttempts<3)
        {
            System.out.println(FEHLER_VERBINDUNG_VERWALTER_STARTUP);
            try
            {
                Thread.sleep(5000);
            }
            catch(InterruptedException ie)
            {
                System.out.println(FEHLER_KRITISCH);
                System.exit(1);
            }
            numberOfAttempts++;
            start();
        }
        else
            System.out.println(FEHLER_NUMBER_OF_ATTEMPTS_EXCEEDED);
    }

    public static MENUE intToMenue(int eingabe)
    {
        MENUE menue_eingabe = MENUE.FALSE;
        switch(eingabe)
        {
            case 0: menue_eingabe = MENUE.CLOSE; break;
            case 1: menue_eingabe = MENUE.BROWSE; break;
            case 2: menue_eingabe = MENUE.SEARCH; break;
            case 3: menue_eingabe = MENUE.CREATE_DIR; break;
            case 4: menue_eingabe = MENUE.CREATE_FILE; break;
            case 5: menue_eingabe = MENUE.DELETE; break;
            case 6: menue_eingabe = MENUE.RENAME; break;
            case 7: menue_eingabe = MENUE.OS_NAME; break;
            case 8: menue_eingabe = MENUE.SERVER_WAHL; break;
            case 9: menue_eingabe = MENUE.VERWALTER_WAHL; break;
            default: menue_eingabe = MENUE.FALSE; break;
        }
        return menue_eingabe;
    }

    private static void menue()
    {
        boolean remoteVerwalter = false;
        int eingabe = -1;
        FileServerListenElement tmp;
        MENUE menue_eingabe = MENUE.FALSE;
        try
        {

            while(menue_eingabe != MENUE.CLOSE)
            {
                eingabe = zeigeMenue();
                menue_eingabe = intToMenue(eingabe);
                switch(menue_eingabe)
                {
                    case CLOSE: System.out.println("Programm wurde beendet!"); break;
                    case BROWSE: client.browse(); break;
                    case SEARCH: client.search(); break;
                    case CREATE_DIR: client.createDir(); break;
                    case CREATE_FILE: client.createFile(); break;
                    case DELETE: client.delete(); break;
                    case RENAME: client.rename(); break;
                    case OS_NAME: client.osname(); break;
                    case SERVER_WAHL: client.setServer(serverWahl(), getServerLaenge()); break;
                    case VERWALTER_WAHL: remoteVerwalter = true;
                        connectVerwalter(verwalterWahl(), getVerwalterLaenge());
                        System.out.println("\nVerbinde nun zu neuem Verwalter!\n");
                        start();
                        break;
                    default: System.out.println("Falsche Eingabe!"); break;
                }
            }
        }
        catch(RemoteException rex)
        {
            if(remoteVerwalter)
            {
                System.out.println(FEHLER_VERBINDUNG_VERWALTER_REMOTE);
                menue();
            }
            else
            {
                System.out.println(FEHLER_VERBINDUNG_VERWALTER);
                try
                {
                    Thread.sleep(5000);
                    start();
                }
                catch(InterruptedException ie)
                {
                    System.out.println(FEHLER_KRITISCH);
                    System.exit(1);
                }
            }
        }
        catch(NotBoundException nex)
        {
            if(remoteVerwalter)
            {
                System.out.println(FEHLER_VERBINDUNG_VERWALTER_REMOTE);
                menue();
            }
            else
            {
                System.out.println(FEHLER_VERBINDUNG_VERWALTER);
                start();
            }
        }
        catch(IndexOutOfBoundsException ioobe)
        {
            System.out.println(FEHLER_EINGABE);
            menue();
        }
        catch(IOException ioe)
        {

        }
        System.exit(0);
    }

    /**
     * <br>Funktion zeigt ein Auswahlmenue und liefert
     * die Auswahl des Benutzers zurück</br>
     */
    private static int zeigeMenue () throws RemoteException, NotBoundException
    {
        //Scanner liste eingabe des Benutzers ein
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int eingabe = -1;
        while(eingabe < 0 || eingabe > 9)
        { //Auswahlmenue zeigen bis eingabe richtig
          try
            {
                System.out.println("------------------------------------------------------------");
                System.out.println("--------------------\t\tMenue\t\t--------------------");
                System.out.println("------------------------------------------------------------");
                System.out.println("0: Beenden");
                System.out.println("1: Browse");
                System.out.println("2: Search");
                System.out.println("3: Create Dir");
                System.out.println("4: Create File");
                System.out.println("5: Delete");
                System.out.println("6: Rename");
                System.out.println("7: OS-Name");
                System.out.println("8: Server waehlen");
                System.out.println("9: Verwalter waehlen");
                System.out.println("------------------------------------------------------------");
                System.out.print("Was moechten Sie tun?: ");
                eingabe = Integer.parseInt(br.readLine());
            }
            catch(IOException ioe)
            {
                System.out.println(FEHLER_EINGABE);
            }
        }
        System.out.println("");
        return eingabe;
    }

    private static int verwalterWahl() throws RemoteException, NotBoundException
    {
        String server="";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int eingabe = -1;
        int i = 0;
        int laenge = getVerwalterLaenge();
        System.out.println("Zu welchem Verwalter wollen Sie verbinden?");
        while(eingabe < 0 || eingabe > laenge)
        { //Auswahlmenue zeigen bis eingabe richtig
            try
            {
                client.getServerNames();
                System.out.println("---------------------------------------");
                System.out.println("        Verfuegbare Verwalter");
                while(!client.verwalterNames.get(i).contains("default"))
                {
                    System.out.println("\n"+i+": "+client.verwalterNames.get(i));
                    i++;
                }
                System.out.println(i+": Cancel");
                System.out.println("---------------------------------------");
                eingabe = Integer.parseInt(br.readLine());
            }
            catch(IOException ioe)
            {
                System.out.println(FEHLER_EINGABE);
            }
            catch(IndexOutOfBoundsException ioobe)
            {
                System.out.println(FEHLER_EINGABE);
            }
        }
        return eingabe;
    }
    /**
     * <br> Untermenue zur Auswahl eines File-Servers die am aktuellen Verwalter angebunden sind
     * @return Wahl des Servers als Ganzzahl
     * @throws RemoteException
     * @throws NotBoundException
     */
    private static int serverWahl() throws NotBoundException, IOException
    {
        String server="";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int eingabe = -1;
        int i = 0;
        int laenge = getServerLaenge();
        System.out.println("Auf welchem Server wollen Sie arbeiten?");

        while(eingabe < 0 || eingabe > laenge)
        { //Auswahlmenue zeigen bis eingabe richtig

            client.getServerNames();
            System.out.println("---------------------------------------");
            System.out.println("        Verfuegbare Server");
            while(!client.fileServerNames.get(i).contains("default"))
            {
                System.out.println("\n"+i+": "+client.fileServerNames.get(i));
                i++;
            }
            System.out.println(i+": Cancel");
            System.out.println("---------------------------------------");
            eingabe = Integer.parseInt(br.readLine());
        }
        return eingabe;
    }

    /**
     * <br> Fragt die tatsaechliche Laenge des ArrayList fileServerNames ab</br>
     * @return laenge des Arrays
     */
    private static int getServerLaenge()
    {
        int laenge = 0;
        while(!client.fileServerNames.get(laenge).contains("default"))
            laenge++;
        return laenge;
    }

    /**
     * <br> Fragt die tatsaechliche Laenge des ArrayList fileServerNames ab</br>
     * @return laenge des Arrays
     */
    private static int getVerwalterLaenge()
    {
        int laenge = 0;
        while(!client.verwalterNames.get(laenge).contains("default"))
            laenge++;
        return laenge;
    }
    private static void connectVerwalter(int verwalter, int laenge)throws RemoteException, NotBoundException
    {
        FileServerListenElement tmp;
        if(verwalter < laenge)
        {
            tmp = client.getNewVerwalter(verwalter);
            verwalterDaten.setServerIP(tmp.getServerIP());
            verwalterDaten.setServerPort(tmp.getServerPort());
        }
        else
            System.out.println("\nVorgang abgebrochen\n");
    }
}//ENDE