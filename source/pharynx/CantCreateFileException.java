/*
 * CantCreateFileException.java
 *
 * Created on November 4, 2002, 8:51 AM
 */

package pharynx;
import java.lang.*;

/**
 *
 * @author  leon
 */
public class CantCreateFileException extends java.lang.RuntimeException {
    
    /**
     * Creates a new instance of <code>CantCreateFileException</code> without detail message.
     */
    public CantCreateFileException() {
    }
    
    
    /**
     * Constructs an instance of <code>CantCreateFileException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CantCreateFileException(String msg) {
        super(msg);
    }
}
