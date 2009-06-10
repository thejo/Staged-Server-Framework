/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net;

import java.util.*;
import java.security.NoSuchAlgorithmException;

import in.kote.ssf.util.BasicUtilities;

/**
 * Provides an implementation of a consistent hash with weighted nodes
 * 
 * @author Thejo
 */
public class ConsistentHash {
    /**
     * Consistent hash for external consumption
     */
    public static SortedMap<Integer, EndPoint> consistentHash =
            new TreeMap<Integer, EndPoint>();
    
    /**
     * An ordered map which represents the circle of a consistent hash
     */
    private SortedMap<Integer, EndPoint> newCircle = 
            new TreeMap<Integer, EndPoint>();
    
    /**
     * The total number of virtual nodes. Each real node will be converted to a 
     * number of virtual nodes depending on its weight
     */
    public static final int virtualNodeCount = 1000;
    private int totalWeight;
    
    /**
     * Constructor
     * 
     * @param nodes - A collection of endpoints
     * @see com.netcore.adserver.net.EndPoint
     */
    public ConsistentHash(Collection<EndPoint> nodes) 
            throws NoSuchAlgorithmException {
        //Get the total weight of all nodes
        for (EndPoint node : nodes) {
            totalWeight += node.getWeight();
        }
        
        //Add all nodes
        for (EndPoint node : nodes) {
            add(node);
        }
    }
    
    /**
     * Get the ordered map representing the circle of the consistent hash
     * 
     * @return SortedMap<Integer, EndPoint>
     */
    public SortedMap<Integer, EndPoint> getNewCircle() {
        return newCircle;
    }
    
    /**
     * Calculates and returns the number of replicas or virtual nodes for a 
     * given weight
     * 
     * @param weight - An integer representingthe weight of an endpoint
     * @return The numer of virtual nodes for the given weight
     */
    private int getReplicaCount(int weight) {
        return (virtualNodeCount * weight) / totalWeight;
    }
    
    /**
     * Add a given endpoint to the circle
     * 
     * @param node - <code>EndPoint</code> to be added to the circle
     */
    public void add(EndPoint node) throws NoSuchAlgorithmException {
        int numberOfReplicas = getReplicaCount(node.getWeight());
        for (int i = 0; i < numberOfReplicas; i++) {
            Integer nodeNum = BasicUtilities.getMD5(node.toString()
                        + i).intValue() % virtualNodeCount;
            newCircle.put(nodeNum, node);
        }
    }
    
    /**
     * Remove an endpoint from the circle
     * 
     * @param node - <code>EndPoint</code> to be removed from the circle
     */
    public void remove(EndPoint node) throws NoSuchAlgorithmException {
        int numberOfReplicas = getReplicaCount(node.getWeight());
        for (int i = 0; i < numberOfReplicas; i++) {
            
            Integer nodeNum = BasicUtilities.getMD5(node.toString()
                    + i).intValue() % virtualNodeCount;
            newCircle.remove(nodeNum);
        }
    }
    
    /**
     * Given a key (user identifier in case of ad server) and ordered map 
     * representing the circle, returns the EndPoint to which the key maps.
     * 
     * @param key - The user identifier
     * @param circle - The consistent hash circle being used
     * @return <code>EndPoint</code> to which the key maps
     */
    public static EndPoint get(Object key, SortedMap<Integer, EndPoint> circle) 
            throws NoSuchAlgorithmException {
        if (circle.isEmpty()) {
            return null;
        }
        
        int hash = 0;
        
        hash = BasicUtilities.getMD5((String) key).intValue()
                % ConsistentHash.virtualNodeCount;
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, EndPoint> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        
        return circle.get(hash);
    }

    /**
     * The static field of this class should be populated if this method is used
     * 
     * @param key
     * @return
     * @throws java.security.NoSuchAlgorithmException
     */
    public static EndPoint get(Object key) throws NoSuchAlgorithmException {
        return get(key, consistentHash);
    }
}
