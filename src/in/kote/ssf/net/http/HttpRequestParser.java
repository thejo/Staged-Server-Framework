/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.net.http;

import java.io.*;
import java.util.*;

import in.kote.ssf.net.CommSocket;
import in.kote.ssf.util.StringUtil;
import in.kote.ssf.exceptions.HTTPParseException;

/**
 * An HTTP request parser. Features - <br />
 * <ul>
 *  <li>Parses GET and POST requests</li>
 *  <li>Supports multi-part form uploads</li>
 *  <li>Max. memory usage is as much as size of file in case of multi-part
 * requests. Provides access to file contents as a byte array. This
 * can be easily modified to a constant memory footprint implementation with a
 * streaming API</li>
 * </ul>
 * @author Thejo
 */
public class HttpRequestParser {
    /**
     * The Carriage Return ASCII character value.
     */
    public static final byte CR = 0x0D;


    /**
     * The Line Feed ASCII character value.
     */
    public static final byte LF = 0x0A;


    /**
     * The dash (-) ASCII character value.
     */
    public static final byte DASH = 0x2D;

    /**
     * A byte sequence that marks the end of <code>header-part</code>
     * (<code>CRLFCRLF</code>).
     */
    protected static final byte[] HEADER_SEPARATOR = {
        CR, LF, CR, LF };


    /**
     * A byte sequence that that follows a delimiter that will be
     * followed by an encapsulation (<code>CRLF</code>).
     */
    protected static final byte[] FIELD_SEPARATOR = {
        CR, LF};


    /**
     * A byte sequence that that follows a delimiter of the last
     * encapsulation in the stream (<code>--</code>).
     */
    protected static final byte[] STREAM_TERMINATOR = {
        DASH, DASH};


    /**
     * A byte sequence that precedes a boundary (<code>CRLF--</code>).
     */
    protected static final byte[] BOUNDARY_PREFIX = {
        CR, LF, DASH, DASH};


    // ---------------------- Data members -----------------------


    /**
     * The input stream from which data is read.
     */
    public final InputStream input;


    /**
     * The length of the boundary token plus the leading <code>CRLF--</code>.
     */
    private int boundaryLength;


    /**
     * The byte sequence that partitions the stream.
     */
    private byte[] boundary;


    /**
     * The length of the buffer used for processing the request.
     */
    private final int bufSize;


    /**
     * The buffer used for processing the request.
     */
    private final byte[] buffer;


    /**
     * The index of first valid character in the buffer.
     * <br>
     * 0 <= head < bufSize
     */
    private int head;


    /**
     * The index of last valid characer in the buffer + 1.
     * <br>
     * 0 <= tail <= bufSize
     */
    private int tail;

    /**
     * Tracks if the end of the incoming request has been reached
     */
    private boolean requestComplete;

    // ----------------- Request Contents -------------------

    private HttpRequest httpRequest = new HttpRequest();

    // ------------- Static strings used when parsing the request --------
    public static String GET = "GET";
    public static String POST = "POST";
    public static String BOUNDARY_KEY = "boundary";
    public static String CONTENT_LENGTH = "content-length";
    public static String CONTENT_DISPOSITION = "content-disposition";
    public static String PARAM_NAME = "name";
    public static String FILE_NAME = "filename";
    public static String CONTENT_TYPE = "content-type";
    public static final String MULTIPART = "multipart/";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";


    /**
     * Server configuration
     */
    IHttpServerConfiguration serverConfig;

    /**
     * Constructor which accepts configuration and incoming socket connection
     * 
     * @param config
     * @param socket
     * @throws java.io.IOException
     */
    public HttpRequestParser(IHttpServerConfiguration config, CommSocket socket) 
            throws IOException {
        
        this.serverConfig = config;
        this.input = socket.getSocket().getInputStream();
        this.httpRequest.setSocket(socket);

        this.bufSize = config.getDefaultBufSize();
        this.buffer = new byte[this.bufSize];
    }

    /**
     * Constructor which uses the default confiiguration
     *
     * @param input - The inputstream of the socket on which we are receiving
     * data
     */
    public HttpRequestParser(CommSocket socket) throws IOException {
        this(new DefaultHttpServerConfiguration(), socket);
    }

