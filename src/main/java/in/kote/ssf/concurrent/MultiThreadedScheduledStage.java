/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is an implementation of the <i>IStage</i> interface. In particular
 * it is for a stage that has a thread pool with multiple threads, with each 
 * thread running a scheduled job. For details, please refer to the <i>IStage</i> 
 * documentation. <br />
 * 
 * This code is based on the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */

public class MultiThreadedScheduledStage implements IStage
{    
    private String name_;
    private DebuggableScheduledThreadPoolExecutor executorService_;
            
    public MultiThreadedScheduledStage(String name, int numThreads)
    {        
        name_ = name;        
        executorService_ = new DebuggableScheduledThreadPoolExecutor( numThreads,
                new ThreadFactoryImpl(name) );        
    }
    
    public String getName() {        
        return name_;
    }    

    public <T> Future<T> execute(Callable<T> callable) {
        return executorService_.submit(callable);
    }
    
    public void execute(Runnable runnable) {
        executorService_.execute(runnable);
    }
    
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callable) 
            throws InterruptedException {
        return executorService_.invokeAll(callable);        
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> callable,
            long timeout, TimeUnit unit) throws InterruptedException {
        return executorService_.invokeAll(callable, timeout, unit);
    }
    
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
    {
        throw new UnsupportedOperationException("This operation is not supported");
    }
    
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, 
            long initialDelay, long period, TimeUnit unit) {
        
        return executorService_.scheduleAtFixedRate(command, initialDelay, 
                period, unit);
    }
    
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, 
            long initialDelay, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException("This operation is not supported");
    }
    
    public void shutdown() {  
        executorService_.shutdownNow(); 
    }
    
    public boolean isShutdown()
    {
        return executorService_.isShutdown();
    }
    
    public long getTaskCount(){
        return (executorService_.getTaskCount() 
                - executorService_.getCompletedTaskCount());
    }
}