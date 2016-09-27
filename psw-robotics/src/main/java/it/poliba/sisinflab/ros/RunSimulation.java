package it.poliba.sisinflab.ros;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.poliba.sisinflab.ros.turtlebot.semantic.SemanticTurtlebot;

public class RunSimulation {
	
	static {
        Logger rootLogger = Logger.getLogger("");
        Handler[] rootHandlers = rootLogger.getHandlers();
        for (Handler handler : rootHandlers) {
            handler.setLevel(Level.WARNING);
        }
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		String master = "http://192.168.2.185:11311";
	    String host = "192.168.2.138";
		
		SemanticTurtlebot bot = new SemanticTurtlebot(master, host);
		
		boolean goal = false;
		while(!goal){
			goal = bot.searchAndGo();
			Thread.sleep(60000);
		}	
		
		System.exit(0);		
	}

}
