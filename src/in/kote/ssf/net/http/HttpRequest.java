/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net.http;

import java.io.ByteArrayOutputStream;
import java.util.*;

import in.kote.ssf.net.CommSocket;

/**
 * This class represents an incoming HTTP request
 * 
 * @author Thejo
 */
public class HttpRequest {

    /**
     * The socket on which we are communication with the client
     */
    private CommSocket socket;

    /**
     * The HTTP headers as a string
     */
    private String httpHeaders;

    /**
     * GET or POST request arguments
     */
    private Map<String,String> requestArgs = new HashMap<String,String>();

    /**
     * Request method
     */
    private String requestMethod;

    /**
     * Content length
     */

    private int contentLength;

    /**
     * To check if it is a multi-part request
     */
    private boolean multiPartRequest;

    /**
     * The contents of the file if it is a multipart form upload
     */
    private ByteArrayOutputStream fileContents;

    /**
     * Name of the file (if available)
     */
    private String fileName;

    /**
     * Content type of the uploaded file (if any)
     */
    private String fileContentType;

    /**
     * The content encoding to use when reading headers.
     */
    private String headerEncoding;
    

    public CommSocket getSocket() {
        return socket;
    }

    public void setSocket(CommSocket socket) {
        this.socket = socket;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public ByteArrayOutputStream getFileContents() {
        return fileContents;
    }

    public void setFileContents(ByteArrayOutputStream fileContents) {
        this.fileContents = fileContents;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHeaderEncoding() {
        return headerEncoding;
    }

    public void setHeaderEncoding(String headerEncoding) {
        this.headerEncoding = headerEncoding;
    }

    public String getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(String httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public boolean isMultiPartRequest() {
        return multiPartRequest;
    }

    public void setMultiPartRequest(boolean multiPartRequest) {
        this.multiPartRequest = multiPartRequest;
    }

    public Map<String, String> getRequestArgs() {
        return requestArgs;
    }

    public void setRequestArgs(Map<String, String> requestArgs) {
        this.requestArgs = requestArgs;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
    
}
