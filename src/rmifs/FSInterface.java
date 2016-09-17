package rmifs;

import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI-Klasse zum Darstellen eines Dateisystems
 * @author Marco Palumbo
 * @version 1.01
 */


public interface FSInterface extends Remote
{
	public String browseDirs(String dir) throws RemoteException;
	public String browseFiles(String dir) throws RemoteException;
	public boolean search(String file, String startDir) throws RemoteException;
	public boolean createFile(String file) throws RemoteException;
	public boolean createDir(String dir) throws RemoteException;
	public boolean delete(String file) throws RemoteException;
	public boolean rename(String oldName, String newName) throws RemoteException;
	public String getOSName()throws RemoteException;
	public String getHostName() throws RemoteException;
	public String getHostAddress() throws RemoteException;
	public void sendClientAddress(String clientAddress) throws RemoteException;
	public String getClientAddress() throws  RemoteException;
	public Path[] getFileList() throws RemoteException;
	//public String sendClientName(String clientName) throws RemoteException; //ToDo
	//public String sendClientOS(String clientOS) throws RemoteException; //ToDo
}