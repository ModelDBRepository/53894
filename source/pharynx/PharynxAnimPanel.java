/*
 * PharynxAnimPanel.java
 *
 * Created on December 22, 2000, 3:26 PM
 */

package pharynx;
import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class PharynxAnimPanel extends JPanel {
    private SimOptions options;
    private PictureData pd;
    private Snapper snapper = new Snapper(this);
    
    /** Creates new PharynxAnimPanel */
    public PharynxAnimPanel() {
        super();
        setOpaque(true);
        setBackground(Color.black);
        options = new SimOptions();
    }
    
    public void display(SimOptions options, PictureData pd) {
        this.options = new SimOptions(options);
        this.pd = new PictureData(pd);
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        snapper.paint(g,
            getSize().width,
            getSize().height,
            pd, options
        );
    }
    
}
