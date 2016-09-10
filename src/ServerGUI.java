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
    public static FSInterface fsserver = new ServerGUI();

    public ServerGUI()
    {
        JFrame frame = new JFrame("ServerGUI");
        frame.setContentPane(serverPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 400);
        serverTextArea.append("Hallo \n\n");
        starteServerButton.addActionListener(this);

        //Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad eingeben
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
                serverTextArea.append("Fehler bei der Port-Eingabe\n");
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
                FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsserver, serverPort);
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry =  LocateRegistry.createRegistry(serverPort);
                //Objekt an registry binden
                registry.rebind("FileSystemServer", stub);
                serverTextArea.append("Server bound ...\n");
            }
            catch(Exception e2)
            {
                System.out.println( "Fehler: " + e2.toString() );
            }

            /** Verbindung mit mehreren Rechner Stuff */
//            try {
//                Naming.rebind("//:2222/FileSystemServer", fsserver);
//                //Naming.rebind("//:4545/FileSystemServer", fsserver);
//            }
//            catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }

            // Button deaktivieren nach Start
            starteServerButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
        }
    }

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
        System.out.println("Funktion: browseDirs - Param: " + dir);
        serverTextArea.append("\nFunktion: browseDirs - Param: " + dir + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
            System.out.println("Funktion: " + e.toString());
            serverTextArea.append("Funktion: " + e.toString() +"\n");
        }
        System.out.println("Return: \"" + ergListe + "\"");
        serverTextArea.append("Return: \"" + ergListe + "\"");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: browseFiles - Param: " + file);
        serverTextArea.append("\nFunktion: browseFiles - Param: " + file + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
        }
        System.out.println("Return: \"" + ergListe + "\"");
        serverTextArea.append("Return: \"" + ergListe  + "\"" + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: search - Params: " + file + ", " + startDir);
        serverTextArea.append("Funktion: search - Params: " + file + ", " + startDir + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);

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
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
        }
        System.out.println("Return: \"" + ergListe + "\"");
        serverTextArea.append("Return: \"" + ergListe + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: createFile - Param: " + file);
        serverTextArea.append("Funktion: createFile - Param: " + file + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        try
        {
            fileCreated = this.fs.create(file, "file");
        }
        catch(Exception e)
        {
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
            fileCreated = false;

        }
        System.out.println("Return: " + fileCreated);
        serverTextArea.append("Return: " + fileCreated + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: createDir - Param: " + dir);
        serverTextArea.append("Funktion: createDir - Param: " + dir + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        try
        {
            dirCreated = this.fs.create(dir, "dir");
        }
        catch(Exception e)
        {
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
            dirCreated = false;

        }
        System.out.println("Return: \"" + dirCreated + "\"");
        serverTextArea.append("Return: \"" + dirCreated + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: delete - Param: " + file);
        serverTextArea.append("Funktion: delete - Param: " + file + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        try
        {
            fileDeleted = this.fs.delete(file);
        }
        catch(Exception e)
        {
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
            fileDeleted = false;
        }
        System.out.println("Return: \"" + fileDeleted + "\"");
        serverTextArea.append("Return: \"" + fileDeleted + "\"" + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
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
        System.out.println("Funktion: rename - Params: " + oldName + ", " + newName);
        serverTextArea.append("Funktion: rename - Params: " + oldName + ", " + newName + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        try
        {
            fileRenamed = this.fs.rename(oldName, newName);
        }
        catch(IOException e)
        {
            System.out.println("Fehler: " + e.toString());
            serverTextArea.append("Fehler: " + e.toString() + "\n");
            fileRenamed = false;

        }
        System.out.println("Return: \"" + fileRenamed + "\"");
        serverTextArea.append("Return: \"" + fileRenamed + "\"" + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        return fileRenamed;
    }

    /**
     * Funktion liefert den Namen des OS zurück
     * @return Name des OS-Systems des FileSystems
     */
    public String getOSName()throws RemoteException
    {
        System.out.println("Funktion: getOSName");
        serverTextArea.append("Funktion: getOSName!\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        String osName;
        osName = this.fs.getOSName();
        System.out.println("Return: \"" + osName + "\"");
        serverTextArea.append("Return: \"" + osName + "\"" + "\n");
        serverTextArea.setCaretPosition(serverTextArea.getText().length() - 1);
        return osName;
    }

    @Override
    public File getFile(String pfad) throws RemoteException
    {
        File wurzel;
        wurzel = this.fs.getDatei(pfad);
        serverTextArea.append(String.valueOf(wurzel));
        return wurzel;
    }

    /**
     * Hauptmethode
     * Startet den Server
     * @param args[] Parameter beim Programm start. Erster Eintrag ist PortNr für Server
     */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
    }
}

/**
 * The methods in this class allow the JTree component to traverse
 * the file system tree, and display the files and directories.
 * WIRD NICHT BENUTZT WEIL ICH ES NICHT ÜBER NETZWERK GEHT
 **/
class FileTreeModel implements TreeModel
{
    private FileSystem fs = new FileSystem();
    private FSInterface fsserver;

    //private File root = fs.gib();
    File root;
    // We specify the root directory when we create the model.
    //protected File root;

    public FileTreeModel()
    {
        this.root = fs.gib();
    }

    public FileTreeModel(File root)
    {
        root = fs.getDatei(String.valueOf(root));
        this.root = root;
    }

    // The model knows how to return the root object of the tree
    public Object getRoot()
    {
        return root;
    }

    // Tell JTree whether an object in the tree is a leaf or not
    public boolean isLeaf(Object node)
    {
        return ((File)node).isFile();
    }

    // Tell JTree how many children a node has
    public int getChildCount(Object parent)
    {
        String[] children = ((File)parent).list();
        if (children == null) return 0;
        return children.length;
    }


    // Fetch any numbered child of a node for the JTree.
    // Our model returns File objects for all nodes in the tree.  The
    // JTree displays these by calling the File.toString() method.
    public Object getChild(Object parent, int index)
    {
        String[] children = ((File)parent).list();
        if ((children == null) || (index >= children.length)) return null;
        return new File((File) parent, children[index]);
    }

    // Figure out a child's position in its parent node.
    public int getIndexOfChild(Object parent, Object child)
    {
        String[] children = ((File)parent).list();
        if (children == null) return -1;
        String childname = ((File)child).getName();
        for(int i = 0; i < children.length; i++)
        {
            if (childname.equals(children[i])) return i;
        }
        return -1;
    }

    // This method is only invoked by the JTree for editable trees.
    // This TreeModel does not allow editing, so we do not implement
    // this method.  The JTree editable property is false by default.
    public void valueForPathChanged(TreePath path, Object newvalue) {}

    // Since this is not an editable tree model, we never fire any events,
    // so we don't actually have to keep track of interested listeners.
    public void addTreeModelListener(TreeModelListener l) {}
    public void removeTreeModelListener(TreeModelListener l) {}
}