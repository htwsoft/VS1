/**
 * Created by Christian Patzek on 18.06.2016.
 *
 */
import java.rmi.server.*;
import java.rmi.registry.*;




public class RMIVerwalter implements Nachricht
{
    private static final int VERWALTER_PORT = 6666;

    public String nachricht(String auswahl)
    {
        switch(auswahl)
        {
            case "Browse":
                return("Dies ist die Browse-Funktion");

            case "Search":
                return("Dies ist die Search-Funktion");

            case "Create":
                return("Dies ist die Create-Funktion");

            case "Delete":
                return("Dies ist die Delete_funktion");

            default:
                return("Fehlerhafte Eingabe");
        }
    }

    public static void main(String[] args)
    {
        try
        {
            RMIVerwalter verwalter = new RMIVerwalter();

            Nachricht stub = (Nachricht) UnicastRemoteObject.exportObject(verwalter, VERWALTER_PORT);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Hello", stub);
        }
        catch(Exception e)
        {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
