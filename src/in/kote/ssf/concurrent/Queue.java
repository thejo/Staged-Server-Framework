/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.concurrent.*;

/**
 *
 * @author Thejo
 */
public class Queue {
    public static enum Type {
        LINKED_BLOCKING_QUEUE,
        PRIORITY_BLOCKING_QUEUE,
        ARRAY_BLOCKING_QUEUE,
        SYNCHRONOUS_QUEUE};

    public static BlockingQueue<Runnable> getQueue(Type type) {
        BlockingQueue<Runnable> queue = null;
        
        switch(type) {
            case LINKED_BLOCKING_QUEUE:
                queue = new LinkedBlockingDeque<Runnable>();
                break;
            case PRIORITY_BLOCKING_QUEUE:
                queue = new PriorityBlockingQueue<Runnable>();
                break;
            case ARRAY_BLOCKING_QUEUE:
                queue = new ArrayBlockingQueue<Runnable>(Integer.MAX_VALUE);
                break;
            case SYNCHRONOUS_QUEUE:
                queue = new SynchronousQueue<Runnable>();
                break;
            default:
                queue = new LinkedBlockingDeque<Runnable>();
        }
        
        return queue;
    }
}
