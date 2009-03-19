/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.exceptions;

/**
 * Used when there are problems with the incoming HTTP request
 */
public class HTTPParseException extends Exception {
	
  private static final long serialVersionUID = 1L;

  public HTTPParseException() {
    super();
  }

  public HTTPParseException(String message) {
    super(message);
  }
}