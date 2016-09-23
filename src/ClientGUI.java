import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Created by Eugen Eberle on 20.08.2016.
 */

public class ClientGUI extends JFrame implements ActionListener {
    static ClientGUI client;
    private JPanel clientPanel;
    private JTextField portTextFeld;
    private JButton startClientButton;
    private JTextArea clientTextArea;
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
    private JLabel ip;
    private JLabel ipLabel;
    private JButton sWechselButton;
    private JLabel banner;
    private JButton anzeigenButton;
    private JButton backButton;

    private VerwalterInterface vServer;

    /**
     * Fuer search
     */
    String searchPfad = "";
    boolean ersteEingabe = true;

    JFrame frame = new JFrame("ClientGUI");

    /**
     * Konstruktor
     */
    public ClientGUI() throws IOException {
        frame.setContentPane(clientPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** JTree */
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        root.setUserObject(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        model.nodeChanged(root);

        model.reload(root);

        frame.setVisible(true);

        /**Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad eingeben */
        ImageIcon img = new ImageIcon("htw.png");
        frame.setIconImage(img.getImage());

        clientTextArea.append("Hallo \n\n");
        addListener();

        deaktiviereButtons();

        //frame.setSize(1000, 1000);
        frame.setLocation(50, 0);
        frame.pack();
        frame.setSize(1100, 900);


        /** listener fuer den tree*/
        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent ae) {
                //TreePath path = ae.getNewLeadSelectionPath();
                //System.out.println( path );

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ae.getPath().getLastPathComponent();

                //event verlassen wenn keine Node ausgewaehlt wurde
                if (node == null)
                {
                    return;
                }

                String pfad = node.toString();
                client.append("Ausgewaehlt: " + pfad + "\n");

                node.removeAllChildren();
                try
                {
                    String dirs = vServer.browseDirs(pfad);
                    String[] dirList = dirs.split("[;]");
                    String files = vServer.browseFiles(pfad);
                    String[] fileList = files.split("[;]");

                    //verarbeiten der gefunden Ordner
                    for (int i = 0; i < dirList.length; i++)
                    {
                        if (!dirList[i].equals(""))
                        {
                            node.add(new DefaultMutableTreeNode(dirList[i]));
                        }
                    }

                    //verarbeite der gefundenen dateien
                    for (int j = 0; j < fileList.length; j++)
                    {
                        if (!fileList[j].equals(""))
                        {
                            Contact temp = new Contact(fileList[j]);
                            node.add(new DefaultMutableTreeNode(temp));
                        }
                    }
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });
    }


    void append(String text)
    {
        clientTextArea.append(text);
        clientTextArea.setCaretPosition(clientTextArea.getText().length() - 1);
    }

    /**
     * Button gedrückt
     */
    public void actionPerformed(ActionEvent ae)
    {
        /**
         * Die Quelle des Events finden,
         * d.h. welcher Button wurden geklickt?
         */
        Object aeSource = ae.getSource();

        if (aeSource == startClientButton) {
            startClientButton();
        } else if (aeSource == OSInfoButton) {
            OSInfoButton();
        } else if (aeSource == createDirButton) {
            createDirButton();
        } else if (aeSource == createFileButton) {
            createFileButton();
        } else if (aeSource == seachButton) {
            searchButton();
        } else if (aeSource == deleteButton) {
            deleteButton();
        } else if (aeSource == renameButton) {
            renameButton();
        } else if (aeSource == sWechselButton) {
            wechselButton();
        } else if (aeSource == anzeigenButton) {
            anzeigen();
        } else if (aeSource == backButton) {
            backButton();
        }
    }

    /**
     * Zeigt ausgewählten Knoten als root
     * fuehrt die Aktion des startClientButton-button aus
     **/
    private void startClientButton()
    {
        int serverPort;
        String host = "192.168.0.101";
        try {
            serverPort = Integer.parseInt(portTextFeld.getText().trim());
        } catch (Exception er) {
            JOptionPane.showMessageDialog(null, "Fehler bei der Port-Eingabe", "Port-Nr", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            if (System.getSecurityManager() == null)
            {
                System.setSecurityManager(new SecurityManager());
            }
            Registry registry = LocateRegistry.getRegistry(host, serverPort + 1);
            this.vServer = (VerwalterInterface) registry.lookup("VerwalterServer");
            client.append("Verbunden...\n");

            ipLabel.setText(host);
            // Start-Button deaktivieren nach Start
            startClientButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
            aktiviereButtons();
        } catch (Exception e2) {
            client.append("Fehler: " + e2.toString());
        }
        browse("\\");
    }

    /**
     * fuehrt die Aktion des OSInfoButton-button aus
     */
    private void OSInfoButton()
    {
        try
        {
            client.append(" Verwendetes OS: " + this.vServer.getOSName() + "\n");
            client.append(" Name des Hosts:  " + this.vServer.getHostName() + "\n\n");
        } catch (Exception eOS) {
            client.append("Fehler: " + eOS.getMessage() + "\n");
        }
    }

    /**
     * fuehrt die Aktion des createDir-button aus
     * */
    private void createDirButton()
    {
        /** Pruefe ob eine Datei markiert ist. */
        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Create File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String dirPfad = aktuellerBaumPfad.getLastPathComponent().toString();

        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Create Directory", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame eingabe = new JFrame();
        String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll erstellt werden?", "Create Directory", JOptionPane.PLAIN_MESSAGE);
        if(pfad != null)
        {
            try
            {
                if( this.vServer.createDir(dirPfad + "//" + pfad) )
                {
                    JOptionPane.showMessageDialog(null, pfad + "   wurde erstellt!", "Create Directory", JOptionPane.INFORMATION_MESSAGE);
                    refreshBaum();
                    tree1.expandPath(aktuellerBaumPfad);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, pfad + "   konnte NICHT erstellt werden", "Create Directory", JOptionPane.ERROR_MESSAGE);
                    DefaultMutableTreeNode node;
                    DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                    node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                    model.reload(node);
                }
            }
            catch(IOException eDir)
            {
                client.append("Fehler: " + eDir.getMessage()+"\n");
            }
        }
    }

    /**
     * fuehrt die Aktion des createFile-button aus
     * */
    private void createFileButton()
    {
        /** Pruefe ob eine Datei markiert ist. */
        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Create File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String filePfad = aktuellerBaumPfad.getLastPathComponent().toString();

        JFrame eingabe = new JFrame();
        String pfad = JOptionPane.showInputDialog(eingabe, "Welche Datei soll erstellt werden?", "Create File", JOptionPane.PLAIN_MESSAGE);

        if(pfad != null)
        {
            try
            {
                if( this.vServer.createFile( filePfad + "//" + pfad ))
                {
                    DefaultMutableTreeNode node;
                    node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                    node.add(new DefaultMutableTreeNode(pfad));

                    JOptionPane.showMessageDialog(null, pfad + "   wurde erstellt!", "Create File", JOptionPane.INFORMATION_MESSAGE);
                    refreshBaum();
                    tree1.expandPath(aktuellerBaumPfad);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, pfad + "   konnte NICHT erstellt werden!", "Create File", JOptionPane.ERROR_MESSAGE);
                    DefaultMutableTreeNode node;
                    DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                    node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                    model.reload(node);
                }
            }
            catch(IOException eFile)
            {
                client.append("Fehler: " + eFile.getMessage() + "\n");
            }
        }
    }

