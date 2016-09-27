package it.poliba.sisinflab.psw.ble;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import it.poliba.sisinflab.owl.KBManager;
import it.poliba.sisinflab.psw.ble.beacon.EddystoneBeacon;
import it.poliba.sisinflab.psw.ble.beacon.PSWUrlBeacon;
import it.poliba.sisinflab.psw.ble.utils.Utils;

public class PSWBeaconScanner {
	
	KBManager kb;	
	HashMap<String, EddystoneBeacon> beacons;
	Utils utils;
	
	public PSWBeaconScanner() throws Exception {
		utils = new Utils();
		
		long start = System.currentTimeMillis();
		kb = new KBManager(utils.getDefaultOntology());		
		long end = System.currentTimeMillis();
		System.out.println("[INFO] KB loaded in " + (end-start) + " ms" );
		
		beacons = new HashMap<String, EddystoneBeacon>();
		
		scanBeacons();
	}	
	
	public PSWBeaconScanner(File onto) throws Exception {
		utils = new Utils();
		kb = new KBManager(onto);		
		beacons = new HashMap<String, EddystoneBeacon>();
		
		scanBeacons();
	}
	
	private void scanBeacons() throws IOException {
		
		if (utils.getSimulationMode())
			this.scanSimulatedBeacons();
		else
			this.scanRealBeacons();	
	}
	
	private void scanRealBeacons() throws IOException {
		NodeScanBLE scan;
		scan = new NodeScanBLE(this);
		Thread t = new Thread(scan);
        t.start();		
	}
	
	private void scanSimulatedBeacons() throws IOException {				
		/*** Load 1st Beacon ***/
		PSWUrlBeacon b1 = new PSWUrlBeacon("DB7618A61C4E", 50, -40, 3, System.currentTimeMillis(), "http://goo.gl/dwT0yQ");
		b1.setFullUrl("http://sim.area/area/zoneA");
		
		kb.loadIndividualFromFile(b1, utils.getResourceDocument("area/ZoneA.owl"));
		beacons.put(b1.getID(), b1);
		
		/*** Load 2nd Beacon ***/
		PSWUrlBeacon b2 = new PSWUrlBeacon("7E975BB7D4C8", 50, -40, 3, System.currentTimeMillis(), "http://swot/zoneb");
		b2.setFullUrl("http://sim.area/area/zoneB");
		
		kb.loadIndividualFromFile(b2, utils.getResourceDocument("area/ZoneB.owl"));
		beacons.put(b2.getID(), b2);
		
		/*** Load 3rd Beacon ***/
		PSWUrlBeacon b3 = new PSWUrlBeacon("92C86BE67D69", 50, -40, 3, System.currentTimeMillis(), "http://swot/zonec");
		b3.setFullUrl("http://sim.area/area/zoneC");
		
		kb.loadIndividualFromFile(b3, utils.getResourceDocument("area/ZoneC.owl"));	
		beacons.put(b3.getID(), b3);				
	}
	
	public HashMap<String, EddystoneBeacon> getBeacons() {
		return beacons;
	}
	
	public HashMap<String, EddystoneBeacon> getRankedBeacons(InputStream request) {		
		kb.loadRequestFromFile(request);		
		for(EddystoneBeacon b : beacons.values()) {
			if (b instanceof PSWUrlBeacon) {
				double rank = kb.getSemanticRank(((PSWUrlBeacon) b).getAnnotationIRI());
				((PSWUrlBeacon) b).setSemanticRank(rank);
			}
		}		
		return beacons;
	}
	
	public EddystoneBeacon getBestBeacon(InputStream request){
		ArrayList<EddystoneBeacon> rBeacons = new ArrayList<EddystoneBeacon>();
		rBeacons.addAll(this.getRankedBeacons(request).values());
		
		EddystoneBeacon best = null;
		double max = 0;
		
		for (EddystoneBeacon b : rBeacons) {
			if (b.getRank() > max) {
				best = b;
				max = b.getRank();
			}
		}
		
		return best;
	}

}