    /**
     * This method should be called to process the incoming HTTP request. It
     * parses the request and sets all available details
     *
     * @throws com.netcore.bulkrequest.exceptions.HTTPParseException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    public void process() throws
            HTTPParseException, UnsupportedEncodingException, IOException {

        String[] lines = getHeaders();
        String firstLine = null;
        boolean boundaryAvailable = true;

        for(String line : lines) {
            if(line.startsWith(GET)) {
                this.httpRequest.setRequestMethod(GET);
                firstLine = line;
            } else if(line.startsWith(POST)) {
                this.httpRequest.setRequestMethod(POST);
            } else if(line.toLowerCase().startsWith(CONTENT_LENGTH)) {
                this.httpRequest.setContentLength(
                        Integer.valueOf(line.split("\\s")[1]) );
            } else if(line.indexOf(MULTIPART_FORM_DATA) != -1) {
                this.httpRequest.setMultiPartRequest(true);

                if(line.indexOf(BOUNDARY_KEY + "=") != -1) {
                    String[] parts = line.split(BOUNDARY_KEY + "=");
                    if(parts.length != 2) {
                        boundaryAvailable = false;
                    } else {
                        this.boundary = parts[1].getBytes("ISO-8859-1");
                        this.boundaryLength = this.boundary.length;
                    }
                } else {
                    boundaryAvailable = false;
                }
            }
        }

        if(this.httpRequest.getRequestMethod() == null) {
            throw new HTTPParseException("Invalid HTTP request");
        }

        if(this.httpRequest.getRequestMethod().equalsIgnoreCase(GET)) {
            this.httpRequest.setRequestArgs(parseGETRequest(firstLine));
        } else if(this.httpRequest.getRequestMethod().equalsIgnoreCase(POST)
                && true == this.httpRequest.isMultiPartRequest()) {

            if(false == boundaryAvailable) {
                throw new HTTPParseException("Boundary is mandatory in a " +
                        "multipart request");
            }

            parseMultiPartRequest();

        } else if(this.httpRequest.getRequestMethod().equalsIgnoreCase(POST)) {
            this.httpRequest.setRequestArgs(parsePOSTRequest());
        } else {
            throw new HTTPParseException("Unsupported HTTP request");
        }
    }

    /**
     * Get only the HTTP headers
     *
     * @return
     * @throws com.netcore.bulkrequest.exceptions.HTTPParseException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    private String[] getHeaders()
            throws HTTPParseException, UnsupportedEncodingException, IOException {
        this.httpRequest.setHttpHeaders(readHeaders());

        if(null == this.httpRequest.getHttpHeaders()) {
            throw new HTTPParseException("No headers available");
        }

        return this.httpRequest.getHttpHeaders().split("\r\n");
    }

    /**
     * Read a section of headers. Can be used when we need to extract a string
     * upto <code>HEADER_SEPARATOR</code>
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    private String readHeaders() throws UnsupportedEncodingException, IOException {
        int i = 0;
        byte b;
        // to support multi-byte characters
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = 0;
        while (i < HEADER_SEPARATOR.length) {
            try {
                b = readByte();
            } catch (IOException e) {
                throw new IOException("Stream ended unexpectedly");
            }
            if (++size > this.serverConfig.getHeaderPartMaxSize()) {
                throw new IOException(
                        "Header section has more than " 
                        + this.serverConfig.getHeaderPartMaxSize()
                        + " bytes (maybe it is not properly terminated)");
            }
            if (b == HEADER_SEPARATOR[i]) {
                i++;
            } else {
                i = 0;
            }
            baos.write(b);
        }

        String headers = null;
        if (this.httpRequest.getHeaderEncoding() != null) {
            try {
                headers = baos.toString(this.httpRequest.getHeaderEncoding());
            } catch (UnsupportedEncodingException e) {
                // Fall back to platform default if specified encoding is not
                // supported.
                headers = baos.toString();
            }
        } else {
            headers = baos.toString();
        }

        return headers;
    }

    /**
     * Read a single byte from the inputstream
     *
     * @return The next byte
     * @throws java.io.IOException
     */
    public byte readByte() throws IOException {
        // Buffer depleted ?
        if (head == tail) {
            head = 0;
            // Refill.
            tail = input.read(buffer, head, bufSize);
            if (tail == -1) {
                // No more data available.
                throw new IOException("No more data is available");
            }
        }
        
        //System.out.print((char)(buffer[head] & 0xFF));
        return buffer[head++];
    }

