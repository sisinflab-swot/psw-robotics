package it.poliba.sisinflab.ros.turtlebot;

import java.util.List;

import org.ros.message.Duration;
import org.ros.message.MessageFactory;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import com.github.ekumen.rosjava_actionlib.ActionClient;
import com.github.ekumen.rosjava_actionlib.ActionClientListener;
import com.github.ekumen.rosjava_actionlib.ClientStateMachine;

import actionlib_msgs.GoalStatus;
import actionlib_msgs.GoalStatusArray;
import geometry_msgs.Twist;
import move_base_msgs.MoveBaseActionFeedback;
import move_base_msgs.MoveBaseActionGoal;
import move_base_msgs.MoveBaseActionResult;

public class TurtlebotTalker extends AbstractNodeMain implements ActionClientListener<MoveBaseActionFeedback, MoveBaseActionResult> {

	Publisher<Twist> publisher;
	MessageFactory f;
	ActionClient<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult> ac = null;
	int cgs = -1; // Current Goal Status

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava_turtlebot/talker");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {		
		publisher = connectedNode.newPublisher(Turtlebot.INPUT_NAVI, Twist._TYPE);
		f = connectedNode.getTopicMessageFactory();

		ac = new ActionClient<MoveBaseActionGoal, MoveBaseActionFeedback, MoveBaseActionResult>(connectedNode,
				"/move_base", MoveBaseActionGoal._TYPE, MoveBaseActionFeedback._TYPE, MoveBaseActionResult._TYPE);
		ac.attachListener(this);
		
		System.out.println("Waiting for the move_base action server to come up...");
		while (ac.waitForActionServerToStart(new Duration(5.0))) {
			// waiting...
		}
		System.out.println("Move_base action server started!");
	}

	public void moveRobot(double x, double y, double z, double ang_x, double ang_y, double ang_z) {
		if (publisher != null) {
			Twist msg = publisher.newMessage();

			msg.getLinear().setX(x);
			msg.getLinear().setY(y);
			msg.getLinear().setZ(z);

			msg.getAngular().setX(ang_x);
			msg.getAngular().setY(ang_y);
			msg.getAngular().setZ(ang_z);

			publisher.publish(msg);

		} else
			System.err.println("[ERROR] Publisher not created!");
	}

	public void moveToAbsoluteGoal(double x, double y, double w) {
		MoveBaseActionGoal goal = ac.newGoalMessage();

		goal.getGoal().getTargetPose().getHeader().setFrameId("map");
		goal.getGoal().getTargetPose().getHeader().setStamp(new Time());

		goal.getGoal().getTargetPose().getPose().getPosition().setX(x);
		goal.getGoal().getTargetPose().getPose().getPosition().setY(y);
		goal.getGoal().getTargetPose().getPose().getOrientation().setW(w);

		System.out.println("Sending goal...");
		ac.sendGoal(goal);

		long start = System.currentTimeMillis();
		while (ac.getGoalState() != ClientStateMachine.ClientStates.DONE) {
			try {
				Thread.sleep(2000);
				
				if ((System.currentTimeMillis() - start)>10000)
					break;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	

	}
	
	public void moveToRelativeGoal(double x, double y, double w) {
		MoveBaseActionGoal goal = ac.newGoalMessage();

		goal.getGoal().getTargetPose().getHeader().setFrameId("map");
		goal.getGoal().getTargetPose().getHeader().setStamp(new Time());

		goal.getGoal().getTargetPose().getPose().getPosition().setX(x);
		goal.getGoal().getTargetPose().getPose().getPosition().setY(y);
		goal.getGoal().getTargetPose().getPose().getOrientation().setW(w);

		System.out.println("Sending goal...");
		ac.sendGoal(goal);

		long start = System.currentTimeMillis();
		while (ac.getGoalState() != ClientStateMachine.ClientStates.DONE) {
			try {
				Thread.sleep(2000);
				
				if ((System.currentTimeMillis() - start)>10000)
					break;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	

	}

	@Override
	public void resultReceived(MoveBaseActionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void feedbackReceived(MoveBaseActionFeedback feedback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void statusReceived(GoalStatusArray status) {		
		if (ac != null && ac.getGoalState() != cgs){
			cgs = ac.getGoalState();
			List<GoalStatus> statusList = status.getStatusList();
		    for(GoalStatus gs:statusList) {
		    	System.out.println("GoalID: " + gs.getGoalId().getId() + " -- GoalStatus: " + gs.getStatus() + " -- " + gs.getText());
		    }
		    System.out.println("Current state of our goal: " + ClientStateMachine.ClientStates.translateState(ac.getGoalState()));	
		}				    
	}

}