    /**
     * fuehrt die Aktion des search-button aus
     */
    private void searchButton()
    {
        String erg;
        String[] fileListe2;
        //Erste Eingabe: Was suchen Sie?
        //Text im Label ist die Bedingung
        if (ersteEingabe == true) {
            searchPfad = searchFeld.getText();
            searchLabel.setText("Wo suchen?");
            searchFeld.setText("");
            ersteEingabe = false;
        } else if (ersteEingabe == false) {
            String startDir = searchFeld.getText();
            try {
                erg = this.vServer.search(searchPfad, startDir);
                fileListe2 = erg.split("[;]");
                client.append("Found-Files: \n");
                client.append("---------------------------------------------------------------\n");
                for (int i = 0; i < fileListe2.length; i++) {
                    client.append(fileListe2[i] + "\n");
                }
            } catch (IOException eSeach) {
                client.append("Fehler: " + eSeach.getMessage() + "\n");
            }
            searchLabel.setText("Was suchen?");
            searchFeld.setText("");
            ersteEingabe = true;
        }
    }

    /**
     * Funktion fuehrt die AKtion des delete Buttons aus
     */
    private void deleteButton()
    {
        /** Pruefe ob eine Datei markiert ist. */
        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Create File", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Delete", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String loeschPfad = aktuellerBaumPfad.getLastPathComponent().toString();

        int jaNein = JOptionPane.showConfirmDialog(null, "Soll  " + loeschPfad + "  wirklich geloescht werden?", "Delete", JOptionPane.YES_NO_OPTION);

        if (jaNein == JOptionPane.YES_OPTION)
        {
            try
            {
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                model.reload(node);

                /** Schauen ob Knoten Kinder hat, muss null sein sonst kann man Ordner mit Inhalt entfernen*/
                int anzahlKinder = node.getChildCount();
                if(anzahlKinder == 0)
                {
                    model.removeNodeFromParent(node);
                }

                if (this.vServer.delete(loeschPfad))
                {
                    JOptionPane.showMessageDialog(null, loeschPfad + "  wurde geloescht!", "Delete", JOptionPane.INFORMATION_MESSAGE);

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT geloescht werden!", "Delete", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException eDelete) {
                client.append("Fehler: " + eDelete.getMessage());
            }
        }
    }

    /**
     * Funtion fuehrt die Aktion fuer den Rename Button aus
     */
    private void renameButton()
    {
        String wahl = clientTextArea.getText().trim();
        String[] parts = wahl.split(":");
        String renamePfad = parts[parts.length - 1].trim();
        String oldPath = "";
        String newName = "";

        JFrame eingabe = new JFrame();
        //String oldName = JOptionPane.showInputDialog(eingabe, "Was soll umbeannt werden?", "Rename", JOptionPane.PLAIN_MESSAGE);
        String name = JOptionPane.showInputDialog(eingabe, "Wie lautet die neue Bezeichnung?", "Rename", JOptionPane.PLAIN_MESSAGE);

        String array[] = renamePfad.split("\\\\");
        for (int i=0; i<array.length; i++)
        {
            array[i].trim();
        }
        for (int i=1; i<array.length-1; i++)
        {

            System.out.println("ARRAY " + i + ": " + array[i]);
            oldPath = oldPath + "\\" + array[i];
            System.out.println("NEWNAME " + oldPath);
        }

        newName = oldPath + "\\" + name; // newName ist der ganze alte Pfad(oldPath) + der neue Name. z.B. \test\NeuerName

        TreePath aktuellerBaumPfad = tree1.getSelectionPath().getParentPath().getParentPath();
        try
        {
            if (this.vServer.rename(renamePfad, newName))
            {
                JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde umbenannt!", "Rename", JOptionPane.INFORMATION_MESSAGE);
            } else
            {
                JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT umbenannt werden!", "Rename", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException eRename)
        {
            client.append("Fehler: " + eRename.getMessage() + "\n");
        }
        refreshBaum();
        tree1.expandPath(aktuellerBaumPfad);
    }

    /**
     * Funktion fuehrt die Aktion beim WechselButton aus
     * */
    private void wechselButton()
    {
        int serverPort;
        Object[] selectionValues = { "10.9.41.43", "10.9.40.171", "10.9.40.174" };
        String initialSelection = "10.9.41.43";
        Object selection = JOptionPane.showInputDialog(null, "Zu welchen Server wechseln?",
                "Server Wechsel", JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelection);

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
            Registry registry = LocateRegistry.getRegistry(String.valueOf(selection), serverPort+1);
            this.vServer = (VerwalterInterface) registry.lookup("VerwalterServer");
            client.append("Server gewechselt...\n");
            client.append((String) selection);
        }
        catch(Exception eOS)
        {
            System.out.println("Fehler_serverWechsel: " + eOS.getMessage());
        }
    }


    private void anzeigen()
    {
        String wahl = clientTextArea.getText().trim();
        String[] parts = wahl.split(":");
        String zeigePfad = parts[parts.length - 1].trim();

        if (tree1.getSelectionPath() == null)
        {
            browse("\\");
        } else
        {
            browse(zeigePfad);
        }
    }

    private void backButton()
    {
        browse("\\");
    }

    /**
     * deaktiviert die Steuer-Elemente
     */
    private void deaktiviereButtons()
    {
        seachButton.setEnabled(false);
        createDirButton.setEnabled(false);
        createFileButton.setEnabled(false);
        deleteButton.setEnabled(false);
        renameButton.setEnabled(false);
        OSInfoButton.setEnabled(false);
        searchFeld.setEnabled(false);
        sWechselButton.setEnabled(false);
        anzeigenButton.setEnabled(false);
        backButton.setEnabled(false);
        tree1.setEnabled(false);
    }

    /**
     * aktiviert alle Steuer-Elemente
     */
    private void aktiviereButtons()
    {
        seachButton.setEnabled(true);
        createDirButton.setEnabled(true);
        createFileButton.setEnabled(true);
        deleteButton.setEnabled(true);
        renameButton.setEnabled(true);
        OSInfoButton.setEnabled(true);
        searchFeld.setEnabled(true);
        sWechselButton.setEnabled(true);
        anzeigenButton.setEnabled(true);
        backButton.setEnabled(true);
        tree1.setEnabled(true);
    }

    /**
     * fuegt die Listener zu den Steuer-Elementen
     */
    private void addListener()
    {
        startClientButton.addActionListener(this);
        seachButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);
        sWechselButton.addActionListener(this);
        anzeigenButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    public static void main(String[] args) throws IOException
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
        client = new ClientGUI();
    }


    private void refreshBaum()
    {
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        tree1.setModel(model);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        model.reload(root);
    }

    /**
     * @param pfad: wird im JTree angezeigt
     */
    public void browse(String pfad)
    {
        String erg;
        String[] dirListe = new String[0];
        String[] fileListe = new String[0];
        try
        {
            erg = this.vServer.browseDirs(pfad);
            dirListe = erg.split("[;]");

            erg = this.vServer.browseFiles(pfad);
            fileListe = erg.split("[;]");
        } catch (IOException e11) {
            client.append("Fehler: " + e11.getMessage() + "\n");
        }

        /**Baum wird aus den Inhalten dirListe und fileListe zusammengebaut*/
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        tree1.setModel(model);
        tree1.setCellRenderer(new MyTreeCellRenderer()); /**CellRender classe*/
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        root.setUserObject(pfad);

        if (root == null)
        {
            return;
        }

        for (int i = 0; i < dirListe.length; i++)
        {
            if (!dirListe[i].equals(""))
            {
                root.add(new DefaultMutableTreeNode(dirListe[i]));
            }
        }

        for (int i = 0; i < fileListe.length; i++)
        {
            if (!fileListe[i].equals("")) {
                Contact temp = new Contact(fileListe[i]);
                root.add(new DefaultMutableTreeNode(temp));
            }
        }
        model.reload(root);
    }

}


/**Logo Render */
class MyTreeCellRenderer extends DefaultTreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        ImageIcon file = new ImageIcon("file.png");
        ImageIcon folder = new ImageIcon("folder.png");

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        // decide what icons you want by examining the node
        if (value instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if (node.getUserObject() instanceof String)
            {
                // your root node, since you just put a String as a user obj
                setIcon(folder);
            }
            else if (node.getUserObject() instanceof Contact)
            {
                // decide based on some property of your Contact obj
                Contact contact = (Contact)node.getUserObject();
                if (contact.isSomeProperty())
                {
                    setIcon(UIManager.getIcon("FileView.hardDriveIcon"));
                }
                else
                {
                    setIcon(file);
                }
            }
        }
        return this;
    }
}


class Contact implements MutableTreeNode
{
    private boolean someProperty;
    private String name;

    public Contact(String name)
    {
        this(name, false);
    }

    public Contact(String name, boolean property)
    {
        this.someProperty = property;
        this.name = name;
    }

    public boolean isSomeProperty()
    {
        return someProperty;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public void insert(MutableTreeNode child, int index) {
    }

    @Override
    public void remove(int index) {
    }

    @Override
    public void remove(MutableTreeNode node) {
    }

    @Override
    public void setUserObject(Object object) {
    }

    @Override
    public void removeFromParent() {
    }

    @Override
    public void setParent(MutableTreeNode newParent) {
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public int getIndex(TreeNode node) {
        return 0;
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public Enumeration children() {
        return null;
    }
}