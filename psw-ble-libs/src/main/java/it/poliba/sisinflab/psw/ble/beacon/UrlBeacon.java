package it.poliba.sisinflab.psw.ble.beacon;

public class UrlBeacon extends EddystoneBeacon {
	
	String fullUrl;
	String shortUrl;
	
	public UrlBeacon(String id, int txPower, int rssi, double distance, long timestamp, String shortUrl) {
		super(id, txPower, rssi, distance, timestamp);
		
		this.fullUrl = shortUrl;
		this.shortUrl = shortUrl;
		this.type = EddystoneBeacon.URL;	
	}

	public String getShortUrl(){
		return shortUrl;
	}
	
	public String getFullUrl(){
		return fullUrl;
	}
	
	public void setFullUrl(String url){
		this.fullUrl = url;
	}
	
	public String toString(){
		return "[" + getTimestamp() + "] " + getType() + ": " + getFullUrl() + " >>> Rank: " + formatter.format(getRank());
	}
}
