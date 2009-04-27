package in.kote.ssf.net;

import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.locks.*;

import org.apache.log4j.Logger;

import in.kote.ssf.util.BasicUtilities;
import in.kote.ssf.exceptions.SocketPoolException;

/**
 * Socket pool implementation. <br />
 * An application cluster can consist of multiple endpoints in a multi-master
 * or master-slave setup. A master which receives a request may need to hand off
 * part of the request to a worker on a different endpoint. Since we don't want
 * to open a new connection every time it is needed, we'll cache the connections
 * between endpoints and use them whenever needed.
 * 
 * @author Thejo
 */
public class SocketPool {

    private static final Lock lock = new ReentrantLock();
    private static Map<EndPoint, LinkedList<SocketPoolEntry>> socketPool =
            new HashMap<EndPoint, LinkedList<SocketPoolEntry>>();
    private static List<EndPoint> endPoints;
    private static int perEndPointpoolSize;
    
    /**
     * Represents an entry in the list of connections to an endpoint
     */
    public static class SocketPoolEntry {
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        
        public SocketPoolEntry(InetAddress address, int port) throws IOException {
            socket = new Socket(address, port);
            outputStream = new ObjectOutputStream( socket.getOutputStream() );
            inputStream = new ObjectInputStream( socket.getInputStream() );
        }

        public Socket getSocket() {
            return socket;
        }

        public ObjectInputStream getInputStream() {
            return inputStream;
        }

        public ObjectOutputStream getOutputStream() {
            return outputStream;
        }
    }
    
    /**
     * Don't allow object instantiation. Everything is static
     */
    private SocketPool() { }
    
    /**
     * Initialize the pool.
     * 
     * @param endPointList
     * @param poolSize
     * @see com.netcore.adserver.net.EndPoint
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static void initPool(List<EndPoint> endPointList, int poolSize) 
            throws SocketPoolException {
        
        if(socketPool.size() > 0) {
            throw new SocketPoolException("Socket pool has already been initialized");
        }
        
        if(null == endPointList || endPointList.isEmpty() || poolSize == 0) {
            throw new SocketPoolException("Invalid initialization parameters");
        }
        
        endPoints = endPointList;
        perEndPointpoolSize = poolSize;

        for (EndPoint ep : endPoints) {
            //Don't create a pool of connections to self
            if( BasicUtilities.isHostLocalHost(ep.getHost()) ) {
                continue;
            }

            LinkedList<SocketPoolEntry> socketList = initEndPoint(ep);
            socketPool.put(ep, socketList);
        }
    }
    
    /**
     * Initialize an endpoint. Just returns an empty linked list to hold the 
     * cached connections
     * @param ep - The endpoint to be initialized
     * @return LinkedList<SocketPoolEntry>
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static LinkedList<SocketPoolEntry> initEndPoint(EndPoint ep) 
            throws SocketPoolException {
        
        LinkedList<SocketPoolEntry> socketList = new LinkedList<SocketPoolEntry>();
        
        /* Don't connect on initialization. Lazy connect is better when there 
         * are multiple nodes in the cluster
        for(int i = 0; i < perEndPointpoolSize; i++) {
            SocketPoolEntry entry = null;
            
            
            try {
                entry = getNewSocket(ep);
            } catch (IOException ioe) {
                throw new SocketPoolException(ioe.getMessage());
            }

            socketList.add(entry);
        }
        */
        
