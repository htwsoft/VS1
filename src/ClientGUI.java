import rmifs.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.NotBoundException;
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

public class ClientGUI extends JFrame implements ActionListener
{
    static ClientGUI client;
    private static String HOST_IP = "192.168.0.102";
    private int HOST_PORT;
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
    private String pfadTrenner; //Variable beinhaltet "Slash" fuer Windows oder Linux
    private static String initDir = "\\"; //Ordner aus der init browse gemacht wird
    private boolean isWindows;
    private FileSystemClient aktuellerVerwalter;
    private FileSystemClient mainVerwalter;

    /**
     * Fuer search
     */
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

        frame.setLocation(50, 0);
        frame.pack();
        frame.setSize(1100, 900);


        /** listener fuer den tree*/
        tree1.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent ae)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ae.getPath().getLastPathComponent();

                //event verlassen wenn keine Node ausgewaehlt wurde
                if (node == null)
                {
                    return;
                }
                //node.removeAllChildren();

                DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                model.reload(node);

                String pfad = node.toString();
                client.append("Ausgewaehlt: " + pfad + "\n");
                if(pfad.equals(initDir))
                {
                    browse(initDir);
                    return;
                }
                node.removeAllChildren();
                try
                {
                    String dirs = aktuellerVerwalter.browseDirs(pfad);
                    dirs = dirs.trim();
                    String[] dirList = dirs.split("[;]");
                    String files = aktuellerVerwalter.browseFiles(pfad);
                    files = files.trim();
                    String[] fileList = files.split("[;]");

                    //verarbeiten der gefunden Ordner
                    for (int i = 0; i < dirList.length; i++)
                    {
                        if (!dirList[i].isEmpty())
                        {
                            node.add(new DefaultMutableTreeNode(dirList[i]));
                        }
                    }

                    //verarbeite der gefundenen dateien
                    for (int j = 0; j < fileList.length; j++)
                    {
                        if (!fileList[j].isEmpty())
                        {
                            Contact temp = new Contact(fileList[j]);
                            node.add(new DefaultMutableTreeNode( temp));
                        }
                    }
                } catch (Exception re)
                {
                    append("\nFehler beim Browsen des Ordners\n");
                    append(re.getMessage() + "\n");
                }
            }
        });


        searchFeld.addKeyListener(new KeyAdapter()
        {
        });
        searchFeld.addKeyListener(new KeyAdapter()
        {
        });
        searchFeld.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    searchButton();
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
        String host = HOST_IP;
        String osName;
        try {
            serverPort = Integer.parseInt(portTextFeld.getText().trim());
        } catch (Exception er)
        {
            JOptionPane.showMessageDialog(null, "Fehler bei der Port-Eingabe", "Port-Nr", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            HOST_PORT = serverPort;
            this.aktuellerVerwalter = new FileSystemClient(serverPort, host);
            this.mainVerwalter = new FileSystemClient(serverPort, host);
            this.isWindows = this.aktuellerVerwalter.getOSName().contains("Windows");
            //Slash fuer Ordnepfade richtig setzen
            if(this.isWindows){this.pfadTrenner = "\\";}
            else{this.pfadTrenner = "/";}
            browse(initDir);
            aktiviereButtons();
            ipLabel.setText(aktuellerVerwalter.getHostAddress());
        }
        catch(Exception e)
        {
            client.append("Fehler: Beim verbinden mit dem Verwalter-Server " + HOST_IP + "\n");
        }
    }

    /**
     * fuehrt die Aktion des OSInfoButton-button aus
     */
    private void OSInfoButton()
    {
        try
        {
            client.append(" Verwendetes OS: " + this.aktuellerVerwalter.getOSName() + "\n");
            client.append(" Name des Hosts:  " + this.aktuellerVerwalter.getHostName() + "\n\n");
        } catch (Exception eOS) {
            client.append("Fehler_OS-Info: " + eOS.getMessage() + "\n");
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
                if( this.aktuellerVerwalter.createDir(dirPfad + "//" + pfad) )
                {
                    DefaultMutableTreeNode altLetzer = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());////////
                    DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());///////////
                    altLetzer.add(new DefaultMutableTreeNode(dirPfad + "\\" + pfad));////////////
                    model.reload(altLetzer);///////

                    JOptionPane.showMessageDialog(null, pfad + "   wurde erstellt!", "Create Directory", JOptionPane.INFORMATION_MESSAGE);
                    //refreshBaum();
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
            catch(Exception eDir)
            {
                client.append("\nFehler beim erstellen des Ordners\n");
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
                if( this.aktuellerVerwalter.createFile( filePfad + "//" + pfad ))
                {

                    DefaultMutableTreeNode altLetzer = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());////////
                    DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());///////////
                    Contact temp = new Contact(filePfad + "\\" + pfad);
                    altLetzer.add(new DefaultMutableTreeNode(temp));////////////
                    model.reload(altLetzer);///////

                    //________________________________________

                    //DefaultMutableTreeNode node;
                    //node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                    //node.add(new DefaultMutableTreeNode(pfad));


                    JOptionPane.showMessageDialog(null, pfad + "   wurde erstellt!", "Create File", JOptionPane.INFORMATION_MESSAGE);
                    //refreshBaum();
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
            catch(Exception eFile)
            {
                client.append("\nFehler beim erstellen der Datei\n");
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
        if (ersteEingabe == true)
        {
            searchPfad = searchFeld.getText();
            searchLabel.setText("Wo suchen?");
            searchFeld.setText("");
            ersteEingabe = false;
        }
        else if (ersteEingabe == false)
        {
            String startDir = searchFeld.getText().trim();
            if(startDir.equals(initDir))
            {
                client.append("\n Suche auf root ist aus Performancegruenden deaktiviert.\n Bitte anderen Pfad eingeben.\n");
            }
            else
            {
                try
                {
                    erg = this.aktuellerVerwalter.search(searchPfad, startDir);
                    fileListe2 = erg.split("[;]");
                    client.append("\n Found-Files: \n");
                    for (int i = 0; i < fileListe2.length; i++)
                    {
                        client.append("  " + fileListe2[i] + "\n");
                    }
                    client.append("---------------------------------------------------------------\n");
                } catch (Exception eSeach)
                {
                    client.append("\nFehler beim Suchen inherhalb des Ordners\n");
                    client.append("Fehler: " + eSeach.getMessage() + "\n");
                }
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

        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String loeschPfad = aktuellerBaumPfad.getLastPathComponent().toString();

        String rootTest = aktuellerBaumPfad.getParentPath().getLastPathComponent().toString();
        if (rootTest.equals("\\"))
        {
            JOptionPane.showMessageDialog(null, "Delete im root nicht erlaubt!", "Delete", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jaNein = JOptionPane.showConfirmDialog(null, "Soll  " + loeschPfad + "  wirklich geloescht werden?", "Delete", JOptionPane.YES_NO_OPTION);

        if (jaNein == JOptionPane.YES_OPTION)
        {
            try
            {
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());

                TreePath nodeExpand = aktuellerBaumPfad.getParentPath();

                /** Schauen ob Knoten Kinder hat, muss null sein sonst kann man Ordner mit Inhalt entfernen*/
                int anzahlKinder = node.getSiblingCount();
                if(anzahlKinder == 1)
                {
                    System.out.println(anzahlKinder);
                    nodeExpand = aktuellerBaumPfad.getParentPath().getParentPath();
                }
                else
                {
                    nodeExpand = aktuellerBaumPfad.getParentPath();
                }

                if (this.aktuellerVerwalter.delete(loeschPfad))
                {
                    JOptionPane.showMessageDialog(null, loeschPfad + "  wurde geloescht!", "Delete", JOptionPane.INFORMATION_MESSAGE);
                    model.removeNodeFromParent(node);
                    model.reload(node);
                    tree1.expandPath(nodeExpand);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT geloescht werden!", "Delete", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception eDelete) {
                client.append("\nFehler beim loeschen des Ordners / der Datei \n");
                client.append("Fehler: " + eDelete.getMessage() + "\n");
            }
        }
    }

    /**
     * Funtion fuehrt die Aktion fuer den Rename Button aus
     */
    private void renameButton()
    {
        /** Pruefe ob eine Datei markiert ist. */
        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Datei zum umbennenen markieren!", " Rename", JOptionPane.ERROR_MESSAGE);
            return;
        }

        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String alterName = aktuellerBaumPfad.getLastPathComponent().toString();

        String rootTest = aktuellerBaumPfad.getParentPath().getLastPathComponent().toString();
        if (rootTest.equals("\\"))
        {
            JOptionPane.showMessageDialog(null, "Rename im root nicht erlaubt!", "Rename", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String neueNameBeginn = aktuellerBaumPfad.getParentPath().getLastPathComponent().toString();

        JFrame eingabe = new JFrame();
        String neuerNameEnde = JOptionPane.showInputDialog(eingabe, "Wie lautet die neue Bezeichnung?", "Rename", JOptionPane.PLAIN_MESSAGE);

        String neuerName = neueNameBeginn.trim() + pfadTrenner + neuerNameEnde.trim();

        if(neuerNameEnde != null)
        {
            try
            {
                if (this.aktuellerVerwalter.rename(alterName, neuerName))
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde umbenannt!", "Rename", JOptionPane.INFORMATION_MESSAGE);
                    browse(initDir);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT umbenannt werden!", "Rename", JOptionPane.ERROR_MESSAGE);
                }
                DefaultMutableTreeNode node;
                DefaultTreeModel model = (DefaultTreeModel) (tree1.getModel());
                node = (DefaultMutableTreeNode) (aktuellerBaumPfad.getLastPathComponent());
                model.reload(node);
            } catch (Exception eRename)
            {
                client.append("\nFehler beim umbenennen des Ordners / der Datei \n");
                client.append("Fehler_rename: " + eRename.getMessage() + "\n");
            }
        }

    }

    /**
     * Funktion fuehrt die Aktion beim WechselButton aus
     * */
    private void wechselButton()
    {
        try
        {
            String[] verwalterNames = this.mainVerwalter.getAllVerwalterNames();
            String initialSelection = HOST_IP;
            Object selection = JOptionPane.showInputDialog(null, "Zu welchen Server wechseln?",
                    "Server Wechsel", JOptionPane.QUESTION_MESSAGE, null, verwalterNames, initialSelection);

            browse(initDir);
            if (selection != null)
            {
                try
                {
                    for(int i=0; i < verwalterNames.length; i++ )
                    {
                        if(selection.toString().equals(verwalterNames[i]))
                        {
                            aktuellerVerwalter = new FileSystemClient(HOST_PORT, HOST_IP);
                            aktuellerVerwalter.connectNewVerwalter(i);
                        }
                    }
                    //browse(initDir);
                    browse2(initDir, selection.toString());
                    ipLabel.setText(selection.toString());
                    client.append(" Server gewechselt...\n");
                    client.append((String) selection);
                } catch (Exception e) {
                    client.append("\n Fehler_serverWechsel: " + e.getMessage() + "\n");
                }

            }
        }
        catch(Exception e)
        {
            client.append("\n Fehler_serverWechsel: " + e.getMessage() + "\n");
        }

    }


    private void anzeigen()
    {
        /** Pruefe ob eine Datei markiert ist. */
        if (tree1.getSelectionPath() == null)
        {
            JOptionPane.showMessageDialog(null, "Bitte Pfad/Datei waehlen!", "Auswahl", JOptionPane.ERROR_MESSAGE);
            return;
        }
        /**Markierte Datei/Order auswaehlen */
        TreePath aktuellerBaumPfad = tree1.getSelectionPath();
        String zeigePfad = aktuellerBaumPfad.getLastPathComponent().toString();

        if (tree1.getSelectionPath() == null)
        {
            browse(initDir);
        } else
        {
            browse(zeigePfad);
        }
    }

    private void backButton()
    {
        browse(initDir);
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
        portTextFeld.setEnabled(false);
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
        //searchFeld.addKeyListener(this);
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
            erg = this.aktuellerVerwalter.browseDirs(pfad);
            erg = erg.trim();
            dirListe = erg.split("[;]");

            erg = this.aktuellerVerwalter.browseFiles(pfad);
            erg = erg.trim();
            fileListe = erg.split("[;]");
        } catch (Exception e11)
        {
            client.append("Fehler beim Laden der Ordner / Dateien innerhalb von \"" + pfad + "\"\n");
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
            if (!dirListe[i].isEmpty())
            {
                root.add(new DefaultMutableTreeNode(dirListe[i]));
            }
        }

        for (int i = 0; i < fileListe.length; i++)
        {
            if (!fileListe[i].isEmpty()) {
                Contact temp = new Contact(fileListe[i]);
                root.add(new DefaultMutableTreeNode(temp));
            }
        }
        model.reload(root);
    }


    public void browse2(String pfad, String aktVerwalter)
    {
        String erg;
        String[] dirListe = new String[0];
        String[] fileListe = new String[0];
        try
        {
            erg = this.aktuellerVerwalter.browseDirs(pfad);
            erg = erg.trim();
            dirListe = erg.split("[;]");

            erg = this.aktuellerVerwalter.browseFiles(pfad);
            erg = erg.trim();
            fileListe = erg.split("[;]");
        } catch (Exception e11)
        {
            client.append("Fehler beim Laden der Ordner / Dateien innerhalb von \"" + pfad + "\"\n");
        }

        /**Baum wird aus den Inhalten dirListe und fileListe zusammengebaut*/
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        tree1.setModel(model);
        tree1.setCellRenderer(new MyTreeCellRenderer()); /**CellRender classe*/
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.setUserObject(aktVerwalter);

        DefaultMutableTreeNode a = new DefaultMutableTreeNode(pfad);
        root.add(a);

        //root.removeAllChildren();
        //root.setUserObject(aktVerwalter);
//        try {
//            root.setUserObject(this.aktuellerVerwalter.getHostName());
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (NotBoundException e) {
//            e.printStackTrace();
//        }

        if (root == null)
        {
            return;
        }

        for (int i = 0; i < dirListe.length; i++)
        {
            if (!dirListe[i].isEmpty())
            {
                //root.add(new DefaultMutableTreeNode(dirListe[i]));
                a.add(new DefaultMutableTreeNode(dirListe[i]));
            }
        }

        for (int i = 0; i < fileListe.length; i++)
        {
            if (!fileListe[i].isEmpty()) {
                Contact temp = new Contact(fileListe[i]);
                a.add(new DefaultMutableTreeNode(temp));
                //root.add(new DefaultMutableTreeNode(temp));
            }
        }
        model.reload(root);
        //a.setUserObject(aktVerwalter);

        int letzerNode = tree1.getRowCount();
        System.out.println(letzerNode);
        tree1.expandRow(letzerNode-1);
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