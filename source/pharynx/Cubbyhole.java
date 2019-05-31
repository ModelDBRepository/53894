/*
 * Cubbyhole.java
 *
 * Created on December 22, 2000, 11:17 AM
 */

package pharynx;

/**
 *
 * @author  leon
 * @version 
 */
public class Cubbyhole extends java.lang.Object {
    private boolean running = true;
    private boolean available = false;
    private Object contents;

    /** Creates new Cubbyhole */
    public Cubbyhole() {}
    
    public synchronized Object get() {
        while(running && !available) {
            try {
                // wait for Producer to put value
                wait();
            } catch (InterruptedException e) {}
        }
        available = false;
        // notify Producer that value has been retrieved
        notifyAll();
        Object c = contents;
        contents = null;
        return c;
    }

    public synchronized void put(Object value) {
        while(available) {
            try {
                // wait for Consumer to get value
                wait();
            } catch (InterruptedException e) {}
        }
        contents = value;
        available = true;
        // notify Consumer that value has been set
        notifyAll();
    }

    public synchronized void halt() {
        while(available) {
            try {
                // wait for Consumer to get value
                wait();
            } catch (InterruptedException e) {}
        }
        if (null != contents)
            throw new CantHappenException("Cubbyhole: can't happen");
        running = false;
        // notify Consumer that we're done
        notifyAll();
    }
    
    public synchronized boolean isRunning() {
        return running;
    }

}
