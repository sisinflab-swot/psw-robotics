package it.poliba.sisinflab.psw.ble;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.physical_web.collection.UrlDevice;
import org.semanticweb.owlapi.model.IRI;

import it.poliba.sisinflab.owl.KBManager;
import it.poliba.sisinflab.psw.PswDevice;
import it.poliba.sisinflab.psw.ble.utils.Utils;

public class PSWBeaconScanner {
	
	public static final String EDDYSTONE_URL = "url";
	public static final String EDDYSTONE_URL_PSW = "url-psw";
	public static final String EDDYSTONE_UID_PSW = "uid-psw";
	
	public static final String TYPE_KEY = "type";
	public static final String PUBLIC_KEY = "public";
	public static final String TITLE_KEY = "title";
	public static final String DESCRIPTION_KEY = "description";
	public static final String RSSI_KEY = "rssi";
	public static final String TXPOWER_KEY = "tx";
	public static final String SCANTIME_KEY = "scantime";
	public static final String DISTANCE_KEY = "distance";
	public static final String RANK_KEY = "rank";
	public static final String SITEURL_KEY = "siteurl";
	
	public static final String LAT_KEY = "lat";
	public static final String LON_KEY = "lon";
	
	KBManager kb;	
	HashMap<String, UrlDevice> beacons;
	Utils utils;
	
	public PSWBeaconScanner() throws Exception {
		utils = new Utils();
		
		long start = System.currentTimeMillis();
		kb = new KBManager(utils.getDefaultOntology());		
		long end = System.currentTimeMillis();
		System.out.println("[INFO] KB loaded in " + (end-start) + " ms" );
		
		beacons = new HashMap<String, UrlDevice>();
		
		scanBeacons();
	}	
	
	public PSWBeaconScanner(File onto) throws Exception {
		utils = new Utils();
		kb = new KBManager(onto);		
		beacons = new HashMap<String, UrlDevice>();
		
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
		UrlDevice b1 = new UrlDevice.Builder("DB7618A61C4E", "http://goo.gl/dwT0yQ")
				.addExtra(TXPOWER_KEY, 50)
				.addExtra(RSSI_KEY, -40)
				.addExtra(DISTANCE_KEY, 3)
				.addExtra(SCANTIME_KEY, System.currentTimeMillis())
				.addExtra(SITEURL_KEY, "http://sim.area/area/zoneA").build();
		
		kb.loadIndividualFromFile(b1, utils.getResourceDocument("area/ZoneA.owl"));
		beacons.put(b1.getId(), b1);
		
		/*** Load 2nd Beacon ***/
		UrlDevice b2 = new UrlDevice.Builder("7E975BB7D4C8", "http://swot/zoneb")
				.addExtra(TXPOWER_KEY, 50)
				.addExtra(RSSI_KEY, -40)
				.addExtra(DISTANCE_KEY, 3)
				.addExtra(SCANTIME_KEY, System.currentTimeMillis())
				.addExtra(SITEURL_KEY, "http://sim.area/area/zoneB").build();
		
		kb.loadIndividualFromFile(b2, utils.getResourceDocument("area/ZoneB.owl"));
		beacons.put(b2.getId(), b2);
		
		/*** Load 3rd Beacon ***/
		UrlDevice b3 = new UrlDevice.Builder("92C86BE67D69", "http://swot/zonec")
				.addExtra(TXPOWER_KEY, 50)
				.addExtra(RSSI_KEY, -40)
				.addExtra(DISTANCE_KEY, 3)
				.addExtra(SCANTIME_KEY, System.currentTimeMillis())
				.addExtra(SITEURL_KEY, "http://sim.area/area/zoneC").build();
		
		kb.loadIndividualFromFile(b3, utils.getResourceDocument("area/ZoneC.owl"));	
		beacons.put(b3.getId(), b3);				
	}
	
	public HashMap<String, UrlDevice> getBeacons() {
		return beacons;
	}
	
	public HashMap<String, UrlDevice> getRankedBeacons(InputStream request) {		
		kb.loadRequestFromFile(request);		
		for(UrlDevice b : beacons.values()) {
			if (b.getExtraString(this.TYPE_KEY).equals(EDDYSTONE_URL_PSW)) {
				double rank = kb.getSemanticRank(IRI.create(b.getExtraString(PswDevice.PSW_IRI_KEY)));
				b = new UrlDevice.Builder(b).addExtra(RANK_KEY, rank).build();
			}
		}		
		return beacons;
	}
	
	public UrlDevice getBestBeacon(InputStream request){
		ArrayList<UrlDevice> rBeacons = new ArrayList<UrlDevice>();
		rBeacons.addAll(this.getRankedBeacons(request).values());
		
		UrlDevice best = null;
		double max = 0;
		
		for (UrlDevice b : rBeacons) {
			if (b.getExtraDouble(RANK_KEY) > max) {
				best = b;
				max = b.getExtraDouble(RANK_KEY);
			}
		}
		
		return best;
	}

}
