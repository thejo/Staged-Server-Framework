/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.*;
import java.util.concurrent.*;

/**
 * An abstraction for stages as described in the SEDA paper by Matt Welsh. 
 * For reference to the paper look over here 
 * <a href="http://www.eecs.harvard.edu/~mdw/papers/seda-sosp01.pdf">SEDA: An 
 * Architecture for WellConditioned, Scalable Internet Services</a>. <br />
 * 
 * This code is based on the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */

public interface IStage
{
    public String getName();
    
    /**
     * This method is used to execute a piece of code on
     * this stage. The idea is that the <i>run()</i> method
     * of this Runnable instance is invoked on a thread from a
     * thread pool that belongs to this stage.
     * @param runnable instance whose run() method needs to be invoked.
     */
    public void execute(Runnable runnable);
    
    /**
     * This method is used to execute a piece of code on
     * this stage which returns a Future pointer. The idea
     * is that the <i>call()</i> method of this Runnable 
     * instance is invoked on a thread from a thread pool 
     * that belongs to this stage.
     
     * @param callable instance that needs to be invoked.
     * @return
     */
    public <T> Future<T> execute(Callable<T> callable);
    
    /**
     * Executes the given tasks, returning a list of Futures holding their 
     * status and results when all complete.
     * 
     * @param callable
     * @return
     */
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callable) 
            throws InterruptedException;
    
    /**
     * This method is used to submit tasks to this stage
     * that execute periodically. 
     * 
     * @param command the task to execute.
     * @param initialDelay the time to delay first execution 
     * @param unit the time unit of the initialDelay and period parameters 
     * @return
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit); 
      
    /**
     * This method is used to submit tasks to this stage
     * that execute periodically. 
     * @param command the task to execute.
     * @param initialDelay the time to delay first execution
     * @param period the period between successive executions
     * @param unit the time unit of the initialDelay and period parameters 
     * @return
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, 
            long initialDelay, long period, TimeUnit unit); 
    
    /**
     * This method is used to submit tasks to this stage
     * that execute periodically. 
     * @param command the task to execute.
     * @param initialDelay the time to delay first execution
     * @param delay  the delay between the termination of one execution and the commencement of the next.
     * @param unit the time unit of the initialDelay and delay parameters 
     * @return
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, 
            long initialDelay, long delay, TimeUnit unit);
    
    /**
     * Shutdown the stage. All the threads of this stage
     * are forcefully shutdown. Any pending tasks on this
     * stage could be dropped or the stage could wait for 
     * these tasks to be completed. This is however an 
     * implementation detail.
     */
    public void shutdown();  
    
    /**
     * Checks if the stage has been shutdown.
     * @return
     */
    public boolean isShutdown();
    
    /**
     * This method returns the number of tasks that are 
     * pending on this stage to be executed.
     * @return
     */
    public long getTaskCount();
}