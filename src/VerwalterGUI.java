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
    private final static String HOST = null;

    public static VerwalterInterface vServer = new VerwalterGUI();

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
                //Security Manager ermöglicht/regelt zugriff auf Klasse
                if (System.getSecurityManager() == null)
                {
                    System.setSecurityManager ( new SecurityManager() );
                }
                Registry registry = LocateRegistry.getRegistry(HOST, serverPort); //HOST ist immer null?
                this.fsserver = (FSInterface) registry.lookup("FileSystemServer"); //schaue nach dem Server?

                System.setProperty("java.rmi.server.hostname","192.168.0.102"); //hier die eigene IP
                VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(vServer, serverPort+1);

                Registry registry2 =  LocateRegistry.createRegistry(serverPort+1); //neue registry2 fuer Verwalter

                registry2.rebind("VerwalterServer", stub);
                append("Server bound ...");

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

    /** MAIN */
    public static void main(String args[])
    {
        //Propertys aus Datei laden
        System.setProperty("java.security.policy", "java.policy");
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException
    {
        return new Socket(host, port);
    }

    /** @return Name und IP-Adressen aller VerwalterServer */
    @Override
    public String getServerList() throws RemoteException
    {
        return VERWALTER_LISTE;
    }

    public boolean rename(String oldName, String newName) throws RemoteException
    {
        return this.fsserver.rename(oldName, newName);
    }

    public String getOSName()throws RemoteException
    {
        return this.fsserver.getOSName();
    }

    //ToDoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
    public String getHostName() throws RemoteException
    {
        return this.fsserver.getHostName();
    }

    public String getHostAdress() throws RemoteException
    {
        return this.fsserver.getHostAdress();
    }


    public boolean delete(String file) throws RemoteException
    {
        return this.fsserver.delete(file);
    }

    public boolean createFile(String file) throws RemoteException
    {
        return this.fsserver.createFile(file);
    }

    public boolean createDir(String dir) throws RemoteException
    {
        return fsserver.createDir(dir);
    }

    public String search(String file, String startDir) throws RemoteException
    {
        String erg = this.fsserver.search(file, startDir);
        if(erg.equals(""))
        {
            return ("Nichts gefunden");
        }
        else
            return erg;
    }

    public String browseFiles(String dir) throws RemoteException
    {
        return this.fsserver.browseFiles(dir);
    }

    public String browseDirs(String dir) throws RemoteException
    {
        return this.fsserver.browseDirs(dir);
    }


    @Override
    public File getFile(String pfad) throws RemoteException
    {
        return this.fsserver.getFile(pfad);
    }

    @Override
    public FileTreeModel getFileTreeModel(File wurzel) throws RemoteException
    {
        return this.fsserver.getFileTreeModel(wurzel);
    }


}