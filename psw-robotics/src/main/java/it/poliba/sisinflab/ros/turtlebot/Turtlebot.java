package it.poliba.sisinflab.ros.turtlebot;

import java.net.URI;

import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.time.WallTimeProvider;

import com.google.common.base.Preconditions;

public class Turtlebot {
	
	/*** Turtlebot Deafult Topics ***/
	
	public final static String BUMPER = "/mobile_base/events/bumper";
	public final static String CLIFF = "/mobile_base/events/cliff";
	public final static String SENSOR_STATE = "/mobile_base/sensors/core";
	public final static String INPUT_NAVI = "/cmd_vel_mux/input/navi";
	
	/*** Default Round Values ***/
	final static double LINEAR_X = 0.2662;
	final static double ANGULAR_Z = 1.331;
	
	TurtlebotListener tbl;
	TurtlebotTalker tbt;
	
	public Turtlebot(){
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();				
		NodeConfiguration config = NodeConfiguration.newPrivate();
		
		tbl = new TurtlebotListener();
		nodeMainExecutor.execute(tbl, config);
		
		tbt = new TurtlebotTalker();
		nodeMainExecutor.execute(tbt, config);
	}
	
	public Turtlebot(String master, String host){
		NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();		
		
		URI masterUri = URI.create(master);
	    NodeConfiguration config = NodeConfiguration.newPublic(host, masterUri);
	    config.setTimeProvider(new WallTimeProvider());
		
		tbl = new TurtlebotListener();		
		Preconditions.checkState(tbl != null);
		nodeMainExecutor.execute(tbl, config);
		
		tbt = new TurtlebotTalker();
		Preconditions.checkState(tbt != null);
		nodeMainExecutor.execute(tbt, config);
	}	
	
	public void moveToAbsoluteGoal(double x, double y, double w) {
		tbt.moveToAbsoluteGoal(x, y, w);
	}
	
	public void goForward() {
		//let's go forward at 1.0 m/s
		goLinear(1.0);
	}		
	
	public void goBackward() {
		//let's go backward at 1.0 m/s
		goLinear(-1.0);
	}
	
	public void goLinear(double speed) {
		//let's go linear (forward or backward) at <speed> m/s
		tbt.moveRobot(speed, 0, 0, 0, 0, 0);
	}
	
	public void turnAroundLeft() {
		turnRobot(1.0);
	}
	
	public void turnAroundRight() {
		turnRobot(-1.0);
	}
	
	public void turnRightUp() {
		tbt.moveRobot(LINEAR_X, 0, 0, 0, 0, -ANGULAR_Z);
	}
	
	public void turnRightDown() {
		tbt.moveRobot(-LINEAR_X, 0, 0, 0, 0, ANGULAR_Z);
	}
	
	public void turnLeftUp() {
		tbt.moveRobot(LINEAR_X, 0, 0, 0, 0, ANGULAR_Z);
	}
	
	public void turnLeftDown() {
		tbt.moveRobot(-LINEAR_X, 0, 0, 0, 0, -ANGULAR_Z);
	}
	
	public void turnRobot(double speed) {
		tbt.moveRobot(0, 0, 0, 0, 0, speed);
	}
	
	public void stop() {
		//stop the turtlebot
		tbt.moveRobot(0, 0, 0, 0, 0, 0);
	}
	
	// bumper sensor
	public byte getBumper(){
		if (tbl.getBumperEvent() != null)
			return tbl.getBumperEvent().getBumper();
		else 
			return 0;
	}
	
	// bumper sensor state
	public byte getBumperState(){
		if (tbl.getBumperEvent() != null)
			return tbl.getBumperEvent().getState();
		else 
			return 0;
	}
	
	// cliff sensor
	public byte getCliffSensor(){
		if (tbl.getCliffEvent() != null)
			return tbl.getCliffEvent().getSensor();
		else 
			return 0;
	}
	
	// cliff sensor state
	public byte getCliffState(){
		if (tbl.getCliffEvent() != null)
			return tbl.getCliffEvent().getState();
		else 
			return 0;
	}
	
	// distance to floor when cliff was detected
	public short getCliffBottom(){
		if (tbl.getCliffEvent() != null)
			return tbl.getCliffEvent().getBottom();
		else 
			return 0;
	}
}
