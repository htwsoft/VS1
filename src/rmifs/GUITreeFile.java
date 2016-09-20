/**
* Klasse zum besseren Arbeiten mit dem Inhalt eines
 * * JTree fuer ein FileSystem
* @author Marco Palumbo
* @version 1.0
*/

package rmifs;

/**
* Klasse file dient zum Unterscheiden zwischen ordner und Datei
* zusaetzlich werden Dateiname und Pfadangabe getrennt
*/
public class GUITreeFile
{
	private String fileName; //Datei-/Ordnername
	private String pathName; //Datei-/Ordner-Pfad
	private String fullName; //Datei-/Ordnername mit Pfad
	private boolean isDir;
	private boolean isRootDir;
	
	/**
	* Konstruktor einer File-Klasse
	* @param fullName name der Datei oder des Ordners
	* @param isDir ist true wenn es sich um eine Datei handelt sonst 
	* @param isRootDir ist true wenn es sich um den Startordner handelt
	*/
	public GUITreeFile(String fullName, boolean isDir, boolean isRootDir, boolean isWindows)
	{
		this.fullName = fullName;
		this.isRootDir = isRootDir;
		//Pruefen ob es sich um den Root-Ordner handelt
		if(isRootDir)
		{
			//Root-Ordner
			this.pathName = fullName;
			this.fileName = fullName;
		}
		else
		{
			//bei nicht root-ordner pfad von datei/filename trennen
			trennePfadVonName(fullName, isWindows);
		}
		this.isDir = isDir;
	}
	
	/**
	* Methode trennt die Pfadangabe vom namen
	* @param fullName vollstaendiger Pfad mit Pfadangabe
	* @param isWindows ist true wenn es ein Windows System ist
	*/
	private void trennePfadVonName(String fullName, boolean isWindows)
	{
		int positionSlash = 0;
		if(isWindows)
		{
			//Windows hat eine andere Pfadangabe als Linux
			//C:\temp\test.txt
			positionSlash = fullName.lastIndexOf("\\");
		}
		else
		{
			//Linux
			// /temp/test.txt
			positionSlash = fullName.lastIndexOf("/");
		}
		this.fileName = fullName.substring(positionSlash+1);
		this.pathName = fullName.substring(0, positionSlash+1);
	}
	
	/**
	* Funktion toString liefert den Namen der Datei oder des Ordners
	* @return Name der Datei / des Ordners
	*/	
	public String getFileName()
	{
		return this.fileName;
	}

	/**
	* Funktion toString liefert die Pfadangabe einer Datei oder eines Ordners
	* @return Name der Datei / des Ordners
	*/		
	public String getPathName()
	{
		return this.pathName;
	}
	
	/**
	* Funktion toString liefert den vollstaendigen Namen der Datei oder des Ordners mit Pfadangabe
	* @return Name der Datei / des Ordners
	*/		
	public String getFullName()
	{
		return this.fullName;
	}
	
	/**
	* Gibt an ob es ein Ordner oder eien Datei ist
	* @return true wenn es ein Ordner ist / false bei Dateien
	*/
	public boolean getIsDir()
	{
		return this.isDir;
	}
	
	/**
	* Gibt an ob es sich um den Root-Ordner handelt
	* @return true wenn es ein Ordner ist / false bei Dateien
	*/
	public boolean getIsRootDir()
	{
		return this.isRootDir;
	}
	
	/**
	* Funktion toString liefert den Namen der Datei oder des Ordners
	* @return Name der Datei / des Ordners
	*/
	public String toString()
	{
		return this.getFileName();
	}
}
