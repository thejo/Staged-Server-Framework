/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net;

import java.net.*;

import in.kote.ssf.util.*;
import in.kote.ssf.concurrent.*;

/**
 * The parent class for all servers used in the application <br />
 * Though this class is not part of any stage, it extends <code>StageTask</code> 
 * to allow the flexibilty of determining the first stage messages are handed 
 * over to at run time in sub-classes.
 * 
 * @author Thejo
 */
public abstract class AbstractServer extends StageTask 
        implements Runnable, Shutdownable {
    
  public ServerSocket server;
  Thread thread;
  boolean done;
  protected int port;

  public AbstractServer(int port) {
      this.port = port;
  }
  
  /**
   * Start the server in a different thread
   */
  public synchronized void startServer() {
    if (thread == null) {
      done = false;
      thread = new Thread( this );
      thread.start();
    }
  }

  public synchronized void shutdown() {
    done = true;
    try {
      thread.interrupt();
      server.close();
    } catch (Exception ex) {
        //Log if required
    }
  }

  public abstract void run();

  public synchronized boolean getDone() {
    return done;
  }
}

