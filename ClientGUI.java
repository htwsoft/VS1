package GUI;

import sun.reflect.generics.tree.Tree;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.*;
import java.nio.file.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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

    private FSInterface fsserver;

    /**Fuer die Tree-Ansicht */
    protected EventListenerList listeners;
    private Map map;
    private File root;

    /**Fuer search*/
    String searchPfad = "";
    boolean ersteEingabe = true;

    /**
     * Konstruktor
     */
    public ClientGUI() throws IOException
    {
        JFrame frame = new JFrame("ClientGUI");
        frame.setContentPane(clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //JTree
        DefaultTreeModel model = (DefaultTreeModel)tree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        //root.removeFromParent();
        root.removeAllChildren();
        //tree1.putClientProperty("JTree.lineStyle", "None");
        root.setUserObject("My label");
        model.nodeChanged(root);

        DefaultMutableTreeNode hallo = new DefaultMutableTreeNode("Hallo");
        hallo.add(new DefaultMutableTreeNode("Amel"));
        root.add(hallo);
        model.reload(root);

        DefaultMutableTreeNode root2 = new DefaultMutableTreeNode("root2");
        DefaultMutableTreeNode bird = new DefaultMutableTreeNode("Birds");
        root2.add(bird);
        root.add(root2);
        model.reload(root);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(900, 400);
        frame.setResizable(false);
        frame.setLocation(10, 10);


        //Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad eingeben
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


    private static final Object LEAF = new Serializable()
    {};

    public ClientGUI(File root)
    {
        this.root = root;
        if (!root.isDirectory())
        {
            map.put(root, LEAF);
        }
        this.listeners = new EventListenerList();
        this.map = new HashMap();
    }

    /** Alles fuer den Tree_ANFANG*/
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

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path)
    {
        java.net.URL imgURL = ClientGUI.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    void append(String text)
    {
        clientTextArea.append(text);
        clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
    }

    /** Add nodes from under "dir" into curTop. Highly recursive.
     * wird nicht benutzt */
//    DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir)
//    {
//        String curPath = dir.getPath();
//        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
//        if (curTop != null)
//        { // should only be null at root
//            curTop.add(curDir);
//        }
//        Vector ol = new Vector();
//        String[] tmp = dir.list();
//        for (int i = 0; i < tmp.length; i++)
//            ol.addElement(tmp[i]);
//        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
//        File f;
//        Vector files = new Vector();
//        // Make two passes, one for Dirs and one for Files. This is #1.
//        for (int i = 0; i < ol.size(); i++)
//        {
//            String thisObject = (String) ol.elementAt(i);
//            String newPath;
//            if (curPath.equals("."))
//                newPath = thisObject;
//            else
//                newPath = curPath + File.separator + thisObject;
//            if ((f = new File(newPath)).isDirectory())
//                addNodes(curDir, f);
//            else
//                files.addElement(thisObject);
//        }
//        // Pass two: for files.
//        for (int fnum = 0; fnum < files.size(); fnum++)
//            curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
//        return curDir;
//    }


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
            }
            catch(Exception e2)
            {
                System.out.println( "Fehler: " + e2.toString() );
                client.append( "Fehler: " + e2.toString() );
            }

            /** Verbindung mit mehreren Rechner Stuff */
//            try
//            {
//                this.fsserver = (FSInterface) Naming.lookup("//192.168.0.104:5555/FileSystemServer");
//            }
//            catch (Exception ex)
//            {
//                System.out.println( "Fehler: " + ex.toString() );
//            }


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

            //JFrame eingabe = new JFrame();
            //String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll untersucht werden?", "Browse", JOptionPane.PLAIN_MESSAGE);

            //Fuer rekusrviven Ausruf
            // Make a tree list with all the nodes, and make it a JTree
//            JTree tree = new JTree(addNodes(null, new File(".") ));
            // Lastly, put the JTree into a JScrollPane.
//            JScrollPane scrollpane = new JScrollPane();
//            scrollpane.getViewport().add(tree);
//            add(BorderLayout.CENTER, scrollpane);
            // Lastly, put the JTree into a JScrollPane.
            //JScrollPane scrollpane = new JScrollPane();
            //scrollpane.getViewport().add(tree);
            //add(BorderLayout.CENTER, scrollpane);

            //NEU
            File a = new File(dirListe[2]); //PopUp fuer Pfadeingabe
            //File pfad = new File("\\");
            JTree baum = new JTree(new ClientGUI(a));

            JFrame f = new JFrame(pfad.toString() + "          " + new SimpleDateFormat("HH:mm:ss").format(new Date()));


            /** Eigene Icons*/
            ImageIcon close = createImageIcon("close.png");
            ImageIcon open = createImageIcon("open.png");
            if (close != null)
            {
                DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
                renderer.setClosedIcon(close);
                renderer.setOpenIcon(open);
                //renderer.setLeafIcon(leafIcon); //TODO
                baum.setCellRenderer(renderer);
            } else {
                System.err.println("Leaf icon missing; using default.");
            }/**Icons Stuff Ende*/


            DefaultTreeModel model = (DefaultTreeModel)tree1.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
            root.removeAllChildren();
            root.setUserObject(pfad);

            for (int i = 1; i < dirListe.length; i++)
            {
                root.add(new DefaultMutableTreeNode(dirListe[i]));
            }
            for (int i = 1; i < fileListe.length; i++)
            {
                root.add(new DefaultMutableTreeNode(fileListe[i]));
            }

            root.add(new DefaultMutableTreeNode(erg));
            model.reload(root);

            f.add(new JScrollPane(baum));
            f.pack();
            f.setVisible(true);
            f.setSize(800, 600);
            f.setResizable(false);
            f.setLocation(950, 10);
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
        //System.setProperty("java.security.policy","C:\\Program Files (x86)\\Java\\jre1.8.0_101\\lib\\security\\java.policy");
        //System.setProperty("java.security.policy","C:\\Program Files\\Java\\jre1.8.0_91\\lib\\security\\java.policy");
        client = new ClientGUI();
    }
}
