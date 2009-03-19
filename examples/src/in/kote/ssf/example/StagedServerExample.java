/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.example;

import java.io.IOException;

import in.kote.ssf.concurrent.Stage;
import in.kote.ssf.concurrent.StageManager;
import in.kote.ssf.concurrent.MultiThreadedStage;
import in.kote.ssf.net.AbstractServer;
import in.kote.ssf.util.Shutdownable;
import in.kote.ssf.util.ShutdownHandler;

import in.kote.ssf.example.net.http.HttpServer;
import in.kote.ssf.example.concurrent.StagedServerStage;

/**
 *
 * @author Thejo
 */
public class StagedServerExample {

    public static void main(String[] args) {
        try {
            StagedServerExample stagedServer = new StagedServerExample(args);
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Constructor
     * 
     * @param args
     */
    private StagedServerExample(String[] args) {
        registerStages();
        AbstractServer httpServer = null;

        try {
            httpServer = new HttpServer( 8080 );
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }

        //Configure shutdown hooks. All stage threadpools are
        //shutdown by the handler
        ShutdownHandler sh = new ShutdownHandler();
        sh.addTask((Shutdownable) httpServer);
        Runtime.getRuntime().addShutdownHook(sh);

        httpServer.startServer();

        System.out.println("Listening on port 8080");

        // Everything's running in independent stages
        while (true) {
          // Sleep for 6 hours now
          // Adjust this to requirements
          try { Thread.sleep(6*60*60*1000); }
          catch (Exception ex) { }
        }
    }

    /**
     * Register the different stages
     */
    private void registerStages() {
        StageManager.registerStage(Stage.HTTP_SERVER_STAGE,
                new MultiThreadedStage(Stage.HTTP_SERVER_STAGE, 10 ));
        StageManager.registerStage(StagedServerStage.APPLICATION_STAGE,
                new MultiThreadedStage(StagedServerStage.APPLICATION_STAGE, 10));
        StageManager.registerStage(Stage.RESPONSE_STAGE,
                new MultiThreadedStage(Stage.RESPONSE_STAGE, 10 ));

    }
}
