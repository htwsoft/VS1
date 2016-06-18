/**
 * Created by Christian Patzek on 18.06.2016.
 *
 */
import java.rmi.registry.*;
import java.util.Scanner;

public class RMIClient
{
    private static final String SERVER_IP = "localhost";

    private static void menue(Registry registry) throws Exception
    {
        String eingabe = "";
        while(!eingabe.equalsIgnoreCase("exit"))
        {
            Scanner sc = new Scanner(System.in);
            System.out.println("--------------------------------------------");
            System.out.println("Bitte waehlen Sie unter folgenden Optionen: ");
            System.out.println("Browse");
            System.out.println("Search");
            System.out.println("Create");
            System.out.println("Delete");
            System.out.println("--------------------------------------------");
            if (sc.hasNext())
            {
                eingabe = sc.nextLine();
                lookupRegistry(registry, eingabe);
            }
            else
                System.out.println("Fehlerhafte Eingabe");
        }
    }

    private static void lookupRegistry(Registry registry , String auswahl) throws Exception
    {
        Nachricht stub = (Nachricht) registry.lookup("Hello");

        String response = stub.nachricht(auswahl);
        System.out.println("response: " + response);
    }

    public static void main()
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry(SERVER_IP);
            menue(registry);
        }
        catch(Exception e)
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
