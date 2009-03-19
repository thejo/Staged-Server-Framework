/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net.http;

import java.io.IOException;
import java.io.PrintWriter;

import in.kote.ssf.net.CommSocket;

/**
 * HTTP request handling
 * 
 * @author Thejo
 */
public class HttpRequestHandler {
    private CommSocket socket;

    public HttpRequestHandler(CommSocket socket) {
        this.socket = socket;
    }

    public HttpRequest process() throws Exception {
        HttpRequestParser httpParser = null;
        
        try {
            httpParser = new HttpRequestParser(this.socket);
            httpParser.process();
        } catch (Exception ex) {
            throw new Exception(ex);
        }

        return httpParser.getHttpRequest();
    }

    public static void writeResponse (CommSocket socket, String response)
            throws IOException {

        writeResponse(socket, response, HttpMimeType.TEXT_PLAIN);
    }

    public static void writeResponse (CommSocket socket, String response,
            String contentType) throws IOException {

        try {
            PrintWriter out = null;
            out = new PrintWriter(socket.getSocket().getOutputStream(), true);

            out.print("HTTP/1.1 200 OK\r\nContent-Type: " +
                    contentType + "\r\n\r\n");
            out.println(response);

        } catch (IOException ioe) {
            throw new IOException(ioe);

        } finally {
            if(! socket.getSocket().isClosed() )  try {
                socket.getSocket().close();
            } catch (IOException ex) { }
        }
    }

}
