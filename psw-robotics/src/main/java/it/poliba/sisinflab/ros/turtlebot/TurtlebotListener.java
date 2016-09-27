package it.poliba.sisinflab.ros.turtlebot;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import kobuki_msgs.BumperEvent;
import kobuki_msgs.CliffEvent;

public class TurtlebotListener extends AbstractNodeMain {
	
	BumperEvent bumperEvent = null;
	CliffEvent cliffEvent = null;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava_turtlebot/listener");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
				
		subscribeBumper(connectedNode);
		subscribeCliff(connectedNode);
		
	}

	/*** Read data about Bumper events ***/
	private void subscribeBumper(ConnectedNode connectedNode) {
		Subscriber<BumperEvent> subscriber = connectedNode.newSubscriber(Turtlebot.BUMPER, BumperEvent._TYPE);
		subscriber.addMessageListener(new MessageListener<BumperEvent>() {
			@Override
			public void onNewMessage(BumperEvent message) {				
				bumperEvent = message;
			}
		});		
	}
	
	public BumperEvent getBumperEvent(){
		return bumperEvent;
	}
	
	/*** Read data about Cliff events ***/
	private void subscribeCliff(ConnectedNode connectedNode) {
		Subscriber<CliffEvent> subscriber = connectedNode.newSubscriber(Turtlebot.CLIFF, CliffEvent._TYPE);
		subscriber.addMessageListener(new MessageListener<CliffEvent>() {
			@Override
			public void onNewMessage(CliffEvent message) {				
				cliffEvent = message;
			}
		});		
	}
	
	public CliffEvent getCliffEvent(){
		return cliffEvent;
	}
	
}