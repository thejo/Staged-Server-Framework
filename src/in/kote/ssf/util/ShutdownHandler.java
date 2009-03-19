/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.util;

import java.util.*;

import in.kote.ssf.concurrent.*;

/**
 * Enables a clean shutdown of the server
 */

public class ShutdownHandler extends Thread {
  Collection<Shutdownable> tasks;

  public ShutdownHandler() {
    tasks = new Vector<Shutdownable>();
  }

  public ShutdownHandler(Collection<Shutdownable> tasks) {
    this.tasks = tasks;
  }

  public void addTask(Shutdownable task) {
    tasks.add(task);
  }
  
  @Override
  public void run() {
    
    //Shutdown individual threads
    for (Iterator<Shutdownable> i = tasks.iterator(); i.hasNext(); ) {
      Shutdownable sh = i.next();
      try {
        sh.shutdown();
      } catch (Exception ex) {
          //Log if required
      }
    }
    
    //Shutdown all stages
    StageManager.shutdown();
    
    try { // Give threads a couple of seconds to clean things up nicely
      Thread.sleep(2000);
    } catch (Exception ex) {
        //Log if required
    }
  }
}