//package src.rmifs;
package rmifs;

/**
* RMI-Klasse zum Darstellen eines Dateisystems
* @author Marco Palumbo
* @version 1.01
*/

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public interface FSInterface extends Remote
{
	public String browseDirs(String dir) throws RemoteException;
	public String browseFiles(String dir) throws RemoteException;
	public String search(String file, String startDir) throws RemoteException;
	public boolean createFile(String file) throws RemoteException;
	public boolean createDir(String dir) throws RemoteException;
	public boolean delete(String file) throws RemoteException;
	public boolean rename(String oldName, String newName) throws RemoteException;
	public String getOSName()throws RemoteException;
	public String getHostName() throws RemoteException;
	public String getHostAddress() throws RemoteException; //ToDo
	//public String getClientAddress() throws RemoteException //ToDo
	//public String getClientName() throws RemoteException //ToDo
	//public String getClientOS() throws RemoteException //ToDo
}