package rmifs;

import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * <br>Ermittelt Netzwerkinformationen des Clients und stellt
 * diese dem FileSystemClient zur Verfuegung</br>
 * @author  soezdemir, cpatzek
 * @version 1.03
 * @date    2016-09-14
  */
public class NetworkController {

    private String clientAddress = "";
    private String clientName = "";
    private String clientOS = "";

    private FileSystemClient client;

    /**
     * <br>Konstruktor initialisiert den Client</br>
     *
     * @param client
     */
    public NetworkController(FileSystemClient client) throws RemoteException, NotBoundException {
        init(client);
    }

    private void init(FileSystemClient client) throws NotBoundException {
        try {
            setClient(client);
            getNetworkInformation();
            setClientOS();
        } catch (SocketException soe){
            soe.printStackTrace();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        } catch (RemoteException rex) {
            rex.printStackTrace();
        }
    }

    /**
     * <br>Setter für NetworkController um den Client zu setzen </br>
     *
     * @param client
     */
    public void setClient(FileSystemClient client){
        if (client == null)
            throw new NullPointerException("Client darf nicht Null sein!");

        this.client = client;
    }

    /**
     * <br>Getter um den aktuellen Client zu erhalten </br>
     *
     * @return client   gibt den übergebenen Client zurück
     */
    public FileSystemClient getClient(){
        return this.client;
    }


    /**
     * <br>Methoder zur Einholung der Netzwerkschnittstellen eth0, wlan0, localhost</br>
     *
     * @throws SocketException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    private void getNetworkInformation() throws SocketException, UnknownHostException, RemoteException, NotBoundException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets))
            if( netint.isUp() && !netint.isLoopback()) //filtert nur aktive Netzwerkschnittstelle
                displayInterfaceInformation(netint);//Methodenaufruf displayInterfaceInformation()
    }

    /**
     * <br>Methode um Daten der Netzwerkschnittstelle auszugeben (nur aktive Verbindungen IPv4)</br>
     * @param netint networkInterface
     * @throws SocketException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    private void displayInterfaceInformation(NetworkInterface netint) throws SocketException, UnknownHostException, RemoteException, NotBoundException {
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses))
            if(inetAddress.toString().length() <= 15 && inetAddress.toString().length() >= 7){
                getClient().setClientAddress(inetAddress.getHostAddress());
                //getClient().sendClientAddress(inetAddress.getHostAddress());
            }
    }

    /**
     * <br>Getter zur Ermittlung des Hostnamens</br>
     * @return clientName   liefer Namen der Maschine
     */
    public String getClientName()
    {
        try {
            InetAddress clientMachine = Inet4Address.getLocalHost();
            clientName = clientMachine.getHostName();
            //System.out.println("ClientName:\t" + clientName);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return clientName;
    }

    /**
     *
     * @return IPv4 Adresse - liefert aber nur localhost Adresse 127.0.1.1
     */
    public String getClientAddress()
    {
        try {
            InetAddress clientIP = Inet4Address.getLocalHost();
            clientAddress = clientIP.getHostAddress();
            System.out.println("ClientIP:\t" + clientAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientAddress;
    }

    /**
     * <br>Setter für den Client (Betriebssystem und Prozessorarchitektur)</br>
     */
    public void setClientOS()
    {
        try {
            String clientOS = System.getProperty("os.name") +
                    ", Version " + System.getProperty("os.version") +
                    " on " + System.getProperty("os.arch") + " architecture.";
            getClient().setClientOS(clientOS);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
     }

    @Override
    public final String toString()
    {
        String output = "IP Address: " + getClient().getClientAddress() +
                " | Client: "  + getClientName() + "\n" + //ToDo getClient().getClientName()
                "OS Name: " + getClient().getClientOS();
        return output;
    }



}

