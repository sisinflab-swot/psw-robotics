package it.poliba.sisinflab.ros.turtlebot;

import kobuki_msgs.BumperEvent;
import kobuki_msgs.CliffEvent;

public class TurtlebotConstant {
	
	private static final String UNKNOWN = "N/A";

	private static final String LEFT = "Left";
	private static final String RIGHT = "Right";
	private static final String CENTER = "Center";

	private static final String PRESSED = "Pressed";
	private static final String RELEASED = "Released";

	private static final String FLOOR = "Floor";
	private static final String CLIFF = "Cliff";

	public static String toSensorEventString(byte value) {
		switch (value) {
		case BumperEvent.CENTER:
			return CENTER;
		case BumperEvent.LEFT:
			return LEFT;
		case BumperEvent.RIGHT:
			return RIGHT;
		default:
			return UNKNOWN;
		}
	}
	
	public static String toBumperStateString(byte value) {
		switch (value) {
		case BumperEvent.PRESSED:
			return PRESSED;
		case BumperEvent.RELEASED:
			return RELEASED;
		default:
			return UNKNOWN;
		}
	}
	
	public static String toCliffStateString(byte value) {
		switch (value) {
		case CliffEvent.FLOOR:
			return FLOOR;
		case CliffEvent.CLIFF:
			return CLIFF;
		default:
			return UNKNOWN;
		}
	}

}
