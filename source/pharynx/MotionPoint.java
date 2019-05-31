/*
 * MotionPoint.java
 *
 * Created on December 20, 2000, 2:48 PM
 */

package pharynx;
import java.util.*;

/** A (t, r) pair
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class MotionPoint implements Comparable {
    private double t = 0;
    private double r = 0;
    MotionPoint() {}
    MotionPoint(double t, double r) {
        this.t = t;
        this.r = r;
    }
    MotionPoint(MotionPoint m) {
        this.t = m.t;
        this.r = m.r;
    }
    public double t() { return(t); }
    public void t(double t) { this.t = t; }
    public double r() { return(r); }
    public void r(double r) { this.r = r; }
    
    /*
     * I think we can just inherit these from Object?
     *
    public boolean equals(Object o) {
        if (!(o instanceof MotionPoint))
            return false;
        MotionPoint m = (MotionPoint) o;
        return (m.t == t) && (m.r == r);
    }

    public int hashCode() {
        return 31*(new double(t)).hashCode() + (new double(r)).hashCode();
    }
     */

    public String toString() {return "[t = " + t + ", r = " + r + "]";}

    public int compareTo(Object o2) {
        MotionPoint m2 = (MotionPoint) o2;
        int c = (t < m2.t) ? -1 : ((t > m2.t) ? 1 : 0);
        if (0 != c) return c;
        return (r < m2.r) ? -1 : ((r > m2.r) ? 1 : 0);
    }

    public boolean equals(Object o) {
        if ((null == o) || !(o instanceof MotionPoint))
            return(false);
        MotionPoint mp = (MotionPoint) o;
        return ((t == mp.t) && (r == mp.r));
    }

}
