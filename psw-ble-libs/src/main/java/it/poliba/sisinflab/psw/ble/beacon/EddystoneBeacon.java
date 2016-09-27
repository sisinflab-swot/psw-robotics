package it.poliba.sisinflab.psw.ble.beacon;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class EddystoneBeacon {
	
	public final static String UID = "uid";
	public final static String URL = "url";
	public final static String SEM_UID = "s-uid";
	public final static String SEM_URL = "s-url";
	
	String type;
	int txPower;
	String id;
	int rssi;
	double distance;
	long timestamp;
	
	NumberFormat formatter = new DecimalFormat("#0.000");  
	
	public EddystoneBeacon(String id, int txPower, int rssi, double distance, long timestamp) {
		this.id = id;
		this.txPower = txPower;
		this.rssi = rssi;
		this.distance = distance;
		this.timestamp = timestamp;
	}
	
	public String getID() {
		return id;
	}
	
	public int getTxPower() {
		return txPower;
	}
	
	public int getRSSI() {
		return rssi;
	}
	
	public double getDistance() {
		return distance;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String getType() {
		return type;
	}
	
	public String getKey(){
		return id + ":" + type;
	}
	
	public double getRank(){
		return 1.0 + (26.0 + rssi)/74.0;
	}		

}