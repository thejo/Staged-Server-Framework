/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.example.net.response;

import java.io.IOException;

import in.kote.ssf.net.CommSocket;
import in.kote.ssf.net.response.IResponse;
import in.kote.ssf.net.http.HttpRequestHandler;

/**
 *
 * @author Thejo
 */
public class SuccessResponse implements Runnable, IResponse {
    private CommSocket socket;
    private String successMessage;

    public SuccessResponse(CommSocket socket, String errorMessage) {
        this.socket = socket;
        this.successMessage = errorMessage;
    }

    public void run() {
        try {
            HttpRequestHandler.writeResponse(socket, response());
        } catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    public String response() {
        return this.successMessage;
    }

    public String toHTML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toXML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toJSON() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toCSV() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
