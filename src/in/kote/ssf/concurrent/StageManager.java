/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages all stages that exist within a process. The application registers
 * and de-registers stages with this abstraction. Any component that has the <i>ID</i> 
 * associated with a stage can obtain a handle to actual stage. <br />
 * 
 * This code is originally from the 
 * <a href="http://code.google.com/p/the-cassandra-project/">Cassandra project</a> 
 * open sourced by Facebook
 */

public class StageManager
{
    private static Map<String, IStage > stageQueues_ = new HashMap<String, IStage>();

    /**
     * Register a stage with the StageManager
     * @param stageName stage name.
     * @param stage stage for the respective message types.
     */
    public static void registerStage(String stageName, IStage stage)
    {
        stageQueues_.put(stageName, stage);
    }

    /**
     * Retrieve a stage from the StageManager
     * @param stageName name of the stage to be retrieved.
    */
    public static IStage getStage(String stageName)
    {
        return stageQueues_.get(stageName);
    }

    /**
     * Deregister a stage from StageManager
     * @param stageName stage name.
     */
    public static void deregisterStage(String stageName)
    {
        stageQueues_.remove(stageName);
    }

    /**
     * This method gets the number of tasks on the
     * stage's internal queue.
     * @param stage name of the stage
     * @return
     */
    public static long getStageTaskCount(String stage)
    {
        return stageQueues_.get(stage).getTaskCount();
    }

    /**
     * This method shuts down all registered stages.
     */
    public static void shutdown()
    {
        for ( IStage registeredStage : stageQueues_.values() )
        {
            registeredStage.shutdown();
        }
    }
}