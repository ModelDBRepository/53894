/*
 * PictureData.java
 *
 * Created on December 22, 2000, 9:31 AM
 */

package pharynx;
import java.util.*;
import java.awt.*;

/**
 * Contains data needed to prepare a picture of the pharynx
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class PictureData extends java.lang.Object {
    public double t;
    public int length;
    public int width;
    public double[] thickness;
    public double[] maxDiameter;
    public double[] currDiameter;
    public java.util.List things;

    /** Creates new PictureData */
    public PictureData() {
        things = new ArrayList();
    }

    /** creates a clone of PictureData */
    public PictureData(PictureData pd) {
        t = pd.t;
        length = pd.length;
        width = pd.width;
        thickness = cloneArray(pd.thickness);
        maxDiameter = cloneArray(pd.maxDiameter);
        currDiameter = cloneArray(pd.currDiameter);
        things = new ArrayList(pd.things);
    }

    public class Thing {
        public Color color;
        public double x;

        private Shape shape;
        private double bbx;             // the bounding box
        private double bby;
        private double bbh;
        private double bbw;

        public Shape shape() { return shape; }
        public void shape(Shape s) {
            shape = s;
            bbx = s.getBounds2D().getMinX();
            bby = s.getBounds2D().getMinY();
            bbh = s.getBounds2D().getHeight();
            bbw = s.getBounds2D().getWidth();
        }
        public double bbx() { return bbx; }
        public double bby() { return bby; }
        public double bbh() { return bbh; }
        public double bbw() { return bbw; }
    }

    static private double[] cloneArray(double[] a) {
        double[] b = new double[a.length];
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

}
