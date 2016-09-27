package it.poliba.sisinflab.psw.ble.beacon;

import org.semanticweb.owlapi.model.IRI;

public class PSWUidBeacon extends UidBeacon {
	
	private double ALPHA = 0.7; // Semantic rank weight
	private double BETA = 0.3; // RSSI rank weight
	private double GAMMA = 0.1; // Preferences weight

	int pref = 0;

	IRI annotationIRI = null;
	double semRank = 0;
	
	double lat = 0;
	double lon = 0;

	public PSWUidBeacon(String id, int txPower, int rssi, double distance, long timestamp, String namespace, String instance) {
		super(id, txPower, rssi, distance, timestamp, namespace, instance);
		
		this.type = EddystoneBeacon.SEM_UID;
	}
	
	public void setSemanticRank(double rank) {
		this.semRank = rank;
	}

	public void setAnnotationIRI(IRI iri) {
		this.annotationIRI = iri;
	}

	public IRI getAnnotationIRI() {
		return this.annotationIRI;
	}
	
	public double getLatidute(){
		return lat;
	}
	
	public double getLongitude(){
		return lon;
	}
	
	public void setLatitude(double lat){
		this.lat = lat;
	}
	
	public void setLongitude(double lon){
		this.lon = lon;
	}
	
	public String getOntologyID() {
		return this.namespace.substring(0, 8);
	}
	
	public String getResourceID() {
		return this.namespace.substring(8);
	}
	
	public String getDeviceAddress() {
		return this.instance;
	}

	@Override
	public double getRank() {
		double mRank = (ALPHA * semRank) + (BETA * super.getRank());
		return mRank * (1 + GAMMA * pref);
	}

	public String toString() {
		return "[" + getTimestamp() + "] " + getType() + ": " + getAnnotationIRI() + " >>> Rank: "
				+ formatter.format(getRank());
	}

}
