import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.rmi.*;

/**
 * Created by Eugen Eberle on 20.08.2016.
 */
public class ClientGUI extends JFrame implements ActionListener, TreeModel, Serializable, Cloneable
{
    static ClientGUI client;
    private JPanel clientPanel;
    private JTextField portTextFeld;
    private JButton startClientButton;
    private JTextArea clientTextArea;
    private JButton browseButton;
    private JButton seachButton;
    private JButton createDirButton;
    private JButton createFileButton;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton OSInfoButton;
    private JLabel port;
    private JTree tree1;
    private JTextField searchFeld;
    private JLabel searchLabel;
    private JScrollPane baumScroll;

    private FSInterface fsserver;

    /**Fuer die Tree-Ansicht */
    protected EventListenerList listeners;
    private Map map;
    private File root;

    /**Fuer search*/
    String searchPfad = "";
    boolean ersteEingabe = true;

    JFrame frame = new JFrame("ClientGUI");
    /**
     * Konstruktor
     */
    public ClientGUI() throws IOException
    {
        frame.setContentPane(clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** JTree */
        DefaultTreeModel model = (DefaultTreeModel)tree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        root.removeAllChildren();
        root.setUserObject("Browse");
        model.nodeChanged(root);

        model.reload(root);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(1000, 800);
        //frame.setResizable(false);
        frame.setLocation(10, 10);


        /**Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad eingeben */
        ImageIcon img = new ImageIcon("htw.png");
        frame.setIconImage(img.getImage());

        clientTextArea.append("Hallo \n\n");
        startClientButton.addActionListener(this);
        browseButton.addActionListener(this);
        seachButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);

        /**
         * Buttons deaktivieren, werden erst nach Verbindung aktiviert
         */
        browseButton.setEnabled(false);
        seachButton.setEnabled(false);
        createDirButton.setEnabled(false);
        createFileButton.setEnabled(false);
        deleteButton.setEnabled(false);
        renameButton.setEnabled(false);
        OSInfoButton.setEnabled(false);
        searchFeld.setEnabled(false);
    }

    /** Alles fuer den Tree_ANFANG, wird in der aktuellen Version nicht benutzt*/
    private static final Object LEAF = new Serializable()
    {};

    public ClientGUI(File root2)
    {
        this.root = root2.getParentFile();
        if (!root.isDirectory())
        {
            map.put(root, LEAF);
        }
        this.listeners = new EventListenerList();
        this.map = new HashMap();
    }

