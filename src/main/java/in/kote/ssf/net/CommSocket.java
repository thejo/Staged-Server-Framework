/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net;

import java.net.*;

/**
 * This class represents the socket and other details associated with the 
 * communication with a client
 * 
 * @author Thejo
 */
public class CommSocket {
    private Socket socket;
    private String connId;
    private String hostAddress;
    private long requestReceivedTime;
    
    public static final String MASTER_PREFIX = "m";
    public static final String WORKER_PREFIX = "w";

    public CommSocket (Socket socket, int connId) {
        this(socket, connId, "c");
    }
    
    public CommSocket (Socket socket, int connId, String type) {
        this.socket = socket;
        this.connId = type + "-" + connId;
        this.hostAddress = socket.getInetAddress().getHostAddress();
        this.requestReceivedTime = System.nanoTime();
    }

    public String getConnId() {
        return connId;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public long getRequestReceivedTime() {
        return requestReceivedTime;
    }

    public void setRequestReceivedTime(long requestReceivedTime) {
        this.requestReceivedTime = requestReceivedTime;
    }
}
