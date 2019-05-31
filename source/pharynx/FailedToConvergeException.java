/*
 * FailedToConvergeException.java
 *
 * Created on December 23, 2000, 9:57 AM
 */

package pharynx;

/**
 * Thrown when iterative solution fails to converge
 *
 * @author  leon@eatworms/swmed.edu
 * @version 0.1
 */
public class FailedToConvergeException extends java.lang.RuntimeException {

    /**
 * Creates new <code>FailedToConvergeException</code> without detail message.
     */
    public FailedToConvergeException() {
    }


    /**
 * Constructs an <code>FailedToConvergeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public FailedToConvergeException(String msg) {
        super(msg);
    }
}


