/**
* RMI-Klasse zum Darstellen eines Dateisystems
* @author Marco Palumbo
* @version 1.0
*/

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FSInterface extends java.rmi.Remote 
{
	public String browseDirs(String dir) throws RemoteException;
	public String browseFiles(String dir) throws RemoteException;
	public String search(String file, String startDir) throws RemoteException;
	public boolean createFile(String file) throws RemoteException;
	public boolean createDir(String dir) throws RemoteException;
	public boolean delete(String file) throws RemoteException;
	public boolean rename(String oldName, String newName) throws RemoteException;
	public String getOSName()throws RemoteException;
}