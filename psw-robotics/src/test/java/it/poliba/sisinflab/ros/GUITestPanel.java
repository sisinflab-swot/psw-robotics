package it.poliba.sisinflab.ros;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

//import it.poliba.sisinflab.ros.turtlebot.Turtlebot;
import it.poliba.sisinflab.ros.turtlebot.TurtlebotConstant;
import it.poliba.sisinflab.ros.turtlebot.semantic.SemanticTurtlebot;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class GUITestPanel extends JFrame {

	String defaultText = "N/A";
    GridLayout experimentLayout = new GridLayout(0,3);    
    static SemanticTurtlebot bot;
    
    static JTextField xValue = new JTextField();
    static JTextField yValue = new JTextField();
    static JTextField wValue = new JTextField();
    
    static JTextField bumpTf = new JTextField();
    static JTextField bumpSensTf = new JTextField();
    static JTextField cliffTf = new JTextField();
    static JTextField cliffSensTf = new JTextField();
    static JTextField cliffBottomTf = new JTextField();
    
    public GUITestPanel(String name) {
        super(name);
        setResizable(false);
    }        
    
    public void addComponentsToPane(final Container pane) {
        final JPanel cmdPanel = new JPanel();
        cmdPanel.setLayout(experimentLayout);
        
        JPanel goalCmd = new JPanel();
        goalCmd.setLayout(new GridLayout(1,8));
        
        JPanel sensors = new JPanel();
        sensors.setLayout(new GridLayout(6,2));
        
        //Set up components preferred size
        JButton b = new JButton("Just fake button");
        Dimension buttonSize = b.getPreferredSize();
        cmdPanel.setPreferredSize(new Dimension((int)(buttonSize.getWidth() * 2.5)+20,
               (int)(buttonSize.getHeight() * 3.5)+20 * 2));
        
        //Add command buttons to the Grid Layout
        JButton leftUp = new JButton("\\");
        leftUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnLeftUp();
            } 
         });
        cmdPanel.add(leftUp);
        
        JButton up = new JButton("Forward");
        up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.goForward();
            } 
         });
        cmdPanel.add(up);
        
        JButton rightUp = new JButton("/");
        rightUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnRightUp();
            } 
         });
        cmdPanel.add(rightUp);
        
        JButton left = new JButton("Left");
        left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnAroundLeft();
            } 
         });
        cmdPanel.add(left);
        
        JButton stop = new JButton("Stop");
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.stop();
            } 
         });
        cmdPanel.add(stop);
        
        JButton right = new JButton("Right");
        right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnAroundRight();
            } 
         });
        cmdPanel.add(right);
        
        JButton leftDown = new JButton("/");
        leftDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnLeftDown();
            } 
         });
        cmdPanel.add(leftDown);
        
        JButton down = new JButton("Backward");
        down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.goBackward();
            } 
         });
        cmdPanel.add(down);
        
        JButton rightDown = new JButton("\\");
        rightDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.turnRightDown();
            } 
         });
        cmdPanel.add(rightDown);
        
        JButton scanBLE = new JButton("Scan BLE");
        scanBLE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                bot.searchAndGo();
            } 
         });
        cmdPanel.add(scanBLE);
        
        //Add controls to send goal commands
        goalCmd.add(new Label("ABS"));        
        
        xValue.setText("0");
        goalCmd.add(new Label("X: "));
        goalCmd.add(xValue);
        
        yValue.setText("0");
        goalCmd.add(new Label("Y: "));
        goalCmd.add(yValue);
        
        wValue.setText("0");
        goalCmd.add(new Label("W: "));
        goalCmd.add(wValue);
        
        JButton absGoal = new JButton("Goal!");
        absGoal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
            	try {
	            	double x = Double.parseDouble(xValue.getText());
	            	double y = Double.parseDouble(yValue.getText());
	            	double w = Double.parseDouble(wValue.getText());
	                bot.moveToAbsoluteGoal(x, y, w);
            	} catch (Exception e) {
            		System.err.println("Error in absolute position value!");
            	}
            } 
         });
        goalCmd.add(absGoal);
        
        //Add controls to view sensor data
        sensors.add(new Label("Sensor Data"));
        sensors.add(new Label(" "));        
        
        bumpTf.setText(defaultText);
        bumpTf.setEditable(false);        
        sensors.add(new Label("Bumper State: "));
        sensors.add(bumpTf);
                
        bumpSensTf.setText(defaultText);
        bumpSensTf.setEditable(false);        
        sensors.add(new Label("Bumper Sensor: "));
        sensors.add(bumpSensTf);      
        
        cliffTf.setText(defaultText);
        cliffTf.setEditable(false);        
        sensors.add(new Label("Cliff State: "));
        sensors.add(cliffTf);
                
        cliffSensTf.setText(defaultText);
        cliffSensTf.setEditable(false);        
        sensors.add(new Label("Cliff Sensor: "));
        sensors.add(cliffSensTf); 
        
        cliffBottomTf.setText(defaultText);
        cliffBottomTf.setEditable(false);        
        sensors.add(new Label("Cliff Bottom: "));
        sensors.add(cliffBottomTf);

        pane.add(cmdPanel, BorderLayout.NORTH);
        pane.add(goalCmd, BorderLayout.CENTER);
        pane.add(sensors, BorderLayout.SOUTH);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        GUITestPanel frame = new GUITestPanel("Test Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();                
            }
        });
        
        bot = new SemanticTurtlebot();        
        new SensorThread().run();
    }
    
    static class SensorThread extends Thread {     
    	public void run() {
    		while(true) {
    			bumpSensTf.setText(TurtlebotConstant.toSensorEventString(bot.getBumper()).toUpperCase());
    			bumpTf.setText(TurtlebotConstant.toBumperStateString(bot.getBumperState()).toUpperCase());
    			
    			cliffSensTf.setText(TurtlebotConstant.toSensorEventString(bot.getCliffSensor()).toUpperCase());
    			cliffTf.setText(TurtlebotConstant.toCliffStateString(bot.getCliffState()).toUpperCase());
    			cliffBottomTf.setText(String.valueOf(bot.getCliffBottom()));
    			
    			try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
}