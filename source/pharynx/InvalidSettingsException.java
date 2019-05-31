/*
 * InvalidSettingsException.java
 *
 * Created on December 31, 2000, 10:21 AM
 */

package pharynx;
import java.lang.*;

/**
 *
 * @author  leon
 * @version 
 */
public class InvalidSettingsException extends java.lang.RuntimeException {

    /**
 * Creates new <code>InvalidSettingsException</code> without detail message.
     */
    public InvalidSettingsException() {
    }


    /**
 * Constructs an <code>InvalidSettingsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidSettingsException(String msg) {
        super(msg);
    }
}


