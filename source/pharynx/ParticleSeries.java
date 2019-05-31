/*
 * ParticleSeries.java
 *
 * Created on December 25, 2000, 4:51 PM
 */

package pharynx;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class ParticleSeries extends java.lang.Object {
    public int type = BACTERIUM;
    private double diameter = 5.0;
    public Color color = new Color(200, 200, 0);
    public Shape shape;
    public double acceleration = 1.0;
    public int n = 1;
    public double startTime = 0.0;
    public double interval = 50.0;
    public double startX = 0.0;
    
    public ParticleSeries() {
        defaultShape();
    }

    public ParticleSeries(ParticleSeries ps) {
        this();
        type = ps.type;
        setDiameter(ps.diameter);
        color = ps.color;
        acceleration = ps.acceleration;
        n = ps.n;
        startTime = ps.startTime;
        interval = ps.interval;
        startX = ps.startX;
    }
    
    public void setDiameter(double d) {
        diameter = d;
        defaultShape();
    }
    
    public double getDiameter() {
        return diameter;
    }

    /**
     * Maps a String to a Particle type
     *
     * @param s     The string to be looked up
     * @return      The type, or -1 if the string doesn't match
     */
    public static int parseType(String s) {
        for(int i = 0; i < particleTypeList.length; i++) {
            if (0 == s.compareToIgnoreCase(particleTypeList[i]))
                return(i);
        }
        return -1;
    }

    public static final int BACTERIUM = 0;
    public static final int FLUID = 1;
    public static final int ACCELERATED = 2;
    public static final String[] particleTypeList = {
        "bacterium", "fluid", "accelerated"
    };
    private static final double MIN_SHAPE_DIAMETER = 6.0;

    private void defaultShape() {
        double d = Math.max(diameter, MIN_SHAPE_DIAMETER);
        shape = new Ellipse2D.Double(-d/2, -d/2, d, d);
    }

}
