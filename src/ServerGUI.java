/**
 * Created by Eugen Eberle on 18.08.2016.
 */
import rmifs.*;

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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerGUI extends JFrame implements ActionListener
{
    private JTextField portTextFeld;
    private JButton starteServerButton;
    private JTextArea serverTextArea;
    private JPanel serverPanel;

    private final String SERVER_HOST_IP = "127.0.0.1";
    JFrame frame = new JFrame("ServerGUI");

    public ServerGUI()
    {
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
            try {
                //Security Manager ermöglicht/regelt zugriff auf Klasse
                if (System.getSecurityManager() == null) {
                    System.setSecurityManager(new SecurityManager());
                }
                FileSystemServer fsServer = new FileSystemServer();
                //Registry erstellen um Objekt ansprechen zu können
                Registry registry = LocateRegistry.createRegistry(serverPort);
                //Stellt das Objekt dem System zur Verfügung
                FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(fsServer, serverPort);
                //Objekt an registry binden
                registry.rebind("FileSystemServer", stub);

                append("FileSystemServer: " + fsServer.getOSName()
                        + "- Server bound .....Port " + serverPort);

                /**System.out.print("\nServer Name:\t" + fsServer.getHostName()
                 + "\nServer IP:\t\t" + fsServer.getHostAddress()
                 + "\nServer runs on\t" + fsServer.getOSName() + "\n");*/
            }
            catch (RemoteException rex)
            {
                rex.printStackTrace();
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
     * Hauptmethode
     * Startet den Server
     */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy" );
        new ServerGUI();

    }
}
