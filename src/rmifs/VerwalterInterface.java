//package src.rmifs;
package rmifs;

/**
 * Interface für die VerwalterServer
 * @author cpatzek & soezdemir
 * @version 1.03
 * @date 2016-09-14
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;

interface VerwalterInterface extends Remote{
    public String getServerList() throws RemoteException;
    public String browseDirs(String dir) throws RemoteException;
    public String browseFiles(String dir) throws RemoteException;
    public String search(String file, String startDir) throws RemoteException;
    public boolean createFile(String file) throws RemoteException;
    public boolean createDir(String dir) throws RemoteException;
    public boolean delete(String file) throws RemoteException;
    public boolean rename(String oldName, String newName) throws RemoteException;
    public String getOSName()throws RemoteException;
    public String getHostName() throws RemoteException;
    public String getHostAddress() throws RemoteException;
    public void setClientAddress(String clientAddress) throws RemoteException;
    //public String setClientName(String clientName) throws RemoteException;
    //public String setClientOS(String clientOS) throws RemoteException; //ToDo
}
