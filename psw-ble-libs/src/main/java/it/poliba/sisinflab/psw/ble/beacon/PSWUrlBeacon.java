package it.poliba.sisinflab.psw.ble.beacon;

import org.semanticweb.owlapi.model.IRI;

public class PSWUrlBeacon extends UrlBeacon {

	private double ALPHA = 0.7; // Semantic rank weight
	private double BETA = 0.3; // RSSI rank weight
	private double GAMMA = 0.1; // Preferences weight

	int pref = 0;

	IRI annotationIRI = null;
	double semRank = 0;
	
	double lat = 0;
	double lon = 0;

	public PSWUrlBeacon(String id, int txPower, int rssi, double distance, long timestamp, String shortUrl) {
		super(id, txPower, rssi, distance, timestamp, shortUrl);

		this.type = EddystoneBeacon.SEM_URL;
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
