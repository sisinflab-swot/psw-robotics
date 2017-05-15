package it.poliba.sisinflab.ros.turtlebot.semantic;

import java.io.InputStream;

import org.physical_web.collection.UrlDevice;

import it.poliba.sisinflab.psw.ble.PSWBeaconScanner;
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
		UrlDevice b = scanner.getBestBeacon(req);
		long end = System.currentTimeMillis();
		System.out.println("[INFO] Goal detected in " + (end-start) + " ms");
		
		if (b != null) {
			System.out.println("[GOAL] " + b.getId() + " >>> " + b.getExtraDouble(PSWBeaconScanner.RANK_KEY));
		
			// Move towards most suitable beacon
			if (b.getExtraString(PSWBeaconScanner.TYPE_KEY).equals(PSWBeaconScanner.EDDYSTONE_URL_PSW) || 
					b.getExtraString(PSWBeaconScanner.TYPE_KEY).equals(PSWBeaconScanner.EDDYSTONE_UID_PSW))
				super.moveToAbsoluteGoal(b.getExtraDouble(PSWBeaconScanner.LAT_KEY), b.getExtraDouble(PSWBeaconScanner.LON_KEY), 1);	
			
			return true;
		} else 
			System.out.println("[WARNING] No beacons found!");
		
		return false;
	}	

}