import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FileSystemClient
{
	public static void main(String args[]) 
	{
		if (System.getSecurityManager() == null) 
		{
			System.setSecurityManager(new SecurityManager());
		}
		try 
		{

			String eing = "";
			int serverPort = 0;
			serverPort = Integer.parseInt(args[0]);	
			
			Scanner eingabe = new Scanner(System.in);
			System.out.print("Ordner/Datei eingeben: ");
			eing = eingabe.nextLine();
			
			Registry registry = LocateRegistry.getRegistry(serverPort);
			FSInterface fsserver = (FSInterface) registry.lookup("FileSystemServer");
			
			String erg = "";
			String [] dirListe;
			String [] fileListe;	
			
			erg = fsserver.browseDirs(eing);
			dirListe = erg.split("[;]");		
			
			erg = fsserver.browseFiles(eing);
 			fileListe = erg.split("[;]");
 			
			System.out.println("File-Liste");
			System.out.println("---------------------------------------------------------------");
			for(int i=0; i<fileListe.length; i++)
			{
				System.out.println(fileListe[i]);
			}
			System.out.println("");
			System.out.println("Dir-Liste");
			System.out.println("---------------------------------------------------------------");
			for(int j=0; j<dirListe.length; j++)
			{
				System.out.println(dirListe[j]);
			}	
			
		} 
		catch (Exception e) 
		{
			System.err.println("ComputeCient exception:");
			e.printStackTrace();
		}
	}    
}