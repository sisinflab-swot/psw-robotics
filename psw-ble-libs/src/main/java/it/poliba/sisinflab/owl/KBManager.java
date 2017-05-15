package it.poliba.sisinflab.owl;

import java.io.File;
import java.io.InputStream;

import org.physical_web.collection.UrlDevice;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import it.poliba.sisinflab.owl.owlapi.MicroReasoner;
import it.poliba.sisinflab.owl.owlapi.MicroReasonerFactory;
import it.poliba.sisinflab.owl.sod.hlds.Item;
import it.poliba.sisinflab.psw.PswDevice;
import it.poliba.sisinflab.psw.ble.PSWBeaconScanner;

public class KBManager {

	MicroReasoner reasoner = null;
	OWLOntologyManager manager;
	OWLOntology onto;
	
	IRI defaultIRI = null;
	IRI requestIRI = null;

	public KBManager(File file) throws OWLOntologyCreationException {
		// Get hold of an ontology manager
		manager = OWLManager.createOWLOntologyManager();

		// Load the local ontology
		onto = manager.loadOntologyFromOntologyDocument(file);
		defaultIRI = onto.getOntologyID().getOntologyIRI();
		System.out.println("Loaded ontology: " + onto);

		// Return an instance of OWLReasoner class that represents our Mini-ME reasoner
		reasoner = new MicroReasonerFactory().createMicroReasoner(onto);
	}
	
	public KBManager(InputStream is) throws OWLOntologyCreationException {
		// Get hold of an ontology manager
		manager = OWLManager.createOWLOntologyManager();

		// Load the local ontology
		onto = manager.loadOntologyFromOntologyDocument(is);
		defaultIRI = onto.getOntologyID().getOntologyIRI();
		System.out.println("Loaded ontology: " + onto);

		// Return an instance of OWLReasoner class that represents our Mini-ME reasoner
		reasoner = new MicroReasonerFactory().createMicroReasoner(onto);
	}

