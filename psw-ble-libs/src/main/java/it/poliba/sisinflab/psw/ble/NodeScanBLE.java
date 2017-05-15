package it.poliba.sisinflab.psw.ble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.apache.commons.io.FileUtils;
import org.physical_web.collection.UrlDevice;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import it.poliba.sisinflab.psw.PswDevice;
import it.poliba.sisinflab.psw.PswEddystoneBeacon;
import it.poliba.sisinflab.psw.UidEddystoneBeacon;

public class NodeScanBLE implements Runnable {
	
	long MAX_AGE;
	String TMP_DIR;

	PSWBeaconScanner psw;
	BufferedReader input = null;
	
	boolean bt_busy = false;

	public NodeScanBLE(PSWBeaconScanner psw) throws IOException {
		
		this.psw = psw;
		
		MAX_AGE = psw.utils.getMaxAge();
		TMP_DIR = psw.utils.getTempFolder();
		
		Runtime rt = Runtime.getRuntime();
		String[] commands = { "sudo", "-n", "node", "basic-scan.js" };
		Process proc = rt.exec(commands);

		input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	}

	@Override
	public void run() {
		try {
			while (true) {
				// read the output from the command
				String tmp = "";
				String s = null;
				while ((s = input.readLine()) != null) {
					tmp = tmp.concat(s);

					if (s.equals("}"))
						break;
				}
				parseMessage(tmp);
			}

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void parseMessage(String msg) {
		JsonObject object = Json.parse(msg).asObject();

		String id = object.get("id").asString();
		int pow = object.get("txPower").asInt();
		int rssi = object.get("rssi").asInt();
		double dist = object.get("distance").asFloat();
		long ts = object.get("lastSeen").asLong();

		String type = object.get("type").asString();

		if (type.equals(PSWBeaconScanner.EDDYSTONE_UID_PSW)) {
			String ns = object.get("namespace").asString();
			String inst = object.get("instance").asString();
			
			UrlDevice uidBeacon = new UrlDevice.Builder(id, "Local OWL fragment @" + id)
					.addExtra(PSWBeaconScanner.TYPE_KEY, PSWBeaconScanner.EDDYSTONE_UID_PSW)
					.addExtra(PSWBeaconScanner.TXPOWER_KEY, pow)
					.addExtra(PSWBeaconScanner.RSSI_KEY, rssi)
					.addExtra(PSWBeaconScanner.DISTANCE_KEY, dist)
					.addExtra(PSWBeaconScanner.SCANTIME_KEY, ts)
					.addExtra(PswDevice.PSW_UID_INST_KEY, ns.substring(4, 10))
					.addExtra(PswDevice.PSW_UID_ONTO_KEY, ns.substring(0, 4))
					.addExtra(PswDevice.PSW_UID_MAC_KEY, inst)
					.build();

			pushUidBeacon(uidBeacon);

		} else if (type.equals(PSWBeaconScanner.EDDYSTONE_URL_PSW)) {
			String url = object.get("url").asString();
			
			UrlDevice urlBeacon = new UrlDevice.Builder(id, url)
					.addExtra(PSWBeaconScanner.TYPE_KEY, PSWBeaconScanner.EDDYSTONE_URL_PSW)
					.addExtra(PSWBeaconScanner.TXPOWER_KEY, pow)
					.addExtra(PSWBeaconScanner.RSSI_KEY, rssi)
					.addExtra(PSWBeaconScanner.DISTANCE_KEY, dist)
					.addExtra(PSWBeaconScanner.SCANTIME_KEY, ts)
					.build();
			pushUrlBeacon(urlBeacon);
		}

	}
	
	private void pushUidBeacon(UrlDevice b) {
		UrlDevice oldBeacon = (UrlDevice) psw.beacons.get(b.getId());
		if (oldBeacon == null || isOld(oldBeacon, b)) {
			new UidBeaconDownloaderThread(b).run();			
		}
	}

	private void pushUrlBeacon(UrlDevice b) {
		UrlDevice oldBeacon = (UrlDevice) psw.beacons.get(b.getId());
		if (oldBeacon == null || isOld(oldBeacon, b)) {
			// resolve short URL and download the relative file (if present)	
			new UrlBeaconDownloaderThread(b).run();					
		}
	}

	private boolean isOld(UrlDevice oldB, UrlDevice newB) {
		if ((newB.getExtraLong(PSWBeaconScanner.SCANTIME_KEY) - oldB.getExtraLong(PSWBeaconScanner.SCANTIME_KEY)) >= MAX_AGE)
			return true;
		else
			return false;
	}

	class UrlBeaconDownloaderThread extends Thread {
		UrlDevice b;

		public UrlBeaconDownloaderThread(UrlDevice b) {
			super();
			this.b = b;
		}

		public void run() {
			URL url;
			try {
				/*** Resolve short URL ***/
				long start = System.currentTimeMillis();
				url = new URL(b.getUrl());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		        connection.setInstanceFollowRedirects(false);
		        connection.connect();
		        String expandedURL = connection.getHeaderField("Location");
		        connection.getInputStream().close();
		        long end = System.currentTimeMillis();
		        System.out.println("[INFO] " + expandedURL + " resolved in " + (end-start) + "ms");
		        
		        
		        //System.out.println("[INFO] " + b.getShortUrl() + " --> " + expandedURL);		      
		        b = new UrlDevice.Builder(b).addExtra(PSWBeaconScanner.SITEURL_KEY, expandedURL).build();
		        
		        if(b.getExtraString(PSWBeaconScanner.TYPE_KEY).equals(PSWBeaconScanner.EDDYSTONE_URL_PSW)){		        	
		        	String name = "tmp-" + b.getId() + "." + PSWBeaconScanner.EDDYSTONE_URL_PSW;
		        	File tmp = new File(TMP_DIR + name);
		        	
		        	start = System.currentTimeMillis();
		        	FileUtils.copyURLToFile(new URL(expandedURL), tmp);
		        	end = System.currentTimeMillis();
		        	System.out.println("[INFO] " + expandedURL + " downloaded in " + (end-start) + "ms");
		        	
		        	/*IRI iri = psw.kb.loadIndividualFromFile(tmp);
		        	((SemUrlBeacon) b).setAnnotationIRI(iri);
		        	
		        	double sr = psw.kb.getSemanticRank(iri);
		        	((SemUrlBeacon) b).setSemanticRank(sr);*/
		        	
		        	psw.kb.loadIndividualFromFile(b, new FileInputStream(tmp));
		        	
		        	FileUtils.forceDelete(tmp);
		        }
		        
		        psw.beacons.put(b.getId(), b);        
		        
			} catch (IOException e) {
				System.err.println("[ERROR] " + b.getUrl() + " not resolved!");
				e.printStackTrace();
			}
		}

	}
	
	class UidBeaconDownloaderThread extends Thread {
		UrlDevice b;
		
		public UidBeaconDownloaderThread(UrlDevice b) {
			super();
			this.b = b;
		}
		
		public void run() {
			
			if (b.getExtraString(PSWBeaconScanner.TYPE_KEY).equals(PSWBeaconScanner.EDDYSTONE_UID_PSW)){
				
				while(bt_busy){
					try {
						this.wait(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				UrlDevice pswB = b;
				try {
					bt_busy = true;
					File tmp = getFileFromRemoteDevice(pswB.getExtraString(PswDevice.PSW_UID_MAC_KEY), 
							pswB.getExtraString(PswDevice.PSW_UID_ONTO_KEY), 
							pswB.getExtraString(PswDevice.PSW_UID_INST_KEY));										
					psw.kb.loadIndividualFromFile(pswB, new FileInputStream(tmp));	
					bt_busy = false;
					
				} catch (IOException e) {
					System.err.println("[ERROR] Error during communication with [" + pswB.getExtraString(PswDevice.PSW_UID_MAC_KEY) + "] device");
					e.printStackTrace();
				}
			}
			
			psw.beacons.put(b.getId(), b);	
		}
		
		private File getFileFromRemoteDevice(String address, String ontoID, String instanceID) throws IOException {
			
			long start = System.currentTimeMillis();

			// display local device address and name
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			System.out.println("Address: " + localDevice.getBluetoothAddress());
			System.out.println("Name: " + localDevice.getFriendlyName());
				
			String connectionURL = "btspp://" + address + ":8;authenticate=false;encrypt=false;master=false";						
			
			// connect to the server and send a line of text
			StreamConnection streamConnection = (StreamConnection) Connector.open(connectionURL);
			
			long conn = System.currentTimeMillis();
			System.out.println("[INFO] Device Connected in " + (conn-start) + " ms");
			
			// send request to retrieve file (ontoID;instanceID)
			OutputStream outStream = streamConnection.openOutputStream();
			PrintWriter pWriter = new PrintWriter(new OutputStreamWriter(outStream));
			pWriter.write(ontoID + ";" + instanceID + "\n");
			pWriter.flush();
					
			// // read and save received file
			String fileName = "tmp-" + ontoID + "_" + instanceID + ".owl";
			String dir = System.getProperty("user.dir");
			File file = new File(dir, fileName);
	        FileOutputStream fStream = new FileOutputStream(file);
					
			String lineRead = "";
			InputStream inStream = streamConnection.openInputStream();
	        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
	        do {
	            lineRead = bReader.readLine();
	            fStream.write(lineRead.concat("\n").getBytes());
	            //System.out.println(lineRead);
	        } while (!lineRead.trim().replace("\n","").equals("</rdf:RDF>"));
	        bReader.close();
	        inStream.close();
	        fStream.close();
	               
	        streamConnection.close();
	        
	        long end = System.currentTimeMillis();
	        System.out.println("[INFO] " + fileName + " downloaded in " + (end-conn) + " ms");
	        
	        return file;
		}
		
	}

}
