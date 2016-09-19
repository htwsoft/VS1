package rmifs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * HtwSoftClient-Klasse zum starten eines FileSystemClients
 * @author soezdemir
 * @version 1.02
 * @date 2016-09-16
 */
public class HtwSoftClient {

    private enum MENUE { CLOSE, LIST, BROWSE, SEARCH, CREATE_DIR, CREATE_FILE, DELETE, RENAME, OS_NAME, FALSE }

    private final static String SERVER_HOST_IP = "192.168.0.26";
    private final static int VERWALTER_PORT_NR = 4712;

    private static FileSystemClient client;

    /**
     * Main() Funktion
     * initialisiert System Security Einstellungen und startet Anschließend den FileSystemClient
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
            try{

                client = new FileSystemClient(VERWALTER_PORT_NR, SERVER_HOST_IP);
                NetworkController nc = new NetworkController(client);
                System.out.println(nc);
                System.out.println(client);
                menue();
            }
            catch (RemoteException rex)
            {
                rex.printStackTrace();
            }
            catch (NotBoundException nbe)
            {
                nbe.printStackTrace();
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
            default: menue_eingabe = MENUE.FALSE; break;
        }
        return menue_eingabe;
    }

        private static void menue(){

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
                        default: System.out.println("Falsche Eingabe!"); break;
                    }
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            System.exit(0);
        }

    /**
     * Funktion zeigt ein Auswahlmenue und liefert
     * die Auswahl des Benutzers zurück
     */
    private static int zeigeMenue ()
    {
        //Scanner liste eingabe des Benutzers ein
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        int eingabe = -1;
        while(eingabe < 0 || eingabe > 8)
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
                System.out.println("------------------------------------------------------------");
                System.out.print("Was moechten Sie tun?: ");
                eingabe = Integer.parseInt(br.readLine());
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        }
        System.out.println("");
        return eingabe;
    }
}//ENDE