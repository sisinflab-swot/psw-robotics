package it.poliba.sisinflab.psw.ble.beacon;

public class UidBeacon extends EddystoneBeacon {
	
	String namespace;
	String instance;
	
	public UidBeacon(String id, int txPower, int rssi, double distance, long timestamp, String namespace, String instance) {
		super(id, txPower, rssi, distance, timestamp);

		this.namespace = namespace;
		this.instance = instance;
		super.type = EddystoneBeacon.UID;	
	}
	
	public String getNamespace() {
		return namespace;
	}
	
	public String getInstance() {
		return instance;
	}

	public String toString(){
		return "[" + getTimestamp() + "] " + getType() + ": " + getNamespace() + "--" + getInstance() + " >>> Rank: " + formatter.format(getRank());
	}
}

