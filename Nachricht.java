/**
 * Created by Christian Patzek on 18.06.2016.
 * Nachricht extends interface Remote, all messages are transmitted via this interface
 */


import java.rmi.Remote;
import java.rmi.RemoteException;


interface Nachricht extends Remote
{
    String nachricht(String auswahl) throws RemoteException;
}
