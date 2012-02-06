/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package in.kote.ssf.net.http;

/**
 *
 * @author Thejo
 */
public class DefaultHttpServerConfiguration implements IHttpServerConfiguration {

    /**
     * Default maximum length of <code>header-part</code> that will be
     * processed (10 kilobytes = 10240 bytes.).
     */
    public static final int HEADER_PART_SIZE_MAX = 10240;


    /**
     * The default length of the buffer used for processing a request.
     */
    protected static final int DEFAULT_BUFSIZE = 4096;

    /**
     * Max. size of the POST field value that we will accept
     */
    protected static final long POST_SIZE_MAX = 20 * 1024 * 1024;

    private int headerPartMaxSize;
    private int defaultBufSize;
    private long postSizeMax;

    public DefaultHttpServerConfiguration(long maxPostSize, int headerPartMaxSize,
            int defaultBufSize) {

        this.postSizeMax = maxPostSize;
        this.headerPartMaxSize = headerPartMaxSize;
        this.defaultBufSize = defaultBufSize;
    }

    public DefaultHttpServerConfiguration(long postSizeMax) {
        this(postSizeMax, HEADER_PART_SIZE_MAX, DEFAULT_BUFSIZE);
    }

    public DefaultHttpServerConfiguration() {
        this(POST_SIZE_MAX, HEADER_PART_SIZE_MAX, DEFAULT_BUFSIZE);
    }

    public int getHeaderPartMaxSize() {
        return this.headerPartMaxSize;
    }

    public int getDefaultBufSize() {
        return this.defaultBufSize;
    }

    public long getMaxPostSize() {
        return this.postSizeMax;
    }

}