	public IRI loadIndividualFromFile(File file) {

		IRI iri = null;

		try {
			OWLOntology tmp = manager.loadOntologyFromOntologyDocument(file);
			reasoner.loadSupply(tmp);
			
			OWLNamedIndividual ind = getIndividual(tmp);

			if (ind != null) {
				iri = ind.getIRI();
				System.out.println("[INFO] New OWL Individual: " + iri);
			}

			manager.removeOntology(tmp);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return iri;
	}
	
	public void loadIndividualFromFile(UrlDevice b, InputStream is) {

		long start = System.currentTimeMillis();
		IRI iri = null;

		try {
			OWLOntology tmp = manager.loadOntologyFromOntologyDocument(is);
			reasoner.loadSupply(tmp);
			
			OWLNamedIndividual ind = getIndividual(tmp);

			if (ind != null) {
				iri = ind.getIRI();
				
				double lat = 0;
				double lon = 0;
				
				/*** Retrieve Geo OWLAnnotations ***/
				for(OWLAnnotation an : ind.getAnnotations(tmp)){
	                System.out.println(an.getProperty().getIRI() + ": " + an.getValue());
	                if (an.getProperty().getIRI().getFragment().equals("lat")){
	                	OWLLiteral val=(OWLLiteral)an.getValue();
	                	lat = val.parseDouble();
	                } else if (an.getProperty().getIRI().getFragment().equals("lon")){
	                	OWLLiteral val=(OWLLiteral)an.getValue();
	                	lon = val.parseDouble();
	                }
	                
	                b = new UrlDevice.Builder(b)
	                		.addExtra(PswDevice.PSW_IRI_KEY, iri.toString())
	                		.addExtra(PSWBeaconScanner.LAT_KEY, lat)
	                		.addExtra(PSWBeaconScanner.LON_KEY, lon).build();

	                //TODO: Add other properties in the OWL file
	            }
			}

			manager.removeOntology(tmp);
			
			long end = System.currentTimeMillis();
			System.out.println("[INFO] OWL Individual (" + iri + ") loaded in " + (end-start) + " ms");
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*public void loadIndividualFromFile(UrlDevice b, InputStream is) {
		
		long start = System.currentTimeMillis();
		IRI iri = null;

		try {
			OWLOntology tmp = manager.loadOntologyFromOntologyDocument(is);
			reasoner.loadSupply(tmp);
			
			OWLNamedIndividual ind = getIndividual(tmp);

			if (ind != null) {
				iri = ind.getIRI();

				double lat, lon;
				
				for(OWLAnnotation an : ind.getAnnotations(tmp)){
	                System.out.println(an.getProperty().getIRI() + ": " + an.getValue());
	                if (an.getProperty().getIRI().getFragment().equals("lat")){
	                	OWLLiteral val=(OWLLiteral)an.getValue();
	                	lat = val.parseDouble();
	                } else if (an.getProperty().getIRI().getFragment().equals("lon")){
	                	OWLLiteral val=(OWLLiteral)an.getValue();
	                	lon = val.parseDouble();
	                }
	                
	                b = new UrlDevice.Builder(b)
	                		.addExtra(PswDevice.PSW_IRI_KEY, iri.toString())
	                		.addExtra(PSWBeaconScanner.LAT_KEY, lat)
	                		.addExtra(PSWBeaconScanner.LON_KEY, lon).build();
	            }
			}

			manager.removeOntology(tmp);
			
			long end = System.currentTimeMillis();
			System.out.println("[INFO] OWL Individual (" + iri + ") loaded in " + (end-start) + " ms");
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}*/
	
	private OWLNamedIndividual getIndividual(OWLOntology onto){
        for(OWLNamedIndividual ind : onto.getIndividualsInSignature()){
            return ind;
        }
        return null;
    }
	
	public IRI loadRequestFromFile(File req){
        try {
            OWLOntology onto_ind = manager.loadOntologyFromOntologyDocument(req);                        
            
            reasoner.loadDemand(onto_ind);
            requestIRI = getIndividual(onto_ind).getIRI();
            
            manager.removeOntology(onto_ind);
            System.out.println("[INFO] " + requestIRI + " loaded!");
        } catch (OWLOntologyCreationException e) {
            System.err.println("[ERROR] >>> Request " + req.getName() + " already exists!");
            e.printStackTrace();
            return null;
        }
        
        return requestIRI;
    }
	
	public IRI loadRequestFromFile(InputStream req){
        try {
            OWLOntology onto_ind = manager.loadOntologyFromOntologyDocument(req);                        
            
            reasoner.loadDemand(onto_ind);
            requestIRI = getIndividual(onto_ind).getIRI();
            
            manager.removeOntology(onto_ind);
            System.out.println("[INFO] " + requestIRI + " loaded!");
        } catch (OWLOntologyCreationException e) {
            System.err.println("[ERROR] >>> Request already exists!");
            e.printStackTrace();
            return null;
        }
        
        return requestIRI;
    }
	
	public IRI getRequestIRI(){
		return requestIRI;
	}
	
	public double getSemanticRank(IRI resIRI, IRI reqIRI){
        Item empty = new Item(IRI.create(defaultIRI.toString() + "#Empty"));
        reasoner.loadSupply(empty);
        double penalty_res = reasoner.abduction(resIRI, reqIRI).penalty;
        double penalty_top = reasoner.abduction(IRI.create(defaultIRI.toString() + "#Empty"), reqIRI).penalty;
        double rank = 1.0 - (penalty_res/penalty_top);
        return rank;
    }
	
	public double getSemanticRank(IRI resIRI){
        Item empty = new Item(IRI.create(defaultIRI.toString() + "#Empty"));
        reasoner.loadSupply(empty);
        double penalty_res = reasoner.abduction(resIRI, requestIRI).penalty;
        double penalty_top = reasoner.abduction(IRI.create(defaultIRI.toString() + "#Empty"), requestIRI).penalty;
        double rank = 1.0 - (penalty_res/penalty_top);
        return rank;
    }

}
