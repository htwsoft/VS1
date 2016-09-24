package rmifs;

import javax.lang.model.element.Element;
import java.io.Serializable;

/**
 *
 * <br>Liste der FileServer, angelegt zur Nutzung in einer ArrayList</br>
 * @author cpatzek
 * @version 1.02
 * @date 2016-09-24
 */
public class FileServerListenElement implements Serializable
{
    private String serverName;

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public String getServerIP()
    {

        return serverIP;
    }

    private String serverIP;

    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }

    private int serverPort;

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
