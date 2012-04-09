/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.concurrent.*;

/**
 * This is a wrapper class for the {@link ThreadPoolExecutor} to be used when the backing queue is a
 * {@link PriorityBlockingQueue}. See this bug report for details -
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6539720
 */

public final class ComparableThreadPoolExecutor extends ThreadPoolExecutor {

    public ComparableThreadPoolExecutor(int corePoolSize,
                                        int maximumPoolSize,
                                        long keepAliveTime,
                                        TimeUnit unit,
                                        BlockingQueue<Runnable> workQueue,
                                        ThreadFactory threadFactory)
    {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        super.prestartAllCoreThreads();
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor (Runnable runnable, T value) {
        return new ComparableFutureTask<T> (runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor (Callable<T> callable) {
        return new ComparableFutureTask<T> (callable);
    }

    protected class ComparableFutureTask<V> extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {
        Comparable comparable;

        ComparableFutureTask (Callable<V> callable) {
            super (callable);
            comparable = (Comparable) callable;
        }

        ComparableFutureTask (Runnable runnable, V result) {
            super (runnable, result);
            comparable = (Comparable) runnable;
        }

        @SuppressWarnings("unchecked")
        public int compareTo (ComparableFutureTask<V> ftask) {
            return comparable.compareTo (ftask.comparable);
        }
    }
}