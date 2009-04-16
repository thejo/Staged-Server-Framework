/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package in.kote.ssf.net.http;

/**
 *
 * @author Thejo
 */
public interface IHttpServerConfiguration {

    /**
     * The maximum length of <code>header-part</code> that will be
     * processed
     *
     * @return int value in bytes
     */
    public int getHeaderPartMaxSize();

    /**
     * The length of the buffer used for processing a request.
     *
     * @return int value in bytes
     */
    public int getDefaultBufSize();

    /**
     * Max. size of the POST field value that we will accept
     *
     * @return long value in bytes
     */
    public long getMaxPostSize();

}
