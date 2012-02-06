/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.concurrent.*;

/**
 * This is a wrapper class for the <i>ScheduledThreadPoolExecutor</i>.
 * 
 * This code is originally from the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */
public class DebuggableScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    
    public DebuggableScheduledThreadPoolExecutor(int threads,
            ThreadFactory threadFactory)
    {
        super(threads, threadFactory);        
    }
}
