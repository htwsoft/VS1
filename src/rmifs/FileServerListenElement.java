package rmifs;

import javax.lang.model.element.Element;

/**
 * Created by cpatzek on 23.09.16.
 * Liste der FileServer, angelegt zur Nutzung in einer ArrayList
 */
public class FileServerListenElement
{
    public String serverName;

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public String serverIP;

    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }

    public int serverPort;

    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }

    public FileServerListenElement()
    {
        serverName = "default";
        serverPort = 1111;
        serverIP = "192.168.0.1";
    }

    public FileServerListenElement(String serverName, String serverIP, int serverPort)
    {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.serverName = serverName;
    }
}