    public Object getRoot()
    {
        return root;
    }
    public boolean isLeaf(Object node)
    {
        return map.get(node) == LEAF;
    }
    public int getChildCount(Object node)
    {
        java.util.List children = children(node);

        if (children == null)
        {
            return 0;
        }
        return children.size();
    }
    public Object getChild(Object parent, int index)
    {
        return children(parent).get(index);
    }
    public int getIndexOfChild(Object parent, Object child)
    {
        return children(parent).indexOf(child);
    }
    protected java.util.List children(Object node)
    {
        File f = (File)node;
        Object value = map.get(f);
        if (value == LEAF)
        {
            return null;
        }
        java.util.List children = (java.util.List)value;
        if (children == null)
        {
            File[] c = f.listFiles();
            if (c != null)
            {
                children = new ArrayList(c.length);
                for (int len = c.length, i = 0; i < len; i++)
                {
                    children.add(c[i]);
                    if (!c[i].isDirectory())
                        map.put(c[i], LEAF);
                }
            }
            else
                children = new ArrayList(0);

            map.put(f, children);
        }
        return children;
    }
    public void valueForPathChanged(TreePath path, Object value)
    {
    }
    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(TreeModelListener.class, l);
    }
    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(TreeModelListener.class, l);
    }
    public Object clone() {
        try {
            ClientGUI clone = (ClientGUI) super.clone();
            clone.listeners = new EventListenerList();
            clone.map = new HashMap(map);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    /** Alles fuer den Tree_ENDE*/

    void append(String text)
    {
        clientTextArea.append(text);
        clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
    }

    /**
     * Button gedrückt
     */
    public void actionPerformed(ActionEvent e)
    {
        /**
         * Die Quelle des Events finden,
         * d.h. welcher Button wurden geklickt?
         */
        Object o = e.getSource();

        if(o == startClientButton)
        {
            int serverPort;
            try
            {
                serverPort = Integer.parseInt(portTextFeld.getText().trim());
            } catch(Exception er)
            {
                JOptionPane.showMessageDialog(null, "Fehler bei der Port-Eingabe", "Port-Nr", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try
            {
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager(new SecurityManager());
                }
                Registry registry = LocateRegistry.getRegistry(serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");
                client.append("Verbunden...\n");

                // Start-Button deaktivieren nach Start
                startClientButton.setEnabled(false);
                // Portfeld deaktivieren nach Start
                portTextFeld.setEditable(false);
                //Buttons aktivieren
                browseButton.setEnabled(true);
                seachButton.setEnabled(true);
                createDirButton.setEnabled(true);
                createFileButton.setEnabled(true);
                deleteButton.setEnabled(true);
                renameButton.setEnabled(true);
                OSInfoButton.setEnabled(true);
                searchFeld.setEnabled(true);
            }
            catch(Exception e2)
            {
                System.out.println( "Fehler: " + e2.toString() );
                client.append( "Fehler: " + e2.toString() );
            }

//            /** Verbindung mit mehreren Rechner Stuff */
//            try
//            {
//                this.fsserver = (FSInterface) Naming.lookup("//10.9.41.43:5656/FileSystemServer");
//            }
//            catch (Exception ex)
//            {
//                System.out.println( "Fehler: " + ex.toString() );
//            }
        }

        if(o == OSInfoButton)
        {
            try
            {
                client.append(" Verwendetes OS: " + this.fsserver.getOSName() + "\n\n");
            }
            catch(Exception eOS)
            {
                System.out.println("Fehler: " + eOS.getMessage());
            }
        }

        if(o == createDirButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll erstellt werden?", "Create Directory", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.createDir(pfad) )
                {
                    client.append("Ordner wurde erstellt!\n");
                }
                else
                {
                    client.append("Ordner konnte NICHT erstellt werden!\n");
                    JOptionPane.showMessageDialog(null, "Ordner konnte NICHT erstellt werden", "Create Directory", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException eDir)
            {
                System.out.println("Fehler: " + eDir.getMessage());
            }
        }

        if(o == createFileButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welche Datei soll erstellt werden?", "Create File", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.createFile(pfad) )
                {
                    client.append("Datei wurde erstellt!\n");
                }
                else
                {
                    client.append("Datei konnte NICHT erstellt werden!\n");
                    JOptionPane.showMessageDialog(null, "Datei konnte NICHT erstellt werden!n", "Create File", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException eFile)
            {
                System.out.println("Fehler: " + eFile.getMessage());
            }
        }

        if(o == browseButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll untersucht werden?", "Browse", JOptionPane.PLAIN_MESSAGE);

            String erg = null;
            String [] dirListe = new String[0];
            String [] fileListe = new String[0];

            try
            {
                erg = this.fsserver.browseDirs(pfad);
                dirListe = erg.split("[;]");

                erg = this.fsserver.browseFiles(pfad);
                fileListe = erg.split("[;]");
            }
            catch(IOException e11)
            {
                System.out.println("Fehler: " + e11.getMessage());
            }


            DefaultTreeModel model = (DefaultTreeModel)tree1.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
            root.removeAllChildren();
            root.setUserObject(pfad + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));

            for (int i = 0; i < dirListe.length; i++)
            {
                String erg2 = null;
                String erg3 = null;
                try
                {
                    erg2 = this.fsserver.browseDirs(dirListe[i]);
                    erg3 = this.fsserver.browseFiles(dirListe[i]);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                String [] dirListe2 = erg2.split("[;]");
                String [] fileListe2 = erg3.split("[;]");

                DefaultMutableTreeNode folder = new DefaultMutableTreeNode(dirListe[i]);
                for (int j = 0; j < dirListe2.length; j++)
                {
                    folder.add(new DefaultMutableTreeNode(dirListe2[j]));
                }

                for (int n = 0; n < fileListe2.length; n++)
                {
                    if(!fileListe2[n].equals(""))
                    {
                        folder.add(new DefaultMutableTreeNode(fileListe2[n]));
                    }
                }
                root.add(folder);
            }

            for (int i = 0; i < fileListe.length; i++)
            {
                if(!fileListe[i].equals(""))
                {
                    root.add(new DefaultMutableTreeNode(fileListe[i]));
                }
            }
            model.reload(root);
        }

        if(o == seachButton)
        {
            String erg;
            String [] fileListe2;
            //Erste Eingabe: Was suchen Sie?
            //Text im Label ist die Bedingung
            if (ersteEingabe == true)
            {
                searchPfad = searchFeld.getText();
                searchLabel.setText("Wo suchen?");
                searchFeld.setText("");
                ersteEingabe = false;
            }
            else if (ersteEingabe == false)
            {
                String startDir = searchFeld.getText();
                try
                {
                    erg = this.fsserver.search(searchPfad, startDir);
                    fileListe2 = erg.split("[;]");
                    client.append("Found-Files: \n");
                    client.append("---------------------------------------------------------------\n");
                    for (int i = 0; i < fileListe2.length; i++)
                    {
                        client.append(fileListe2[i] + "\n");
                    }
                } catch (IOException eSeach) {
                    System.out.println("Fehler: " + eSeach.getMessage());
                }
                searchLabel.setText("Was suchen?");
                searchFeld.setText("");
                ersteEingabe = true;
            }
        }

        if(o == deleteButton)
        {
            JFrame eingabe = new JFrame();
            String pfad = JOptionPane.showInputDialog(eingabe, "Was soll gelöscht werden?", "Delete", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.delete(pfad) )
                {
                    client.append("Ordner oder Datei wurde geloescht!\n");
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde geloescht!", "Delete", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT geloescht werden!", "Delete", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Ordner oder Datei konnte NICHT geloescht werden!");
                }
            }
            catch(IOException eDelete)
            {
                System.out.println("Fehler: " + eDelete.getMessage());
            }
        }

        if(o == renameButton)
        {
            JFrame eingabe = new JFrame();
            String oldName = JOptionPane.showInputDialog(eingabe, "Was soll umbeannt werden?", "Rename", JOptionPane.PLAIN_MESSAGE);
            String newName = JOptionPane.showInputDialog(eingabe, "Wie lautet die neue Bezeichnung?", "Rename", JOptionPane.PLAIN_MESSAGE);
            try
            {
                if( this.fsserver.rename(oldName, newName) )
                {
                    System.out.println("Ordner oder Datei wurde umbenannt!");
                    client.append("Ordner oder Datei wurde umbenannt!\n");
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde umbenannt!", "Rename", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT umbenannt werden!", "Rename", JOptionPane.ERROR_MESSAGE);
                    System.out.println("Ordner oder Datei konnte NICHT umbenannt werden!");
                }
            }
            catch(IOException eRename)
            {
                System.out.println("Fehler: " + eRename.getMessage());
            }
        }

    }

    public static void main(String[] args) throws IOException {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
        client = new ClientGUI();
    }
}
