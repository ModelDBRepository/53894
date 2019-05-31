/*
 * Kickable.java
 *
 * Created on December 20, 2000, 11:29 AM
 */

package pharynx;

/**
 * A Kickable can respond to a kick
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public interface Kickable {
    /**
     * receive a Kick from the Kick handler
     *
     * @param k     the Kick
     */
    public void kick(Kick k);
}

