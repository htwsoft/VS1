import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * Created by Eugen Eberle on 20.08.2016.
 */

public class ClientGUI extends JFrame implements ActionListener
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
    private JLabel ip;
    private JLabel ipLabel;
    private JButton sWechselButton;
    private JLabel banner;

    private VerwalterInterface vServer;

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
        root.setUserObject(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        model.nodeChanged(root);

        model.reload(root);

        frame.pack();
        frame.setVisible(true);
        frame.setSize(1100, 800);
        //frame.setResizable(false);
        frame.setLocation(10, 10);

        /**Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad eingeben */
        ImageIcon img = new ImageIcon("htw.png");
        frame.setIconImage(img.getImage());

        clientTextArea.append("Hallo \n\n");
        addListener();

        deaktiviereButtons();

        frame.pack();
        frame.setLocation(50, 50);


//        /**Die Handles unsichtbar machen, bringt leider nicht viel da immernoch anklickbar*/
//        tree1.setUI(new BasicTreeUI()
//        {
//            @Override
//            protected boolean shouldPaintExpandControl(final TreePath path, final int row, final boolean isExpanded, final boolean hasBeenExpanded, final boolean isLeaf)
//            {
//                boolean shouldDisplayExpandControl = false;
//                return shouldDisplayExpandControl;
//            }
//        });


        /** listener fuer den tree*/
        tree1.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override public void valueChanged(TreeSelectionEvent ae)
            {
                //tree1.setToggleClickCount(0);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) ae.getPath().getLastPathComponent();
                //event verlassen wenn keine Node ausgewaehlt wurde
                if (node == null)
                    return;
                DefaultMutableTreeNode dirNode;
                String pfad = node.toString();
                client.append("You selected: " + pfad + "\n");
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
                            dirNode = new DefaultMutableTreeNode(dirList[i]);

                            node.add(dirNode);
                            //Dummy node anhängen um Ordnerbild zu erzeuegen
                            dirNode.add(new DefaultMutableTreeNode(""));
                        }
                    }
                    //verarbeite der gefundenen dateien
                    for (int j = 0; j < fileList.length; j++)
                    {
                        if (!fileList[j].equals(""))
                        {
                            node.add(new DefaultMutableTreeNode(fileList[j]));
                        }
                    }
                } catch (RemoteException re)
                {
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

        if(aeSource == startClientButton)
        {
            startClientButton();
        }
        else
        if(aeSource == OSInfoButton)
        {
            OSInfoButton();
        }
        else
        if(aeSource == createDirButton)
        {
            createDirButton();
        }
        else
        if(aeSource == createFileButton)
        {
            createFileButton();
        }
        else
        if(aeSource == browseButton)
        {
            browseButton();
        }
        else
        if(aeSource == seachButton)
        {
            searchButton();
        }
        else
        if(aeSource == deleteButton)
        {
            deleteButton();
        }
        else
        if(aeSource == renameButton)
        {
            renameButton();
        }
        else
        if(aeSource == sWechselButton)
        {
            wechselButton();
        }
    }

    /**
     * fuehrt die Aktion des startClientButton-button aus
     * */
    private void startClientButton()
    {
        int serverPort;
        String host = "192.168.0.103";
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
            Registry registry = LocateRegistry.getRegistry(host, serverPort+1);
            this.vServer = (VerwalterInterface) registry.lookup("VerwalterServer");
            client.append("Verbunden...\n");

            ipLabel.setText(host);
            // Start-Button deaktivieren nach Start
            startClientButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            portTextFeld.setEditable(false);
            aktiviereButtons();
        }
        catch(Exception e2)
        {
            client.append( "Fehler: " + e2.toString() );
        }
    }

    /**
     * fuehrt die Aktion des OSInfoButton-button aus
     * */
    private void OSInfoButton()
    {
        try
        {
            client.append(" Verwendetes OS: " + this.vServer.getOSName() + "\n");
            client.append(" Name des Hosts:  " + this.vServer.getHostName() + "\n\n");
        }
        catch(Exception eOS)
        {
            client.append("Fehler: " + eOS.getMessage()+"\n");
        }
    }

    /**
     * fuehrt die Aktion des createDir-button aus
     * */
    private void createDirButton()
    {
        JFrame eingabe = new JFrame();
        String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll erstellt werden?", "Create Directory", JOptionPane.PLAIN_MESSAGE);
        try
        {
            if( this.vServer.createDir(pfad) )
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
            client.append("Fehler: " + eDir.getMessage()+"\n");
        }
    }

    /**
     * fuehrt die Aktion des createFile-button aus
     * */
    private void createFileButton()
    {
        JFrame eingabe = new JFrame();
        String pfad = JOptionPane.showInputDialog(eingabe, "Welche Datei soll erstellt werden?", "Create File", JOptionPane.PLAIN_MESSAGE);
        try
        {
            if( this.vServer.createFile(pfad) )
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
            client.append("Fehler: " + eFile.getMessage() + "\n");
        }
    }

    /**
     * fuehrt die Aktion des search-button aus
     * */
    private void searchButton()
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
                erg = this.vServer.search(searchPfad, startDir);
                fileListe2 = erg.split("[;]");
                client.append("Found-Files: \n");
                client.append("---------------------------------------------------------------\n");
                for (int i = 0; i < fileListe2.length; i++)
                {
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
     * fuehrt die Aktion des browse-Button aus
     * */
    private void browseButton()
    {
        JFrame eingabe = new JFrame();
        String pfad = JOptionPane.showInputDialog(eingabe, "Welcher Ordner soll untersucht werden?", "Browse", JOptionPane.PLAIN_MESSAGE);

        String erg = null;
        String [] dirListe = new String[0];
        String [] fileListe = new String[0];

        try
        {
            erg = this.vServer.browseDirs(pfad);
            dirListe = erg.split("[;]");

            erg = this.vServer.browseFiles(pfad);
            fileListe = erg.split("[;]");
        }
        catch(IOException e11)
        {
            client.append("Fehler: " + e11.getMessage() + "\n");
        }

        /**Baum wird aus den Inhalten dirListe und fileListe zusammengebaut*/
        DefaultTreeModel model = (DefaultTreeModel)tree1.getModel();
        tree1.setModel(model);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        root.removeAllChildren();
        root.setUserObject(pfad + " " + new SimpleDateFormat("HH:mm:ss").format(new Date()));

        if (root == null)
            return;
        DefaultMutableTreeNode dirNode;
        root.removeAllChildren();
        for (int i = 0; i < dirListe.length; i++)
        {
            if(!dirListe[i].equals(""))
            {
                //root.add(new DefaultMutableTreeNode(dirListe[i]));
                dirNode = new DefaultMutableTreeNode(dirListe[i]);
                root.add(dirNode);
                //Dummy node anhängen um Ordnerbild zu erzeuegen
                dirNode.add(new DefaultMutableTreeNode(""));
            }
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

    /**
     *  Funktion fuehrt die AKtion des delete Buttons aus
     * */
    private void deleteButton()
    {
        String wahl = clientTextArea.getText().trim();
        String [] parts = wahl.split(":");
        String loeschPfad = parts[parts.length - 1].trim();

        int jaNein = JOptionPane.showConfirmDialog(null, "Soll  " +loeschPfad+ "  wirklich geloescht werden?", "Delete", JOptionPane.YES_NO_OPTION);

        if(jaNein == JOptionPane.YES_OPTION)
        {
            try
            {
                if( this.vServer.delete(loeschPfad) )
                {
                    JOptionPane.showMessageDialog(null, loeschPfad+ "  wurde geloescht!", "Delete", JOptionPane.INFORMATION_MESSAGE);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT geloescht werden!", "Delete", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException eDelete)
            {
                client.append("Fehler: " + eDelete.getMessage());
            }
        }
    }

    /**
     * Funtion fuehrt die Aktion fuer den Rename Button aus
     * */
    private void renameButton()
    {
        JFrame eingabe = new JFrame();
        String oldName = JOptionPane.showInputDialog(eingabe, "Was soll umbeannt werden?", "Rename", JOptionPane.PLAIN_MESSAGE);
        String newName = JOptionPane.showInputDialog(eingabe, "Wie lautet die neue Bezeichnung?", "Rename", JOptionPane.PLAIN_MESSAGE);
        try
        {
            if( this.vServer.rename(oldName, newName) )
            {
                JOptionPane.showMessageDialog(null, "Ordner oder Datei wurde umbenannt!", "Rename", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Ordner oder Datei konnte NICHT umbenannt werden!", "Rename", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(IOException eRename)
        {
            client.append("Fehler: " + eRename.getMessage() + "\n");
        }
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

    /**
     * deaktiviert die Steuer-Elemente
     * */
    private void deaktiviereButtons()
    {
        browseButton.setEnabled(false);
        seachButton.setEnabled(false);
        createDirButton.setEnabled(false);
        createFileButton.setEnabled(false);
        deleteButton.setEnabled(false);
        renameButton.setEnabled(false);
        OSInfoButton.setEnabled(false);
        searchFeld.setEnabled(false);
        sWechselButton.setEnabled(false);
        tree1.setEnabled(false);
    }

    /**
     * aktiviert alle Steuer-Elemente
     * */
    private void aktiviereButtons()
    {
        //Buttons aktivieren
        browseButton.setEnabled(true);
        seachButton.setEnabled(true);
        createDirButton.setEnabled(true);
        createFileButton.setEnabled(true);
        deleteButton.setEnabled(true);
        renameButton.setEnabled(true);
        OSInfoButton.setEnabled(true);
        searchFeld.setEnabled(true);
        sWechselButton.setEnabled(true);
        tree1.setEnabled(true);
    }

    /**
     *  fuegt die Listener zu den Steuer-Elementen
     * */
    private void addListener()
    {
        startClientButton.addActionListener(this);
        browseButton.addActionListener(this);
        seachButton.addActionListener(this);
        createDirButton.addActionListener(this);
        createFileButton.addActionListener(this);
        deleteButton.addActionListener(this);
        renameButton.addActionListener(this);
        OSInfoButton.addActionListener(this);
        sWechselButton.addActionListener(this);
        /** listener fuer den tree*/
        //tree1.addTreeSelectionListener(new GUITreeSelectionListener(vServer));
    }

    public static void main(String[] args) throws IOException
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
        client = new ClientGUI();
    }
}