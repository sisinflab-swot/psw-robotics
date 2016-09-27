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
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import it.poliba.sisinflab.psw.ble.beacon.EddystoneBeacon;
import it.poliba.sisinflab.psw.ble.beacon.PSWUidBeacon;
import it.poliba.sisinflab.psw.ble.beacon.PSWUrlBeacon;
import it.poliba.sisinflab.psw.ble.beacon.UidBeacon;
import it.poliba.sisinflab.psw.ble.beacon.UrlBeacon;

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

		if (type.equals(EddystoneBeacon.UID)) {
			String ns = object.get("namespace").asString();
			String inst = object.get("instance").asString();

			UidBeacon uidBeacon = new PSWUidBeacon(id, pow, rssi, dist, ts, ns, inst);
			pushUidBeacon(uidBeacon);

		} else if (type.equals(EddystoneBeacon.URL)) {
			String url = object.get("url").asString();

			PSWUrlBeacon urlBeacon = new PSWUrlBeacon(id, pow, rssi, dist, ts, url);
			pushUrlBeacon(urlBeacon);
		}

	}
	
	private void pushUidBeacon(UidBeacon b) {
		UidBeacon oldBeacon = (UidBeacon) psw.beacons.get(b.getKey());
		if (oldBeacon == null || isOld(oldBeacon, b)) {
			new UidBeaconDownloaderThread(b).run();			
		}
	}

	private void pushUrlBeacon(UrlBeacon b) {
		UrlBeacon oldBeacon = (UrlBeacon) psw.beacons.get(b.getKey());
		if (oldBeacon == null || isOld(oldBeacon, b)) {
			// resolve short URL and download the relative file (if present)	
			new UrlBeaconDownloaderThread(b).run();					
		}
	}

	private boolean isOld(EddystoneBeacon oldB, EddystoneBeacon newB) {
		if ((newB.getTimestamp() - oldB.getTimestamp()) >= MAX_AGE)
			return true;
		else
			return false;
	}

	class UrlBeaconDownloaderThread extends Thread {
		UrlBeacon b;

		public UrlBeaconDownloaderThread(UrlBeacon b) {
			super();
			this.b = b;
		}

		public void run() {
			URL url;
			try {
				/*** Resolve short URL ***/
				long start = System.currentTimeMillis();
				url = new URL(b.getShortUrl());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
		        connection.setInstanceFollowRedirects(false);
		        connection.connect();
		        String expandedURL = connection.getHeaderField("Location");
		        connection.getInputStream().close();
		        long end = System.currentTimeMillis();
		        System.out.println("[INFO] " + expandedURL + " resolved in " + (end-start) + "ms");
		        
		        
		        //System.out.println("[INFO] " + b.getShortUrl() + " --> " + expandedURL);		        
		        b.setFullUrl(expandedURL);
		        
		        if(b instanceof PSWUrlBeacon){		        	
		        	String name = "tmp-" + b.getID() + "." + b.getType();
		        	File tmp = new File(TMP_DIR + name);
		        	
		        	start = System.currentTimeMillis();
		        	FileUtils.copyURLToFile(new URL(expandedURL), tmp);
		        	end = System.currentTimeMillis();
		        	System.out.println("[INFO] " + expandedURL + " downloaded in " + (end-start) + "ms");
		        	
		        	/*IRI iri = psw.kb.loadIndividualFromFile(tmp);
		        	((SemUrlBeacon) b).setAnnotationIRI(iri);
		        	
		        	double sr = psw.kb.getSemanticRank(iri);
		        	((SemUrlBeacon) b).setSemanticRank(sr);*/
		        	
		        	psw.kb.loadIndividualFromFile((PSWUrlBeacon)b, new FileInputStream(tmp));
		        	
		        	FileUtils.forceDelete(tmp);
		        }
		        
		        psw.beacons.put(b.getKey(), b);        
		        
			} catch (IOException e) {
				System.err.println("[ERROR] " + b.getShortUrl() + " not resolved!");
				e.printStackTrace();
			}
		}

	}
	
	class UidBeaconDownloaderThread extends Thread {
		UidBeacon b;
		
		public UidBeaconDownloaderThread(UidBeacon b) {
			super();
			this.b = b;
		}
		
		public void run() {
			
			if (b instanceof PSWUidBeacon){
				
				while(bt_busy){
					try {
						this.wait(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				PSWUidBeacon pswB = (PSWUidBeacon) b;
				try {
					bt_busy = true;
					File tmp = getFileFromRemoteDevice(pswB.getDeviceAddress(), pswB.getOntologyID(), pswB.getResourceID());										
					psw.kb.loadIndividualFromFile(pswB, new FileInputStream(tmp));	
					bt_busy = false;
					
				} catch (IOException e) {
					System.err.println("[ERROR] Error during communication with [" + pswB.getDeviceAddress() + "] device");
					e.printStackTrace();
				}
			}
			
			psw.beacons.put(b.getKey(), b);	
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
