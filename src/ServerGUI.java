/**
 * Created by Eugen Eberle on 18.08.2016.
 */

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.nio.file.*;
import java.rmi.*;


public class ServerGUI extends JFrame implements FSInterface, ActionListener
{
    private JTextField portTextFeld;
    private JButton starteServerButton;
    private JTextArea serverTextArea;
    private JPanel serverPanel;

    private FileSystem fs = new FileSystem();
    public static FSInterface fsServer = new ServerGUI();

    public ServerGUI()
    {
        JFrame frame = new JFrame("ServerGUI");
        frame.setContentPane(serverPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1000, 400);
        serverTextArea.append("Hallo \n\n");
        starteServerButton.addActionListener(this);

        //Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad angeben
        ImageIcon img = new ImageIcon("htw.png");
        frame.setIconImage(img.getImage());
    }

    /**
     * Button gedrückt
     */
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();
        if(o == starteServerButton)
        {
            int serverPort;
            try
            {
                serverPort = Integer.parseInt(portTextFeld.getText().trim());
            } catch(Exception er)
            {
                append("Fehler bei der Port-Eingabe.\n");
                return;
            }
            try
            {
                //Security Manager ermöglicht/regelt zugriff auf Klasse
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager ( new SecurityManager() );
                }
                //Stellt das Objekt dem System zur Verfügung
                FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsServer, serverPort);
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry =  LocateRegistry.createRegistry(serverPort);
                //Objekt an registry binden
                registry.rebind("FileSystemServer", stub);
                append("Server bound ...\n");
            }
            catch(Exception e2)
            {
                append("Fehler: " + e2.toString());
            }

            // Button deaktivieren nach Start
            starteServerButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
        }
    }

    /**Zum zum anhaengen von Text in die TextArea*/
    void append(String text)
    {
        serverTextArea.append(text);
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
    }

    /**
     * Funktion sucht alle Ordner eines angegebenen Directory
     * @param dir Ordner der durchsucht werden soll
     * @return einen String mit allen gefunden Ordner durch ";" getrennt
     */
    public String browseDirs(String dir) throws RemoteException
    {
        Path [] dirListe;
        String ergListe = "";
        append("\nFunktion: browseDirs - Param: " + dir + "\n");
        try
        {
            this.fs.browse(dir);
            dirListe = this.fs.getDirListe();
            for(int i=0; i<dirListe.length; i++)
            {
                if(i>0)
                {
                    ergListe = ergListe + ";" + dirListe[i] ;
                }
                else
                {
                    ergListe = ergListe + dirListe[i];
                }
            }
            //prüfen ob ein Ordner gefunden wurde
            //wenn nicht ist ergListe = dir
            if( ergListe.equals(dir) )
            {
                ergListe = "";
            }
        }
        catch(Exception e)
        {
            ergListe = "";
            append("Funktion: " + e.toString() +"\n");
        }
        append("Return: \"" + ergListe + "\"");
        return ergListe;
    }

    /**
     * Funktion sucht alle Dateien eines angegebenen Directory
     * @param file Ordner der durchsucht werden soll
     * @return einen String mit allen gefunden Dateien durch ";" getrennt
     */
    public String browseFiles(String file) throws RemoteException
    {
        Path [] fileListe;
        String ergListe = "";
        append("\nFunktion: browseFiles - Param: " + file + "\n");
        try
        {
            this.fs.browse(file);
            fileListe = this.fs.getFileListe();
            for(int i=0; i<fileListe.length; i++)
            {
                if(i>0)
                {
                    ergListe = ergListe + ";" + fileListe[i] ;
                }
                else
                {
                    ergListe = ergListe + fileListe[i];
                }
            }
            //prüfen ob eine Datei gefunden wurde
            //wenn nicht ist ergListe = file
            if( ergListe.equals(file) )
            {
                ergListe = "";
            }
        }
        catch(Exception e)
        {
            ergListe = "";
            append("Fehler: " + e.toString() + "\n");
        }
        append("Return: \"" + ergListe  + "\"" + "\n");
        return ergListe;
    }

    /**
     * Funktion sucht nach der übergebenen Datei ab dem angegebenen Ordner
     * @param file Datei nach der gesucht werden soll
     * @param startDir Ordner ab dem die Datei gesucht werden soll
     * @return Liste mit Dateien die auf den Such-String passen mit ";" getrennt
     */
    public String search(String file, String startDir) throws RemoteException
    {
        append("Funktion: search - Params: " + file + ", " + startDir + "\n");
        Path [] fileListe = null;
        String ergListe = "";
        try
        {
            //search liefert true zurueck wenn mindestens eine Datei
            //gefunden wurde
            if( this.fs.search(file, startDir) )
            {
                //Gefundene Dateien speichern und als String
                //zurück liefern
                fileListe = this.fs.getFileListe();
                for(int i=0; i<fileListe.length; i++)
                {
                    if(i>0)
                    {
                        ergListe = ergListe + ";" + fileListe[i] ;
                    }
                    else
                    {
                        ergListe = ergListe + fileListe[i];
                    }
                }
            }
        }
        catch(Exception e)
        {
            ergListe = "";
            append("Fehler: " + e.toString() + "\n");
        }
        append("Return: \"" + ergListe + "\n");
        return ergListe;
    }

    /**
     * Funktion erstellt eine Datei
     * @param file Datei die erstellt werden soll
     * @return True wenn die Datei erstellt wurde
     */
    public boolean createFile(String file) throws RemoteException
    {
        boolean fileCreated;
        append("Funktion: createFile - Param: " + file + "\n");
        try
        {
            fileCreated = this.fs.create(file, "file");
        }
        catch(Exception e)
        {
            append("Fehler: " + e.toString() + "\n");
            fileCreated = false;

        }
        append("Return: " + fileCreated + "\n");
        return fileCreated;
    }

    /**
     * Funktion erstellt einen Ordner
     * @param dir Ordner der erstellt werden soll
     * @return True wenn der Ordner erstellt wurde
     */
    public boolean createDir(String dir) throws RemoteException
    {
        boolean dirCreated;
        append("Funktion: createDir - Param: " + dir + "\n");
        try
        {
            dirCreated = this.fs.create(dir, "dir");
        }
        catch(Exception e)
        {
            append("Fehler_createDir: " + e.toString() + "\n");
            dirCreated = false;

        }
        append("Return: \"" + dirCreated + "\n");
        return dirCreated;
    }

    /**
     * Funktion löscht einen Ordner oder eine Datei
     * @param file Ordner/Datei der/die gelöscht werden soll
     * @return True wenn der Ordner/die Datei geloescht wurde
     */
    public boolean delete(String file) throws RemoteException
    {
        boolean fileDeleted;
        append("Funktion: delete - Param: " + file + "\n");
        try
        {
            fileDeleted = this.fs.delete(file);
        }
        catch(Exception e)
        {
            append("Fehler_delete: " + e.toString() + "\n");
            fileDeleted = false;
        }
        append("Return: \"" + fileDeleted + "\"" + "\n");
        return fileDeleted;
    }

    /**
     * Funktion benennt einen Ordner oder eine Datei um
     * @param oldName aktueller Name der Datei oder des Ordners
     * @param newName neuer Name der Datei oder des Ordners
     * @return True wenn der Ordner/die Datei umbenannt werden konnte
     */
    public boolean rename(String oldName, String newName) throws RemoteException
    {
        boolean fileRenamed;
        append("Funktion: rename - Params: " + oldName + ", " + newName + "\n");
        try
        {
            fileRenamed = this.fs.rename(oldName, newName);
        }
        catch(IOException e)
        {
            append("Fehler_rename: " + e.toString() + "\n");
            fileRenamed = false;

        }
        append("Return: \"" + fileRenamed + "\"" + "\n");
        return fileRenamed;
    }

    /**
     * Funktion liefert den Namen des OS zurück
     * @return Name des OS-Systems des FileSystems
     */
    public String getOSName()throws RemoteException
    {
        append("Funktion: getOSName:\n");
        String osName;
        osName = this.fs.getOSName();
        append("Return: \"" + osName + "\"" + "\n");
        return osName;
    }

    /**
     * Funktion liefert den Namen eines Hosts zurück
     * @return Host Name des FileSystems
     * @throws RemoteException
     */
    //ToDooooooooooooooooooooooooooooooooooooooooooooo
    public String getHostName() throws RemoteException
    {
        append("Funktion: getHostName:\n");
        String hostName;
        hostName = fs.getHostName();
        append("Return: \"" + hostName + "\"" + "\n");
        return hostName;
    }

    public String getHostAdress()
    {
        append("Funktion: getHostAddress");
        String hostAddress;
        hostAddress = fs.getHostAdress();
        append("Return: \"" + hostAddress + "\"");
        return hostAddress;
    }

    @Override
    public File getFile(String pfad) throws RemoteException
    {
        append("\n Funktion: getFile: \n");
        append("\n pfad: " + pfad + "\n");
        File wurzel = null;

        try
        {
            this.fs.baum(wurzel);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        wurzel = this.fs.getDatei(pfad);
        serverTextArea.append(String.valueOf(wurzel));
        return wurzel;
    }

    @Override
    public FileTreeModel getFileTreeModel(File wurzel) throws RemoteException
    {
        append("\n Funktion: getFileTreeModel: \n");
        FileTreeModel a = null;
        try
        {
            a = this.fs.baum(wurzel);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        serverTextArea.append(String.valueOf(wurzel));
        return a;
    }

    /**
     * Hauptmethode
     * Startet den Server
     */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
    }
}