/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package main.java.in.kote.ssf.net.response;

/**
 * Represents a response to client requests
 * 
 * @author Thejo
 */
public interface IResponse {
    
    /**
     * Fetch a response string to be sent to the client
     * 
     * @return
     */
    public String response();

    /**
     * Generate the response string in HTML format
     *
     * @return
     */
    public String toHTML();
    
    /**
     * Generate the response string in XML format
     * 
     * @return
     */
    public String toXML();
    
    /**
     * Generate JSON response
     * 
     * @return
     */
    public String toJSON();
    
    /**
     * Generate response in CSV format
     * 
     * @return
     */
    public String toCSV();
}
