/*
 * MotionList.java
 *
 * Created on December 21, 2000, 6:53 AM
 */

package pharynx;
import java.util.*;

/**
 * A List of MotionPoints
 *
 * Probably totally unnecessary
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class MotionList extends ArrayList {

    /** Creates new MotionList */
    public MotionList() { super(); }
    public MotionList(List l) { super(l); }
    public MotionList(int i) { super(i); }

    /** get a MotionPoint */
    public MotionPoint getmp(int i) {
        return (MotionPoint) get(i);
    }

    public double maxT() {
        double t = Double.NEGATIVE_INFINITY;
        for(int i = 0; i < size(); i++) {
            if (getmp(i).t() > t) t = getmp(i).t();
        }
        return t;
    }
}
