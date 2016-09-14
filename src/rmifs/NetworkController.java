package rmifs;

import java.net.*;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.in;
import static java.lang.System.out;

/**
 * Created by soezdemir on 14.09.16.
 */
public class NetworkController {

    private String clientAddress = "";
    private String clientName = "";
    private String clientOS = "";


    private FileSystemClient client;

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
        } catch (RemoteException ree) {
            ree.printStackTrace();
        }
    }

    public void setClient(FileSystemClient client){
        if (client == null)
            throw new NullPointerException("Client darf nicht Null sein!");

        this.client = client;
    }

    public FileSystemClient getClient(){
        return this.client;
    }

    private void getNetworkInformation() throws SocketException, UnknownHostException, RemoteException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

        for (NetworkInterface netint : Collections.list(nets))
            if( netint.isUp() && !netint.isLoopback())
                displayInterfaceInformation(netint);
    }

    private void displayInterfaceInformation(NetworkInterface netint) throws SocketException, UnknownHostException, RemoteException {
        //out.printf("Display name: %s\n", netint.getDisplayName());
        //out.printf("Name: %s\n", netint.getName());

        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses))
            if(inetAddress.toString().length() <= 15){
                getClient().setClientAddress(inetAddress.getHostAddress());
                getClient().sendClientAddress(inetAddress.getHostAddress());
            }

    }


    public String getClientName()
    {
        //String clientName = "";
        try {
            InetAddress clientMachine = Inet4Address.getLocalHost();
            clientName = clientMachine.getHostName();
            System.out.println("ClientName:\t" + clientName);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return clientName;
    }

    public String getClientAddress()
    {
        //String clientAddress = "";
        try {
            InetAddress clientIP = Inet4Address.getLocalHost();
            clientAddress = clientIP.getHostAddress();
            System.out.println("ClientIP:\t" + clientAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientAddress;
    }

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
        String output = "\nIP Address: " + getClient().getClientAddress() +
                " | Client: "  + getClientName() +
                " | OS Name: " + getClient().getClientOS();
        return output;
    }



}

