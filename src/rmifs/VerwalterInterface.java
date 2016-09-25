package rmifs;

/**
 * <br>Interface für die VerwalterServer</br>
 * @author cpatzek & soezdemir
 * @version 1.05
 * @date 2016-09-14
 */

import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

interface VerwalterInterface extends Remote
{
    public String initialBrowseDirs(String dir) throws RemoteException, NotBoundException;
    public String initialBrowseFiles(String dir) throws RemoteException, NotBoundException;
    public String getServerList() throws RemoteException, NotBoundException;
    public String search(String file, String startDir) throws RemoteException, NotBoundException;
    public String browseDirs(String dir, int server) throws RemoteException, NotBoundException;
    public String browseFiles(String dir, int server) throws RemoteException, NotBoundException;
    public ArrayList<String> getAllVerwalterNames(int index)throws RemoteException, NotBoundException;
    public FileServerListenElement getVerwalter(int verwalter) throws RemoteException, NotBoundException;
    public ArrayList<String> getAllFileServerNames(int index) throws RemoteException, NotBoundException;
    //public boolean search(String file, String startDir) throws RemoteException;
    public String createFile(String file, int server) throws RemoteException, NotBoundException;
    public String createDir(String dir, int server) throws RemoteException, NotBoundException;
    public String delete(String file, int server) throws RemoteException, NotBoundException;
    public String rename(String oldName, String newName, int server) throws RemoteException, NotBoundException;
    public String getOSName(int server) throws RemoteException, NotBoundException;
    public String getHostName(int server) throws RemoteException, NotBoundException;
    public String getHostAddress(int server) throws RemoteException, NotBoundException;
    public String sendClientAddress(String clientAddress) throws RemoteException, NotBoundException;
    public Path [] getFileList() throws RemoteException, NotBoundException;
    //public String sendClientName(String clientName) throws RemoteException;//ToDo
    //public String sendClientOS(String clientOS) throws RemoteException; //ToDo

}
