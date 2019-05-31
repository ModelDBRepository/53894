/*
 * CantHappenException.java
 *
 * Created on December 21, 2000, 1:30 PM
 */

package pharynx;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class CantHappenException extends java.lang.Error {

    /**
 * Creates new <code>CantHappenException</code> without detail message.
     */
    public CantHappenException() {
    }


    /**
 * Constructs an <code>CantHappenException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CantHappenException(String msg) {
        super(msg);
    }
}


