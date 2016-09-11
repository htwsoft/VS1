import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Christian Patzek on 03.09.2016.
 * Interface für die Verwalter
 */
interface VerwalterInterface extends Remote
{
    public String getServerList() throws RemoteException;
//    public String browseDirs(String dir) throws RemoteException;
//    public String browseFiles(String dir) throws RemoteException;
//    public String search(String file, String startDir) throws RemoteException;
//    public boolean createFile(String file) throws RemoteException;
//    public boolean createDir(String dir) throws RemoteException;
//    public boolean delete(String file) throws RemoteException;
//    public boolean rename(String oldName, String newName) throws RemoteException;
//    public String getOSName()throws RemoteException;
//    public String getHostName() throws RemoteException; //ToDoooooooooooooooooooooooooooo
//    public String getHostAdress() throws RemoteException; //ToDoooooooooooooooooooooooooooo
}
