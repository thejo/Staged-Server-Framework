/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.example.net.http;

import java.io.*;
import java.net.*;

import in.kote.ssf.net.*;
import in.kote.ssf.concurrent.*;

/**
 * Listens to a configured port and handles incoming HTTP requests. A new thread
 * is spawned in <code>AbstractServer</code>. It is this thread that accepts
 * the incoming requests.
 * 
 * @author Thejo
 */
public class HttpServer extends AbstractServer {
    public static IStage defaultnextStage = StageManager.getStage(
            Stage.HTTP_SERVER_STAGE);
    
    public HttpServer(int port) throws IOException {

        super(port);
        
        setNextStage( defaultnextStage );
        
        //Accept incoming HTTP requests
        server = new ServerSocket( this.port );
    }    
    
    /**
     * Runs in an infinite loop accepting requests on the port we are listening 
     * to. Every new request is added to the HTTP-SERVER-STAGE threadpool, where
     * it is processed.
     */
    public void run() {
        int conn_id = 0;
        while (!getDone()) {
            try {
                Socket dataSocket = server.accept();                
                CommSocket socket = new CommSocket(dataSocket, conn_id++, 
                        CommSocket.MASTER_PREFIX);
                
                StageTask request = new ExampleHttpRequestTask(socket);
                this.getNextStage().execute( request );

            } catch (Exception ex) {
                // Only report if we want to continue running the server
                if (!getDone()) {}
            }
        }
    }
}
