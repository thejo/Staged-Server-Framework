/*
 * Please refer to http://code.thejo.in/license/
 * for details about source code license.
 */

package in.kote.ssf.util;

import java.util.*;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Utility String handling methods
 */
public class StringUtil {
    
    public static String join(String sep, List<String> pieces) {
        if (pieces.isEmpty())  return "";
        if (pieces.size() == 1)  return pieces.get(0);

        StringBuilder buf = new StringBuilder(16*pieces.size());
        Iterator<String> i = pieces.iterator();
        buf.append(i.next());        // We already know that size > 1
        while (i.hasNext()) {
          buf.append(sep).append(i.next());
        }
        return buf.toString();
    }

    public static String joinIntegerList(String sep, List<Integer> pieces) {
        List<String> newList = new ArrayList<String>(pieces.size());
        for (Integer myInt : pieces) {
          newList.add(String.valueOf(myInt));
        }

        return join(sep, newList);
    }
  
    public static String join(String sep, String[] pieces) {
        if (pieces.length == 0)  return "";
        if (pieces.length == 1)  return pieces[0];

        StringBuilder buf = new StringBuilder(16*pieces.length);
        buf.append(pieces[0]);
        for (int i=1; i<pieces.length; i++) {
          buf.append(sep).append(pieces[i]);
        }
        return buf.toString();
    }

    public static String joinv(String sep, String ... pieces) {
        return join(sep, pieces);
    }

    public static String concat(String[] pieces) {
        if (pieces.length == 0)  return "";
        if (pieces.length == 1)  return pieces[0];

        StringBuilder buf = new StringBuilder (16*pieces.length);
        for (int i=0; i<pieces.length; i++) {
          buf.append(pieces[i]);
        }
        return buf.toString();
    }

    public static String concatv(String ... pieces)
    {
        return concat(pieces);
    }
  
    /**
    * Given a <code>HashMap</code> with String key / value pairs, convert into a 
    * query string that can be used in a URL
    * 
    * @param getArgs
    * @return A query string that can be used in a URL. Does not include the "?"
    */
    public static String convertToQueryString(Map<String, String> getArgs) {
        if(getArgs.isEmpty()) return "";

        String queryString = null;
        String[] list = new String[getArgs.size()];
        int count = 0;

        for(Map.Entry<String, String> pair : getArgs.entrySet()) {
            String key = pair.getKey();
            String value = pair.getValue();

            list[count] = urlEncode(key) + "=" + urlEncode(value);
            count++;
        }

        queryString = join("&", list);

        return queryString;
    }

    public static String urlDecode(String str) {
        try {
          return URLDecoder.decode(str, "UTF-8");
        } catch (Exception ex) {
          return "";
        }
    }
  
    public static String urlEncode(String str) {
        try {
          return URLEncoder.encode(str, "UTF-8");
        } catch (Exception ex) {
          return "";
        }
    }
    
    /**
     * Escape special characters. Used to process text before adding to 
     * generated XML
     * 
     * @param string
     * @return
     */
    public static String escape(String string) {
        if(null == string || string.length() == 0) { return ""; }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0, len = string.length(); i < len; i++) {
            char c = string.charAt(i);
            switch (c) {
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Prepare a string to be added as an element of an output CSV file.
     * A comma is appended to the end of the string in this method.
     * 
     * @param String
     * @return String which can be used as an element of a CSV output
     */
    public static String prepareCSV(String str) {
        StringBuilder csv = new StringBuilder();
        String string = (null == str) ? "" : str;

        csv.append( string.replace("\"", "\"\"") );
        if(csv.length() != 0) {
            csv.insert(0, "\"");
            csv.append("\"");
        }
        csv.append(",");

        return csv.toString();
    }

    /**
     * Returns true if any one string in the list of needles is a substring of
     * haystack. Comparison is conducted in a case insensitive manner.
     * 
     * @param haystack
     * @param needles
     * @return
     */
    public static boolean containsIgnoreCase(String haystack,
            List<String> needles) {
        for (String n : needles) {
            if(haystack.toLowerCase().contains(n.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
}