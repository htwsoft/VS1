import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.nio.*;
import java.util.*;

public class FileSystemClient
{
	public static void main(String args[])
	{
		try
		{
			Socket socket = new Socket ("127.0.0.1", 6000);
			Scanner input = new Scanner(socket.getInputStream());
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			
			System.out.println("Parameter: " + args[0]);
			output.println(args[0]);
			
			String files = "";
			String dirs = "";
			String [] fileListe = null;
			String [] dirListe = null;
			files = input.nextLine();
			dirs = input.nextLine();
			fileListe = files.split("[;]");
			dirListe = dirs.split("[;]");
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
			System.exit(0);
		}
		catch(Exception e)
		{
			System.out.println("Fehler: " + e.getMessage());
		}
	}
}