        return socketList;
    }
    
    /**
     * Close all cached socket connections for a given endpoint
     * 
     * @param ep - The endpoint we want to disconnect from
     */
    public static void disconnectEndPoint(EndPoint ep) {
        
        LinkedList<SocketPoolEntry> socketList = socketPool.get(ep);
        for (SocketPoolEntry entry : socketList) {
            if(! entry.getSocket().isClosed()) try {
                entry.getSocket().close();
            } catch (IOException ioe) {
                //The endpoint is no longer in use anyway
            }
            
            //The endpoint has to be removed from the socketPool by the caller 
            //in a thread safe way
        }
    }
    
    /**
     * Creates and returns a new socket connection to an endpoint in the form 
     * of a <code>SocketPoolEntry</code> object
     * 
     * @param ep - The endpoint to which the new connection is required
     * @return An objct of <code>SocketPoolEntry</code>
     * @throws java.io.IOException
     */
    private static SocketPoolEntry getNewSocket(EndPoint ep) throws IOException {
        SocketPoolEntry entry = new SocketPoolEntry( 
                ep.getInetAddress(), ep.getPort() );
        
        if( log.isDebugEnabled() ) {
            log.debug("Created new socket to endpoint " + ep.toString());
        }
        
        return entry;
    }
    
    /**
     * Given an endpoint returns a cached connection. This method uses a lazy 
     * connect, i.e, if a cached connection is not available, it creates a new 
     * one and returns it. This will ensure that we don't have to block till a 
     * connection is available. Makes the implementation simpler and reduces 
     * chances of starvation. The one problem is if too many connections are 
     * opened. That is an not likely to happen in case of the ad server
     * 
     * @param ep - The endpoint to which you need to connect
     * @return A <code>SocketPoolEntry</code> object
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static SocketPoolEntry getSocketPoolEntry(EndPoint ep) 
            throws SocketPoolException {
        
        SocketPoolEntry entry = null;

        lock.lock();
        try {            
            if(null == ep) {
                throw new SocketPoolException("Request socket for invalid EndPoint");
            }
            
            //Get a connection from the cache if available
            if( socketPool.containsKey(ep) && socketPool.get(ep).size() > 0 ) {
                LinkedList<SocketPoolEntry> socketList = socketPool.get(ep);
                //Retrieve and remove the head
                entry = socketList.poll();
                
                //Remove this endpoint if there are no other sockets available
                if(socketList.size() == 0) {
                    socketPool.remove(ep);
                } else {
                    socketPool.put(ep, socketList);
                }
                
                if( log.isDebugEnabled() ) {
                    log.debug("Retrieved socket to endpoint " + ep.toString());
                }
                
            } else {
                //Create a new connection and return that
                try {
                    entry = getNewSocket(ep);
                } catch (IOException ioe) {
                    throw new SocketPoolException(ioe.getMessage());
                }
            }
            
            return entry;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Return a connection to the pool after use. This implementation tries to 
     * enforce the pool size defined when the socket pool was initialized, i.e, 
     * if the number of cached connections for an endpoint is equal to the max. 
     * allowed number, the connection is simply closed.
     * 
     * @param ep - The endpoint for which we want to return the connection
     * @param entry - The <code>SocketPoolEntry</code> object to be cached
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static void returnSocketPoolEntry(EndPoint ep, SocketPoolEntry entry) 
            throws SocketPoolException {

        lock.lock();
        try {
            
            if(null == ep || null == entry) {
                throw new SocketPoolException("Can't add SocketEntry back to " +
                        "pool. Invalid EndPoint or SocketEntry.");
            }
            
            LinkedList<SocketPoolEntry> socketList = new LinkedList<SocketPoolEntry>();
            
            if( socketPool.containsKey(ep) ) {
                socketList = socketPool.get(ep);
                
                if (socketList.size() < perEndPointpoolSize) {
                    socketList.add(entry);
                } else {
                    if( ! entry.getSocket().isClosed() ) try {
                        entry.getSocket().close();
                    } catch (Exception ex) {
                        log.warn(ex.toString(), ex);
                    } finally {
                        log.info("Closed excess socket to endpoint " + 
                                ep.toString());
                    }
                }                
                
            } else {
                socketList.add(entry);
            }
            
            socketPool.put(ep, socketList);
            
            if( log.isDebugEnabled() ) {
                log.debug("Returned socket to endpoint " + ep.toString());
            }            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Utility function to make writing to given socket easier
     * 
     * @param socket
     * @return PrintWriter
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static PrintWriter getOut(Socket socket) throws SocketPoolException {
        PrintWriter out = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception ex) {
            throw new SocketPoolException(ex.getMessage());
        }
        
        return out;
    }
    
    /**
     * Utility function to make reading from a given socket easier
     * 
     * @param socket
     * @return BufferedReader
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static BufferedReader getIn(Socket socket) throws SocketPoolException {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
        } catch (Exception ex) {
            throw new SocketPoolException(ex.getMessage());
        }
        
        return in;
    }
    
    /**
     * The Ad server allows adding and removal of endpoints to a cluster. It 
     * periodically reads the list of available endpoints from the database and 
     * updates the socket pool as needed. <br />
     * To remove an endpoint from a cluster, remove the corresponding entry from 
     * the table which maintains the list. Wait for the next update cycle of the 
     * ad server, and then disconnect the machine from the network. <br />
     * To add a new machine to a cluster, first connect it to the network, i.e, 
     * make it accessible to the other servers in the cluster, then add an entry 
     * in the table which maintains the list. The ad server will automatically 
     * start sending the new machine requests after the next update cycle
     * 
     * @param newList
     * @throws com.netcore.adserver.exceptions.SocketPoolException
     */
    public static void updatePool(List<EndPoint> newList) 
            throws SocketPoolException {
        
        //Get the set of newly added endpoints
        HashSet<EndPoint> addedEndPoints = new HashSet<EndPoint>( newList );
        addedEndPoints.removeAll( endPoints );

        //Get the set of removed endpoints
        HashSet<EndPoint> removedEndPoints = new HashSet<EndPoint>( endPoints );
        removedEndPoints.removeAll( newList );

        lock.lock();
        try {
            //Update the socket pool with connections to new endpoints
            if(addedEndPoints.size() > 0) for (EndPoint ep : addedEndPoints) {
                if( BasicUtilities.isHostLocalHost(ep.getHost()) ) {
                    continue;
                }

                LinkedList<SocketPoolEntry> socketList = initEndPoint(ep);
                socketPool.put(ep, socketList);
            }

            //Disconnect from removed endpoints
            if(removedEndPoints.size() > 0) for (EndPoint ep: removedEndPoints) {
                /**
                 * Don't remove if the endpoint is still part of the consistent hash. 
                 * The socket connections may be in use. We'll remove them in the 
                 * next update cycle.
                 */
                if(  ConsistentHash.consistentHash.containsValue(ep) ) {
                    continue;
                }

                disconnectEndPoint(ep);
                socketPool.remove(ep);
            }

            //Update the current list of endpoints
            endPoints = new ArrayList<EndPoint>( newList );
        
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Shutdown the socket pool. Closes cached connections to all endpoints. We 
     * don't have to worry about requests for connections or returns to the pool 
     * during shutdown if all the ad server stages have already been shutdown
     */
    public static void shutdown() {

        lock.lock();
        try {
            
            for(Map.Entry<EndPoint, LinkedList<SocketPoolEntry>> entry : 
                socketPool.entrySet()) {
                    
                EndPoint ep = entry.getKey();
                
                disconnectEndPoint(ep);
                socketPool.remove(ep);                
            }
            
        }  finally {
            lock.unlock();
        }
    }//End of shutdown() method
    
    private static final Logger log = Logger.getLogger(SocketPool.class);
}
