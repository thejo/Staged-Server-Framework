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
public class HttpRequestError implements Runnable, IResponse {

    private CommSocket socket;
    private String errorMessage;

    public HttpRequestError(CommSocket socket, String errorMessage) {
        this.socket = socket;
        this.errorMessage = errorMessage;
    }

    public void run() {
        try {
            HttpRequestHandler.writeResponse(socket, response());
        } catch (IOException ex) {
            //Log error
        }
    }

    public String response() {
        return this.errorMessage;
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

    public String toHTML() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
