package rmifs;

/**
 * Interface für die VerwalterServer
 * @author cpatzek & soezdemir
 * @version 1.03
 * @date 2016-09-14
 */

import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

interface VerwalterInterface extends Remote{
    public String getServerList() throws RemoteException;
    public String search(String file, String startDir) throws RemoteException, NotBoundException;
    public String browseDirs(String dir) throws RemoteException, NotBoundException;
    public String browseFiles(String dir) throws RemoteException, NotBoundException;
    //public boolean search(String file, String startDir) throws RemoteException;
    public String createFile(String file) throws RemoteException, NotBoundException;
    public String createDir(String dir) throws RemoteException, NotBoundException;
    public String delete(String file) throws RemoteException, NotBoundException;
    public String rename(String oldName, String newName) throws RemoteException, NotBoundException;
    public String getOSName()throws RemoteException, NotBoundException;
    public String getHostName() throws RemoteException, NotBoundException;
    public String getHostAddress() throws RemoteException, NotBoundException;
    public void sendClientAddress(String clientAddress) throws RemoteException;
    public Path [] getFileList() throws RemoteException, NotBoundException;
    //public String sendClientName(String clientName) throws RemoteException;//ToDo
    //public String sendClientOS(String clientOS) throws RemoteException; //ToDo

}