    /**
     * Read and return a given number of bytes from the incoming buffer
     * 
     * @param count
     * @return
     * @throws java.io.IOException
     */
    private byte[] readBytes(int count) throws IOException {
        byte[] bytes = new byte[count];

        for(int i = 0; i < count; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }

    /**
     * Checks if we've reached the end of the multi-part request, by looking
     * at the two bytes following the boundary
     *
     * @return true if the end of the multipart request has been reached, false
     * otherwise
     * @throws java.io.IOException
     * @throws com.netcore.bulkrequest.exceptions.HTTPParseException - If the
     * request is not in the expected format
     */
    private boolean isMultiPartRequestDone() throws IOException, HTTPParseException {

        //Check if we have reached the end of request
        byte[] checkRequestEnd = new byte[2];
        checkRequestEnd[0] = readByte();
        checkRequestEnd[1] = readByte();

        if(checkRequestEnd[0] == DASH && checkRequestEnd[1] == DASH) {
            return true;
        }

        //If we are not at the end CRLF should be present
        if(checkRequestEnd[0] != CR || checkRequestEnd[1] != LF) {
            throw new HTTPParseException("Invalid multipart request");
        }
        
        return false;
    }

    /**
     * Checks if the given array of bytes is the boundary string for this
     * multi-part request
     * 
     * @param boundary
     * @return true if the array of bytes is the boundary string
     */
    private boolean isBoundary(byte[] boundary) {
        for(int i = 0; i < this.boundaryLength; i++) {
            if(boundary[i] != this.boundary[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Extracts details from a single section of a multi part request
     *
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    private void readMultiPartParameter()
            throws UnsupportedEncodingException, IOException, HTTPParseException {

        String paramName = null;
        String fileNameLocal = null;

        String elementHeaders = readHeaders();
        String[] lines = elementHeaders.split("\r\n");

        for(String line : lines) {

            if(line.toLowerCase().indexOf(CONTENT_DISPOSITION) != -1) {
                String[] paramDetails = line.split(";");
                for(String d: paramDetails) {
                    if(d.toLowerCase().trim().startsWith(PARAM_NAME)) {
                        paramName = d.trim().split("=")[1].replaceAll("\"", "");
                    }

                    if(d.toLowerCase().trim().startsWith(FILE_NAME)) {
                        String fileNameWithPath = d.trim().split("=")[1].
                                replaceAll("\"", "");
                        String[] pathElements = fileNameWithPath.split("\\\\");
                        fileNameLocal = pathElements[pathElements.length - 1];
                    }
                }
            }

            if(line.toLowerCase().indexOf(CONTENT_TYPE) != -1) {
                this.httpRequest.setFileContentType(line.split("\\s")[1].
                        toLowerCase());
            }
        }

        ByteArrayOutputStream paramValue = readMultiPartValue();

        if(fileNameLocal == null) {
            this.httpRequest.getRequestArgs().put(paramName.toLowerCase(),
                    new String(paramValue.toByteArray(), "UTF-8"));
        } else {
            this.httpRequest.setFileContents(paramValue);
            this.httpRequest.setFileName(fileNameLocal);
        }

    }

    /**
     * Read the parameter value of a single section of a multipart request
     *
     * @return
     * @throws java.io.IOException
     */
    private ByteArrayOutputStream readMultiPartValue() throws IOException,
            HTTPParseException {

        int boundaryPrefixLength = BOUNDARY_PREFIX.length;
        byte[] miniBuffer = new byte[boundaryPrefixLength];
        ByteArrayOutputStream value = new ByteArrayOutputStream();

        byte b;
        int i = 0;
        long size = 0;
        while(i < boundaryPrefixLength) {
            b = readByte();

            //Check for max. allowed POSTed field value
            if (++size > this.serverConfig.getMaxPostSize()) {
                throw new IOException("File size exceeds maximum allowed size " +
                        "of " + this.serverConfig.getMaxPostSize() + " bytes");
            }

            //Always start populating the mini buffer as soon as we see a \r (the
            //first character of a boundary prefix)
            if(b == BOUNDARY_PREFIX[0]) {
                //If this is part of series like \r\n\r or \r\n-\r, we need
                //to save the bytes we've been tracking
                if(i > 0) {
                    for(int j = 0; j < i; j++) {
                        value.write(miniBuffer[j]);
                    }
                }
                
                miniBuffer[i] = b;
                i = 1;
            }
            //Once we've caught the first \r of a boundary prefix (possibly),
            //check if the subsequent characters match the boundary prefix
            else if(b == BOUNDARY_PREFIX[i]) {
                miniBuffer[i] = b;
                i++;
            }
            //If the subsequent characters don't match the boundary prefix,
            //save the values we've been buffering and move on
            else {
                for(int j = 0; j < i; j++) {
                    value.write(miniBuffer[j]);
                }

                i = 0;
            }

            //If the byte we're looking at didn't get caught in the checks for
            //the boundary check above, just save the byte
            if(i == 0) {
                value.write(b);
            }

            //Have we encountered the boundary prefix?
            if(i == boundaryPrefixLength) {
                int currentHead = this.head;

                //If we have come across the boundary; get out of the loop
                if( isBoundary( readBytes(this.boundaryLength) ) ) {
                    //If we see the double dash after the boundary, it means the
                    //request is done
                    if( isMultiPartRequestDone() ) {
                        setRequestComplete(true);
                    }
                    break;
                } else {
                    //Continue reading from where started checking for the boundary
                    //The boundary prefix has appeared as part of the form data
                    value.write(BOUNDARY_PREFIX, 0, BOUNDARY_PREFIX.length);
                    this.head = currentHead;
                    i = 0;
                }
            }
        }

        return value;
    }

    /**
     * Parse an multi-part section of an incoming HTTP POST request
     *
     * @throws java.io.IOException
     * @throws com.netcore.bulkrequest.exceptions.HTTPParseException
     */
    private void parseMultiPartRequest() throws IOException, HTTPParseException {
        this.boundaryLength = this.boundary.length;

        //Check that the next two bytes are double dash
        if(readByte() != DASH || readByte() != DASH) {
            throw new HTTPParseException("Invalid multipart request");
        }

        //Process each section of the multipart request
        while( ! isRequestComplete() ) {
            readMultiPartParameter();
        }
    }

    /**
     * Given the first line of a GET HTTP request, parses it and returns the
     * parameters as a map
     *
     * @param req The first line of the HTTP GET request
     * @return Map<String,String> - A map of key value pairs
     */
    private static Map<String,String> parseGETRequest(String req) {

        int s = req.indexOf(" ");
        int e = req.lastIndexOf(" ");

        if (s<0 || e<0 || s==e) { return null; }

        req = req.substring(s+1, e);
        int loc = req.indexOf('?');
        if (loc < 0) {  return null;  }

        req = req.substring(loc+1);

        return parseQueryString(req);
    }

    /**
     * Given a query string, parses it and returns a map
     * @param req - GET or POST query string
     * @return
     */
    public static Map<String,String> parseQueryString(String req) {
        Map<String,String> args = new HashMap<String,String>();

        if (req.length() < 1) {  return null;  }

        String[] kvs = req.split("[&;]+");

        int loc;
        for (String kv: kvs) {
            if (kv.length() < 2) {  continue;  }
            loc = kv.indexOf('=');
            if (loc < 1) {  continue;  }
            String key = kv.substring(0, loc);
            String value = kv.substring(loc+1);
            if (key.length() < 1 || value.length() < 1) {  continue;  }

            args.put(key.toLowerCase(), StringUtil.urlDecode(value) );
        }

        return args;
    }

    /**
     * Parse the query string of a POST request and return a map
     *
     * @return Map<String,String> - A map of key value pairs
     */
    private Map<String,String> parsePOSTRequest() throws HTTPParseException {

        //We need a content length to parse POST requests
        if(this.httpRequest.getContentLength() == 0) {
            throw new HTTPParseException("Content-Length HTTP header is mandatory");
        }

        //Get the query string from the buffer
        ByteArrayOutputStream postParams = new ByteArrayOutputStream();
        byte b = 0;

        for(int i = 0; i < this.httpRequest.getContentLength(); i++) {
           try {
                b = readByte();
                postParams.write(b);
            } catch (IOException ex) {
                break;
            }
        }

        return parseQueryString(postParams.toString());
    }

    private boolean isRequestComplete() {
        return requestComplete;
    }

    private void setRequestComplete(boolean requestComplete) {
        this.requestComplete = requestComplete;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
}