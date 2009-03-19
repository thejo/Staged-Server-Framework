/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * This class is an implementation of the <i>ThreadFactory</i> interface. This 
 * is useful to give Java threads meaningful names which is useful when using 
 * a tool like JConsole. <br />
 * 
 * This code is originally from the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */

public class ThreadFactoryImpl implements ThreadFactory
{
    protected String id_;
    protected ThreadGroup threadGroup_;
    protected final AtomicInteger threadNbr_ = new AtomicInteger(1);
    
    public ThreadFactoryImpl(String id)
    {
        SecurityManager sm = System.getSecurityManager();
        threadGroup_ = ( sm != null ) ? sm.getThreadGroup() :
            Thread.currentThread().getThreadGroup();
        id_ = id;
    }    
    
    public Thread newThread(Runnable runnable)
    {        
        String name = id_ + ":" + threadNbr_.getAndIncrement();       
        Thread thread = new Thread(threadGroup_, runnable, name);        
        return thread;
    }
}