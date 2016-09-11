import rmifs.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Fuse on 11.09.2016.
 */
public class VerwalterGUI extends JFrame implements VerwalterInterface, RMIClientSocketFactory, ActionListener, Remote
{
    private JPanel verwalterPanel;
    private JTextField verwalterTextFeld;
    private JButton starteVerwalterButton;
    private JTextArea verwalterTextArea;

    private FSInterface fsserver;

    /** enthaelt die Liste aller verfuegbaren(remote) VerwalterServer und indirekt deren verbundenen FileServer */
    private static final String VERWALTER_LISTE = "Name: Server1 IP: xxx.xxx.xxx.xxx\n" +
            "Name: Server2 IP: yyy.yyy.yyy.yyy\n" +
            "Name: Server3 IP: zzz.zzz.zzz.zzz";

    /** HOST entspricht der IP-Adresse des lokalen FileServer */
    private final static String HOST= null;

    /** PORT_NR entspricht dem gebundenen Port des FileServers */
    private final static int PORT_NR = 4567;

    /** PORT_NR2, diese muss als Parameter beim Client angegeben werden */
    private final static int PORT_NR2 = 4568;


    private FileSystem fs = new FileSystem();
    public static VerwalterInterface vServer = new VerwalterGUI();
    //public  VerwalterGUI vServer = new VerwalterGUI();

    public VerwalterGUI()
    {
        JFrame frame = new JFrame("VerwalterGUI");
        frame.setContentPane(verwalterPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1000, 400);
        verwalterTextArea.append("Hallo \n\n");
        starteVerwalterButton.addActionListener(this);

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
                //Security Manager ermöglicht/regelt zugriff auf Klasse
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager ( new SecurityManager() );
                }
                Registry registry = LocateRegistry.getRegistry(HOST, serverPort);
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer");

                System.setProperty("java.rmi.server.hostname","192.168.0.103");
                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(vServer, serverPort+1);

                registry.rebind("VerwalterServer", stub);
                append("Server bound ...");

//                //Stellt das Objekt dem System zur Verfügung
//                FSInterface stub = (FSInterface) UnicastRemoteObject.exportObject(vServer, serverPort);
//                //Registry erstellen um Objekt ansprechen zu können
//                Registry registry =  LocateRegistry.createRegistry(serverPort);
//                //Objekt an registry binden
//                registry.rebind("FileSystemServer", stub);
//                append("Server bound ...\n");


                //Stellt das Objekt dem System zur Verfügung
                //VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(vServer, PORT_NR2);
                //Registry erstellen um Objekt ansprechen zu können
                //Registry registry =  LocateRegistry.createRegistry(PORT_NR2);
                //Objekt an registry binden

            }
            catch(Exception e2)
            {
                append("Fehler_startButton: " + e2.toString());
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




    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException
    {
        //return new Socket(host, port);
        return null;
    }

    /** @return Name und IP-Adressen aller VerwalterServer */
    @Override
    public String getServerList() throws RemoteException
    {
        //return VERWALTER_LISTE;
        return null;
    }
}
