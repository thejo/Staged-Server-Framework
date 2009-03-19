/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.example.net.http;

import in.kote.ssf.concurrent.IStage;
import in.kote.ssf.concurrent.Stage;
import in.kote.ssf.concurrent.StageTask;
import in.kote.ssf.concurrent.StageManager;
import in.kote.ssf.net.CommSocket;
import in.kote.ssf.net.http.HttpRequest;
import in.kote.ssf.net.http.HttpRequestHandler;

import in.kote.ssf.example.application.ExampleStage;
import in.kote.ssf.example.concurrent.StagedServerStage;
import in.kote.ssf.example.net.response.HttpRequestError;

/**
 * Class which handles incoming HTTP requests and hands over to the
 * appropriate stage
 * 
 * @author Thejo
 */
public class ExampleHttpRequestTask extends StageTask {
    private CommSocket socket;
    public static IStage defaultnextStage = StageManager.getStage(
            Stage.HTTP_SERVER_STAGE);

    public ExampleHttpRequestTask(CommSocket dataSocket) {
        this.socket = dataSocket;

        defaultnextStage = StageManager.getStage(
            StagedServerStage.APPLICATION_STAGE);
        setNextStage( defaultnextStage );
    }

    public void run() {
        Runnable nextAction = null;

        try {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler(this.socket);
            HttpRequest httpRequest = httpRequestHandler.process();

            //If required, validate the parsed request here

            //Hand over to next stage
            nextAction = new ExampleStage(httpRequest);

        } catch (Exception ex) {
            //An error occurred
            //Send output via response stage
            setNextStage( StageManager.getStage(Stage.RESPONSE_STAGE) );
            nextAction = new HttpRequestError(this.socket, ex.getMessage());
        }

        getNextStage().execute(nextAction);
    }
}
