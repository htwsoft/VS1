import rmifs.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Fuse on 11.09.2016.
 */
public class VerwalterGUI extends JFrame implements ActionListener
{
    private JPanel verwalterPanel;
    private JTextField verwalterTextFeld;
    private JButton starteVerwalterButton;
    private JTextArea verwalterTextArea;

    /** enthaelt die Liste aller verfuegbaren(remote) VerwalterServer und indirekt deren verbundenen FileServer */
    private static final String VERWALTER_LISTE = "Name: Server1 IP: xxx.xxx.xxx.xxx\n" +
            "Name: Server2 IP: yyy.yyy.yyy.yyy\n" +
            "Name: Server3 IP: zzz.zzz.zzz.zzz";
    private String SYSTEM_HOST_IP;

    JFrame frame = new JFrame("VerwalterGUI");

    public VerwalterGUI()
    {
        SYSTEM_HOST_IP   = "127.0.0.1";
        System.setProperty("java.rmi.server.hostname", SYSTEM_HOST_IP);

        this.setContentPane(verwalterPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setSize(1000, 400);
        verwalterTextArea.append("Hallo \n\n");
        starteVerwalterButton.addActionListener(this);

        /** Logo laden, muss im selben dir sein wie die java Files oder absoluten Pfad angeben */
        ImageIcon img = new ImageIcon("htw.png");
        frame.setIconImage(img.getImage());
    }

    /**
     * Button gedrückt
     */
    public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource();
        if(o == starteVerwalterButton)
        {
            int serverPort;
            try
            {
                serverPort = Integer.parseInt(verwalterTextFeld.getText().trim());
            } catch(Exception er)
            {
                append("Fehler bei der Port-Eingabe.\n");
                return;
            }
            try
            {
                VerwalterServer verwalterServer = new VerwalterServer(serverPort+1, SYSTEM_HOST_IP);

                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, serverPort+1);
                Registry registry       =  LocateRegistry.createRegistry(serverPort+1);
                registry.rebind("VerwalterServer", stub);

                verwalterServer.log("\nServer bound ...\tPort open at " + (serverPort+1));
            }
            catch (RemoteException rex)
            {
                rex.printStackTrace();
            }
            catch (NotBoundException nbe)
            {
                nbe.printStackTrace();
            }

            // Button deaktivieren nach Start
            starteVerwalterButton.setEnabled(false);
            // Portfeld deaktivieren nach Start
            verwalterTextFeld.setEditable(false);
        }
    }

    /**Zum zum anhaengen von Text in die TextArea*/
    void append(String text)
    {
        verwalterTextArea.append(text);
        verwalterTextArea.setCaretPosition(verwalterTextArea.getText().length() - 1);
    }

    /** MAIN */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        //System.setProperty("java.security.policy", "java.policy");
        System.setProperty("java.security.policy", "java.policy" );
    }

}