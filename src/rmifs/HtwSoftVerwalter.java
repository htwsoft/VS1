//package src.rmifs;
package rmifs;

/**
 * Startet den VerwalterServer
 * @author soezdemir
 * @version 1.02
 * @date 2016-09-16
 */

import java.lang.String;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class HtwSoftVerwalter {

    private final static String SERVER_HOST_IP_1 = "192.168.0.24";
    private final static String SERVER_HOST_IP_2 = "192.168.0.23";
    private final static String SERVER_HOST_IP_3 = "192.168.0.11";
    private final static String SERVER_HOST_FGVT = "172.19.1.209"; //localhost in fgvt

    /**
     * HOST entspricht der IP-Adresse des lokalen FileServers
     */
    private final static String HOST = SERVER_HOST_IP_1;

    /**
     * PORT_NR entspricht dem gebundenen Port des FileServers
     */
    private final static int PORT_NR = 4711;


    public static void main(String args[]){

        //**** regelt RMI Kommunikation ***** muss anfang der main bleiben
        //und setzt policy fest
        System.setProperty("java.security.policy", "policy/java.policy" );
        init(); // initialisiert den VerwalterServer mit server port und ip
                // für den Client


    }


    public static void init(){

        try {

            VerwalterServer verwalterServer = new VerwalterServer(PORT_NR, HOST);

            //Noetig fuer RMI Client Anbindung zum VerwalterServer z.B. 192.168.0.11 Port 4711
            System.setProperty("java.rmi.server.hostname", SERVER_HOST_IP_1); //"172.19.1.209" fgvt
            //Stellt das Objekt dem System zur Verfuegung
            VerwalterInterface stub = (VerwalterInterface) UnicastRemoteObject.exportObject(verwalterServer, PORT_NR+1);
            //Registry erstellen um Objekt ansprechen zu können
            Registry registry =  LocateRegistry.createRegistry(PORT_NR+1); //ToDo lookup fuer VerwalterServer & FileServer
            //Objekt an registry binden
            registry.rebind("VerwalterServer", stub);


            verwalterServer.log("\nServer bound ...\tPort open at " + ((PORT_NR)+1));
        } catch (RemoteException rex){
            rex.printStackTrace();
        } catch (NotBoundException nbe){
            nbe.printStackTrace();
        }

    }




}//ENDE
