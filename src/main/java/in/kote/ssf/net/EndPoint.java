/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net;

import java.net.*;


/**
 * Represents a node in the cluster <br />
 * 
 * This code is based on the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 * 
 * @author Thejo
 */
public class EndPoint implements Comparable<EndPoint> {
    
    private String host_;
    private int port_;
    private int weight_;
    private int id_;
    private String type_;
    
    private transient InetSocketAddress ia_;
    public static volatile String localHost;
    
    public EndPoint(String host, int port)
    {
        
        host_ = host;
        port_ = port;
        
        //Resolve the IP address at object creation time
        setInetSocketAddress(host, port);
    }

    public String getHost()
    {
        return host_;
    }

    public int getPort()
    {
        return port_;
    }

    public void setPort(int port)
    {
        port_ = port;
    }

    public int getWeight() {
        return weight_;
    }

    public void setWeight(int weight) {
        this.weight_ = weight;
    }

    public int getId() {
        return id_;
    }

    public void setId(int id) {
        this.id_ = id;
    }

    public String getType() {
        return type_;
    }

    public void setType(String type) {
        this.type_ = type;
    }

    public InetSocketAddress getInetSocketAddress()
    {
        if (ia_ == null || ia_.isUnresolved())
        {
            ia_ = new InetSocketAddress(host_, port_);
        }
        return ia_;
    }
    
    public void setInetSocketAddress(String host, int port) {
        if (ia_ == null || ia_.isUnresolved()) {
            ia_ = new InetSocketAddress(host_, port_);
        }
    }
    
    public InetAddress getInetAddress()
    {
        return getInetSocketAddress().getAddress();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof EndPoint)) {
            return false;
        }

        EndPoint rhs = (EndPoint) o;
        return (host_.equals(rhs.host_) && port_ == rhs.port_);
    }

    @Override
    public int hashCode()
    {
        return (host_ + port_).hashCode();
    }

    public int compareTo(EndPoint rhs)
    {
        return host_.compareTo(rhs.host_);
    }

    @Override
    public String toString()
    {
        return (host_ + ":" + port_);
    }

    public static EndPoint fromString(String str)
    {
        String[] values = str.split(":");
        return new EndPoint(values[0], Integer.parseInt(values[1]));
    }
}
