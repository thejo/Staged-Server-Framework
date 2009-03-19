/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.example.application;

import java.util.Map;

import in.kote.ssf.concurrent.IStage;
import in.kote.ssf.concurrent.Stage;
import in.kote.ssf.concurrent.StageTask;
import in.kote.ssf.concurrent.StageManager;
import in.kote.ssf.util.StringUtil;
import in.kote.ssf.net.http.HttpRequest;

import in.kote.ssf.example.net.response.SuccessResponse;

/**
 * An example stage of the application
 * 
 * @author Thejo
 */
public class ExampleStage extends StageTask {
    
    private HttpRequest httpRequest;

    /**
     * Default next stage - can be changed at run time
     */
    public static IStage defaultnextStage = StageManager.getStage(
            Stage.RESPONSE_STAGE);

    public ExampleStage(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;

        setNextStage( defaultnextStage );
    }

    @Override
    public void run() {
        Map<String, String> requestArgs = this.httpRequest.getRequestArgs();
        String[] pieces = new String[requestArgs.size()];
        
        int i = 0;
        for(Map.Entry<String, String> entry : requestArgs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String combined = key + "=" + value;
            pieces[i] = combined;
            System.out.println(combined);
            i++;
        }

        /**
         * Convert the incoming parameters into a pipe separated string and
         * echo it back
         */
        getNextStage().execute(new SuccessResponse(this.httpRequest.getSocket(),
                StringUtil.join(" | ", pieces)));
    }

}

