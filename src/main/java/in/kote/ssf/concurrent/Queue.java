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
        LINKED_BLOCKING_DEQUE,
        PRIORITY_BLOCKING_QUEUE,
        ARRAY_BLOCKING_QUEUE,
        SYNCHRONOUS_QUEUE,
        DELAY_QUEUE};

    public static BlockingQueue<Runnable> getQueue(Type type) {
        BlockingQueue<Runnable> queue = null;
        
        switch(type) {
            case LINKED_BLOCKING_QUEUE:
                queue = new LinkedBlockingQueue<Runnable>();
                break;
            case LINKED_BLOCKING_DEQUE:
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
            case DELAY_QUEUE:
                /* If a Delay queue is used, the element added to the queue
                 should implement Runnable and Delayed */
                queue = new DelayQueue();
                break;
            default:
                queue = new LinkedBlockingQueue<Runnable>();
        }
        
        return queue;
    }
}
