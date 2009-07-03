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
    private IHttpServerConfiguration serverConfig;

    public HttpRequestHandler(IHttpServerConfiguration config, CommSocket socket) {
        this.serverConfig = config;
        this.socket = socket;
    }

    public HttpRequestHandler(CommSocket socket) {
        this(new DefaultHttpServerConfiguration(), socket);
    }

    public HttpRequest process() throws Exception {
        HttpRequestParser httpParser = null;
        
        try {
            httpParser = new HttpRequestParser(this.serverConfig, this.socket);
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

        if(! socket.getSocket().isClosed() ) {
            PrintWriter out = null;
            
            try {
                out = new PrintWriter(socket.getSocket().getOutputStream(), true);

                out.print("HTTP/1.1 200 OK\r\nContent-Type: " +
                        contentType + "; charset=utf-8" + "\r\n\r\n");
                out.println(response);

            } catch (IOException ioe) {
                throw new IOException(ioe);

            } finally {
                out.close();
                socket.getSocket().close();
            }
        }
    }
}
