/*
 * PharynxSim1.java
 *
 * Created on December 22, 2000, 11:55 AM
 */

package pharynx;
import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

/**
 *
 * @author  leon
 * @version 
 */
public class PharynxSim1 extends javax.swing.JFrame
    implements SimOptions.Updater {
    private Cubbyhole c;
    private SimOptions options;
    private AnimThread animation;
    private SimThread simulation;
    private PharynxAnimPanel pharynxAnim;

    /** Creates new form PharynxSim1 */
    public PharynxSim1() {
        options = new SimOptions();
        initComponents();
        initPharynxAnim();
        update((SimOptions) null);
        pack();
        show();
    }

    public void start() {
        try {
            readOptions(null);
        } catch(InvalidSettingsException e) {}
        halt();
        c = new Cubbyhole();
        simulation = new SimThread(c, options);
        simulation.start();
        animation = new AnimThread(c, pharynxAnim, options);
        animation.start();
    }

    public void halt() {
        if (null != simulation) simulation.halt();
    }

    public void exit() {
        halt();
        dispose();
        try {
            System.exit(0);
        } catch(java.security.AccessControlException e) {
            // We're running as an Applet: can't exit
        }
    }

    private void initPharynxAnim() {
        pharynxAnim = new PharynxAnimPanel();
        pharynxAnim.setPreferredSize(getPharynxPanelDims());
        pharynxAnim.setBorder(new javax.swing.border.EtchedBorder());
        pharynxAnim.setForeground(java.awt.Color.white);
        pharynxAnim.setBackground(java.awt.Color.black);
        pharynxAnim.setOpaque(true);
        
        getContentPane().add(pharynxAnim, java.awt.BorderLayout.CENTER);
        
    }

    public boolean readOptions(SimOptions options) {
        if (null == options)
            this.options = settingsPanel.getOptions();
        else
            this.options = options;
        return true;
    }

    public void update(SimOptions options) {
        readOptions(options);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        optionsPanel = new javax.swing.JPanel();
        settingsPanel = new SettingsPanel(this);
        actionPanel = new javax.swing.JPanel();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        
        setTitle("Pharynx Simulation");
        setName("pharynxSimFrame");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        
        optionsPanel.setLayout(new javax.swing.BoxLayout(optionsPanel, javax.swing.BoxLayout.Y_AXIS));
        
        optionsPanel.add(settingsPanel);
        
        actionPanel.setLayout(new javax.swing.BoxLayout(actionPanel, javax.swing.BoxLayout.X_AXIS));
        
        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start(evt);
            }
        });
        
        actionPanel.add(startButton);
        
        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop(evt);
            }
        });
        
        actionPanel.add(stopButton);
        
        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit(evt);
            }
        });
        
        actionPanel.add(exitButton);
        
        optionsPanel.add(actionPanel);
        
        getContentPane().add(optionsPanel, java.awt.BorderLayout.SOUTH);
        
    }//GEN-END:initComponents

  private void exit(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit
        exit();
  }//GEN-LAST:event_exit

  private void stop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stop
        halt();
  }//GEN-LAST:event_stop

  private void start(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start
        start();
  }//GEN-LAST:event_start

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        exit();
    }//GEN-LAST:event_exitForm

    public static Dimension getPharynxPanelDims() {
        LumenDims lds = new LumenDims();
        int l = lds.length();
        int w = (int) Math.ceil(lds.width());
        return new Dimension(l + 20, w + 20);
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
        new PharynxSim1();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel optionsPanel;
    private pharynx.SettingsPanel settingsPanel;
    private javax.swing.JPanel actionPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JButton exitButton;
    // End of variables declaration//GEN-END:variables

}
