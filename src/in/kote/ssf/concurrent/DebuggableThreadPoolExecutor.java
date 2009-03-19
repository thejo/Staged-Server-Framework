/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.concurrent.*;

/**
 * This is a wrapper class for the <i>ScheduledThreadPoolExecutor</i>. <br />
 * 
 * This code is originally from the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */

public final class DebuggableThreadPoolExecutor extends ThreadPoolExecutor
{
    
    public DebuggableThreadPoolExecutor(int corePoolSize,
            int maximumPoolSize,
            long keepAliveTime,
            TimeUnit unit,
            BlockingQueue<Runnable> workQueue,
            ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        super.prestartAllCoreThreads();
    }
}