package rmifs;

import java.net.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author  soezdemir & cpatzek
 * @version 1.03
 * @date    2016-09-14
 *
 * Kurzbeschreibung:
 *      Ermittelt Netzwerkinformationen des Clients
 */
public class NetworkController {

    private String clientAddress = "";
    private String clientName = "";
    private String clientOS = "";

    private FileSystemClient client;

    /**Konstruktor initialisiert den Client
     *
     * @param client
     */
    public NetworkController(FileSystemClient client) {
        init(client);
    }

    private void init(FileSystemClient client) {
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
     * Setter für NetworkController
     * um den Client zu setzen
     *
     * @param client
     */
    public void setClient(FileSystemClient client){
        if (client == null)
            throw new NullPointerException("Client darf nicht Null sein!");

        this.client = client;
    }

    /**
     * Getter um den aktuellen Client zu erhalten
     *
     * @return client   gibt den übergebenen Client zurück
     */
    public FileSystemClient getClient(){
        return this.client;
    }


    /**
     * Methoder zur Einholung der Netzwerkschnittstellen eth0, wlan0, localhost
     *
     * @throws SocketException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    private void getNetworkInformation() throws SocketException, UnknownHostException, RemoteException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets))
            if( netint.isUp() && !netint.isLoopback()) //filtert nur aktive Netzwerkschnittstelle
                displayInterfaceInformation(netint);//Methodenaufruf displayInterfaceInformation()
    }

    /**
     * Methode um Daten der Netzwerkschnittstelle auszugeben (nur aktive Verbindungen IPv4)
     * @param netint networkInterface
     * @throws SocketException
     * @throws UnknownHostException
     * @throws RemoteException
     */
    private void displayInterfaceInformation(NetworkInterface netint) throws SocketException, UnknownHostException, RemoteException {
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses))
            if(inetAddress.toString().length() <= 15 && inetAddress.toString().length() >= 7){
                getClient().setClientAddress(inetAddress.getHostAddress());
                getClient().sendClientAddress(inetAddress.getHostAddress());
            }
    }

    /**
     * Getter zur Ermittlung des Hostnamens
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
     * Setter für den Client (Betriebssystem und Prozessorarchitektur)
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

