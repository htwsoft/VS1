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
    private static final String FEHLER_VERBINDUNG_VERWALTER = "Die Verbindung zum Verwalter konnte nicht hergestellt" +
            " werden bzw. sie wurde unterbrochen!\n Bitte versuchen versuchen Sie es spaeter noch einmal!\n" +
            "Sollte das Problem weiterhin bestehen, dann starten Sie das Programm neu.\n";
    private static final String FEHLER_EINGABE = "Fehlerhafte Eingabe! Bitte ueberpruefen Sie ihre Eingabe!\n";
    private enum MENUE { CLOSE, LIST, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE,
                         RENAME, OS_NAME, SERVER_WAHL, FALSE }
    private final static String SERVER_HOST_IP = "192.168.0.26";
    private final static int VERWALTER_PORT_NR = 4712;

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
        System.setProperty("java.rmi.server.hostname", SERVER_HOST_IP);
    }

    private static void start()
    {
        try
        {

            client = new FileSystemClient(VERWALTER_PORT_NR, SERVER_HOST_IP);
            NetworkController nc = new NetworkController(client);
            System.out.println(nc);
            System.out.println(client);
            client.browse();
            menue();
        }
        catch (RemoteException rex)
        {
            System.out.println(FEHLER_VERBINDUNG_VERWALTER);
            start();
        }
        catch (NotBoundException nbe)
        {
            System.out.println(FEHLER_VERBINDUNG_VERWALTER);
            start();
        }
    }

    public static MENUE intToMenue(int eingabe)
    {
        MENUE menue_eingabe = MENUE.FALSE;
        switch(eingabe)
        {
            case 0: menue_eingabe = MENUE.CLOSE; break;
            case 1: menue_eingabe = MENUE.LIST; break;
            case 2: menue_eingabe = MENUE.BROWSE; break;
            case 3: menue_eingabe = MENUE.SEARCH; break;
            case 4: menue_eingabe = MENUE.CREATE_DIR; break;
            case 5: menue_eingabe = MENUE.CREATE_FILE; break;
            case 6: menue_eingabe = MENUE.DELETE; break;
            case 7: menue_eingabe = MENUE.RENAME; break;
            case 8: menue_eingabe = MENUE.OS_NAME; break;
            case 9: menue_eingabe = MENUE.SERVER_WAHL; break;
            default: menue_eingabe = MENUE.FALSE; break;
        }
        return menue_eingabe;
    }

    private static void menue() throws RemoteException, NotBoundException
    {

        int eingabe = -1;
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
                    case LIST: client.list(); break;
                    case BROWSE: client.browse(); break;
                    case SEARCH: client.search(); break;
                    case CREATE_DIR: client.createDir(); break;
                    case CREATE_FILE: client.createFile(); break;
                    case DELETE: client.delete(); break;
                    case RENAME: client.rename(); break;
                    case OS_NAME: client.osname(); break;
                    case SERVER_WAHL: client.setServer(serverWahl()); break;
                    default: System.out.println("Falsche Eingabe!"); break;
                }
            }
        }
        catch (IOException ioe)
        {
            System.out.println(FEHLER_EINGABE);
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
                System.out.println("1: Server List");
                System.out.println("2: Browse");
                System.out.println("3: Search");
                System.out.println("4: Create Dir");
                System.out.println("5: Create File");
                System.out.println("6: Delete");
                System.out.println("7: Rename");
                System.out.println("8: OS-Name");
                System.out.println("9: Server waehlen");
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

    private static int serverWahl() throws RemoteException, NotBoundException
    {
        String server="";
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int eingabe = -1;
        System.out.println("Auf welchem Server wollen Sie arbeiten?");
        while(eingabe < 0 || eingabe > 3)
        { //Auswahlmenue zeigen bis eingabe richtig
            try
            {
                client.getServerNames();
                System.out.println("---------------------------------------");
                System.out.println("        Verfuegbare Server");
                System.out.println("0: Cancel\n1: "+client.fileServerNames[0]+"\n2: "
                                    +client.fileServerNames[1]+"\n3: "
                                    +client.fileServerNames[2]+"\n");
                System.out.println("---------------------------------------");
                eingabe = Integer.parseInt(br.readLine());
            }
            catch(IOException ioe)
            {
                System.out.println(FEHLER_EINGABE);
            }
        }
        return eingabe;
    }
}//ENDE