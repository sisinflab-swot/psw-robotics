package it.poliba.sisinflab.ros;

import it.poliba.sisinflab.ros.turtlebot.Turtlebot;
import kobuki_msgs.BumperEvent;

public class SimpleTest {

	public static void main(String[] args) throws InterruptedException {	
		
		Turtlebot bot = new Turtlebot();
		
		while(true) {	
			
			/*** Write command ***/
			bot.goForward();
			//bot.goBackward();
			
			/*** Read data ***/
			if (bot.getBumperState() == BumperEvent.PRESSED){
				System.out.println("...Bumper: " + bot.getBumper());
				System.out.println("...State: " + bot.getBumperState());
			}			
			
			Thread.sleep(1000);
		}
	}

}
