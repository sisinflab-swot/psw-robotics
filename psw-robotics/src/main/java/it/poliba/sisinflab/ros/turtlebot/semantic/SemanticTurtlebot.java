package it.poliba.sisinflab.ros.turtlebot.semantic;

import java.io.InputStream;

import it.poliba.sisinflab.psw.ble.PSWBeaconScanner;
import it.poliba.sisinflab.psw.ble.beacon.EddystoneBeacon;
import it.poliba.sisinflab.psw.ble.beacon.PSWUrlBeacon;
import it.poliba.sisinflab.ros.turtlebot.Turtlebot;

public class SemanticTurtlebot extends Turtlebot {
	
	PSWBeaconScanner scanner;
	
	public SemanticTurtlebot() {
		super();
		init();			
	}
	
	public SemanticTurtlebot(String master, String host){
		super(master, host);
		init();
	}
	
	private void init() {
		try {
			scanner = new PSWBeaconScanner();
		} catch (Exception e) {
			System.err.println("Error while Turtlebot start!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public boolean searchAndGo() {
		// Scan and Rank nearby beacons
		InputStream req = getClass().getClassLoader().getResourceAsStream("data/robot/RobotA.owl");		
		
		long start = System.currentTimeMillis();
		EddystoneBeacon b = scanner.getBestBeacon(req);
		long end = System.currentTimeMillis();
		System.out.println("[INFO] Goal detected in " + (end-start) + " ms");
		
		if (b != null) {
			System.out.println("[GOAL] " + b.getID() + " >>> " + b.getRank());
		
			// Move towards most suitable beacon
			if (b instanceof PSWUrlBeacon)
				super.moveToAbsoluteGoal(((PSWUrlBeacon) b).getLatidute(), ((PSWUrlBeacon) b).getLongitude(), 1);	
			
			return true;
		} else 
			System.out.println("[WARNING] No beacons found!");
		
		return false;
	}	

}