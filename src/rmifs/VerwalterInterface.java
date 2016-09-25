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

public interface VerwalterInterface extends Remote
{
    public String initialBrowseDirs(String dir) throws RemoteException, NotBoundException;
    public String initialBrowseFiles(String dir) throws RemoteException, NotBoundException;
    public String getServerList() throws RemoteException, NotBoundException;
    public String search(String file, String startDir) throws RemoteException, NotBoundException;
    public String browseDirs(String dir, String server) throws RemoteException, NotBoundException;
    public String browseFiles(String dir, String server) throws RemoteException, NotBoundException;
    public String[] getAllVerwalterNames()throws RemoteException, NotBoundException;
    public FileServerListenElement getVerwalter(int verwalter) throws RemoteException, NotBoundException;
    public String[] getAllFileServerNames() throws RemoteException, NotBoundException;
    //public boolean search(String file, String startDir) throws RemoteException;
    public String createFile(String file, String server) throws RemoteException, NotBoundException;
    public String createDir(String dir, String server) throws RemoteException, NotBoundException;
    public String delete(String file, String server) throws RemoteException, NotBoundException;
    public String rename(String oldName, String newName, String server) throws RemoteException, NotBoundException;
    public String getOSName(String server) throws RemoteException, NotBoundException;
    public String getHostName(String server) throws RemoteException, NotBoundException;
    public String getHostAddress(String server) throws RemoteException, NotBoundException;
    public String sendClientAddress(String clientAddress) throws RemoteException, NotBoundException;
    public Path [] getFileList() throws RemoteException, NotBoundException;
    //public String sendClientName(String clientName) throws RemoteException;//ToDo
    //public String sendClientOS(String clientOS) throws RemoteException; //ToDo

}
