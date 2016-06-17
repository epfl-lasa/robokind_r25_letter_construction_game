package com.github.rosjava.my_pub_sub_tutorial.Robokind;

import org.robokind.api.motion.messaging.RemoteRobot;
import org.robokind.api.speech.messaging.RemoteSpeechServiceClient;

import static org.robokind.api.motion.Robot.*;
import static org.robokind.client.basic.RobotJoints.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.logging.Log;
import org.apache.log4j.BasicConfigurator;
import org.robokind.api.animation.Animation;
import org.robokind.api.animation.messaging.RemoteAnimationPlayerClient;
import org.robokind.api.common.position.NormalizedDouble;
import org.robokind.api.motion.Joint;
import org.robokind.api.motion.Robot.JointId;
import org.robokind.api.motion.Robot.RobotPositionHashMap;
import org.robokind.client.basic.Robokind;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;

import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import visualization_msgs.MarkerArray;

public class InteractionDemo extends AbstractNodeMain {
	// class variables
	private Log log;
	private static RobotPositionMap myGoalPositions;
	private static RobotPositionMap myDefaultPositions;
	private static RemoteSpeechServiceClient mySpeaker;
	private static RemoteAnimationPlayerClient myPlayer;
	private static RemoteRobot myRobot;
	private static int timeFrame;
	private int roundTotalObjects;
	private List<List<Double>> objectPositions;
	private List<List<Double>> objectCurrentPositions;
	private List<List<Double>> ObjectInitialPositions;
	// private int currentnumber;
	private boolean start = false;

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("rosjava/InteractionDemo");
	}

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		BasicConfigurator.configure();
		log = connectedNode.getLog();
		objectPositions = new ArrayList<List<Double>>();
		ObjectInitialPositions = new ArrayList<List<Double>>();

		// kinect object cluster subscriber
		final Subscriber<visualization_msgs.MarkerArray> objectClusterSubscriber = connectedNode
				.newSubscriber("/tabletop/clusters", visualization_msgs.MarkerArray._TYPE);

		objectClusterSubscriber.addMessageListener(new MessageListener<visualization_msgs.MarkerArray>() {
			@Override
			public void onNewMessage(visualization_msgs.MarkerArray message) {
				getObjectData(message);
			}
		});

		// execute robot
		try {
			robotInteraction();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getObjectData(MarkerArray message) {
		int all = message.getMarkers().size();
		objectPositions.clear();

		// save object positions
		for (int i = 0; i < all; i++) {
			if (message.getMarkers().get(i).getPoints().size() < 800) {
				List<Double> objectCoords = new ArrayList<Double>();

				objectCoords.add(message.getMarkers().get(i).getPoints().get(3).getX() * 100);
				objectCoords.add(message.getMarkers().get(i).getPoints().get(3).getY() * 100);
				objectCoords.add(message.getMarkers().get(i).getPoints().get(3).getZ() * 100);

				objectPositions.add(objectCoords);
			}
		}

	}

	private void robotInteraction() throws InterruptedException {

		String robotID = "myRobot";
		String robotIP = "192.168.2.3";

		String log_mode;
		String log_interaction_start_time;
		String log_total_objects;
		String log_round;
		String log_robot_pointed_object_position_x;
		String log_robot_pointed_object_position_y;
		String log_robot_pointed_object_position_z;
		String log_robot_pointing_start_time;
		String log_robot_pointing_finish_time;
		String log_user_modified_object_position_x = null;
		String log_user_modified_object_position_y = null;
		String log_user_modified_object_position_z = null;
		String log_user_response_time = null;
		String log_success;

		// set addresses
		SetSettings settings = new SetSettings(robotID, robotIP);

		// make connection
		myRobot = Robokind.connectRobot();
		mySpeaker = Robokind.connectSpeechService();
		myDefaultPositions = myRobot.getDefaultPositions();

		// robot position with respect to the camera
		double x_r25 = -20;
		double y_r25 = -50;
		double z_r25 = 30;

		// Conditions: 1 -> speach, 2 -> head, 3 -> hands, 4 -> head & hands
		int MODE = 4;
		log_mode = MODE + "";

		// check if robot is connected
		if (myRobot.isConnected()) {

			Expression expression = new Expression();
			expression.setExpressionJoints(myRobot);
			Letters letters = new Letters();
			String currentLetter = null;
			log_interaction_start_time = System.currentTimeMillis() + "";
			log_total_objects = objectPositions.size() + "";

			mySpeaker.speak("Hi!");
			mySpeaker.speak("I would like to play a letter game with you");
			Robokind.sleep(7000);
			// expression.smile(700, myRobot);
			// expression.blink(500, myRobot);
			mySpeaker
					.speak("I will point you to the objects on the table and you have to construct a letter from them");
			Robokind.sleep(7000);
			mySpeaker.speak("Lets start the game!");
			Robokind.sleep(2000);

			double x;
			double y;
			double z;

			ObjectInitialPositions.clear();

			for (int i = 0; i < objectPositions.size(); i++) {
				List<Double> objectCoords = new ArrayList<Double>();
				double init_x = (objectPositions.get(i).get(0)) - x_r25;
				double init_y = (objectPositions.get(i).get(1)) - y_r25;
				double init_z = (objectPositions.get(i).get(2)) - z_r25;
				objectCoords.add(init_x);
				objectCoords.add(init_y);
				objectCoords.add(init_z);

				ObjectInitialPositions.add(objectCoords);
			}
			// log.info(objectPositions.get(0).get(0));

			for (int i = 0; i < ObjectInitialPositions.size(); i++) {

				log_round = i + "";

				x = ObjectInitialPositions.get(i).get(0);
				y = ObjectInitialPositions.get(i).get(1);
				z = ObjectInitialPositions.get(i).get(1);

				log_robot_pointed_object_position_x = x + "";
				log_robot_pointed_object_position_y = y + "";
				log_robot_pointed_object_position_z = z + "";

				Robokind.sleep(2000);
				log_robot_pointing_start_time = System.currentTimeMillis() + "";
				if (MODE != 1) {
					moveRobot(x, y, z, MODE, myRobot);
				}

				if (ObjectInitialPositions.size() == 4) {
					currentLetter = "T";
				} else if (ObjectInitialPositions.size() == 5) {
					currentLetter = "E";
				}
				Robokind.sleep(600);
				if (currentLetter != null) {
					letters.describeLetter(currentLetter, MODE, i, 7000, mySpeaker, myRobot, myDefaultPositions);
				}

				if (MODE == 3 || MODE == 4) {

					JointId left_shoulder_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_PITCH));
					JointId left_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_ROLL));
					JointId right_shoulder_pitch = new JointId(myRobot.getRobotId(),
							new Joint.Id(RIGHT_SHOULDER_PITCH));
					JointId right_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SHOULDER_ROLL));
					JointId brows = new JointId(myRobot.getRobotId(), new Joint.Id(BROWS));
					JointId left_smile = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SMILE));
					JointId right_smile = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SMILE));
					JointId neck_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_PITCH));
					JointId neck_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_YAW));
					JointId eyelids = new JointId(myRobot.getRobotId(), new Joint.Id(EYELIDS));

					myGoalPositions = new RobotPositionHashMap();
					myGoalPositions.put(left_shoulder_pitch, new NormalizedDouble(0.185));
					myGoalPositions.put(left_shoulder_roll, new NormalizedDouble(0.55));
					myGoalPositions.put(right_shoulder_pitch, new NormalizedDouble(0.185));
					myGoalPositions.put(right_shoulder_roll, new NormalizedDouble(0.55));
					myGoalPositions.put(left_smile, new NormalizedDouble(0.5));
					myGoalPositions.put(right_smile, new NormalizedDouble(0.5));
					// makes the robot look up
					myGoalPositions.put(neck_pitch, new NormalizedDouble(0.7));
					myGoalPositions.put(neck_yaw, new NormalizedDouble(0.5));
					myGoalPositions.put(brows, new NormalizedDouble(0.542));
					myGoalPositions.put(eyelids, new NormalizedDouble(0.8));
					myRobot.move(myGoalPositions, 1000);
				}

				log_robot_pointing_finish_time = System.currentTimeMillis() + "";
				Robokind.sleep(4000);

				long before = System.currentTimeMillis();
				long after = System.currentTimeMillis() - before;

				if (objectPositions.size() > 0) {

					while (after <= 4000) {

						after = System.currentTimeMillis() - before;
						if (i == 0) {
							if (objectPositions.size() < ObjectInitialPositions.size()) {
								double init_x = (objectPositions.get(0).get(0)) - x_r25;
								double init_y = (objectPositions.get(0).get(1)) - y_r25;
								double init_z = (objectPositions.get(0).get(2)) - z_r25;

								log_user_response_time = System.currentTimeMillis() + "";
								log_user_modified_object_position_x = init_x + "";
								log_user_modified_object_position_y = init_y + "";
								log_user_modified_object_position_z = init_z + "";
								break;
							}

						} else {
							if (objectPositions.size() < (ObjectInitialPositions.size() - i)) {
								double init_x = (objectPositions.get(0).get(0)) - x_r25;
								double init_y = (objectPositions.get(0).get(1)) - y_r25;
								double init_z = (objectPositions.get(0).get(2)) - z_r25;

								log_user_response_time = System.currentTimeMillis() + "";
								log_user_modified_object_position_x = init_x + "";
								log_user_modified_object_position_y = init_y + "";
								log_user_modified_object_position_z = init_z + "";
								break;
							}
						}

						// if(after > 3000) {
						// mySpeaker.speak("I will repeat it again!");
						// i = i - 1;
						// break;
						// }
					}
				}

				SimpleLog.write(log_mode + "," + log_interaction_start_time + "," + log_total_objects + "," + log_round
						+ "," + log_robot_pointed_object_position_x + "," + log_robot_pointed_object_position_y + ","
						+ log_robot_pointed_object_position_z + "," + log_robot_pointing_start_time + ","
						+ log_robot_pointing_finish_time + "," + log_user_response_time + ","
						+ log_user_modified_object_position_x + "," + log_user_modified_object_position_y + ","
						+ log_user_modified_object_position_z + "");
				Robokind.sleep(3000);
				roundTotalObjects = objectPositions.size();
			}

			Robokind.sleep(2000);

			// robot final response
			if (objectPositions.size() == 0) {
				expression.smile(700, myRobot);
				Robokind.sleep(3000);
				mySpeaker.speak("Great Job, you just constructed a letter");
				mySpeaker.speak(currentLetter);
				// SimpleLog.write("success: 1");

			} else {
				expression.frown(700, myRobot);
				Robokind.sleep(700);
				mySpeaker.speak("Hmm you did not construct a letter");
				Robokind.sleep(700);
				// SimpleLog.write("success: 0");
			}

			JointId left_shoulder_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_PITCH));
			JointId left_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_ROLL));
			JointId right_shoulder_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SHOULDER_PITCH));
			JointId right_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SHOULDER_ROLL));
			JointId brows = new JointId(myRobot.getRobotId(), new Joint.Id(BROWS));
			JointId left_smile = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SMILE));
			JointId right_smile = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SMILE));
			JointId neck_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_PITCH));
			JointId neck_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_YAW));
			JointId eyelids = new JointId(myRobot.getRobotId(), new Joint.Id(EYELIDS));

			myGoalPositions = new RobotPositionHashMap();
			myGoalPositions.put(left_shoulder_pitch, new NormalizedDouble(0.185));
			myGoalPositions.put(left_shoulder_roll, new NormalizedDouble(0.55));
			myGoalPositions.put(right_shoulder_pitch, new NormalizedDouble(0.185));
			myGoalPositions.put(right_shoulder_roll, new NormalizedDouble(0.55));
			myGoalPositions.put(left_smile, new NormalizedDouble(0.5));
			myGoalPositions.put(right_smile, new NormalizedDouble(0.5));
			// makes the robot look up
			myGoalPositions.put(neck_pitch, new NormalizedDouble(0.3));
			myGoalPositions.put(neck_yaw, new NormalizedDouble(0.5));
			myGoalPositions.put(brows, new NormalizedDouble(0.542));
			myGoalPositions.put(eyelids, new NormalizedDouble(0.8));

			myRobot.move(myGoalPositions, 3000);

			SimpleLog.write("-------------------------------------------------------");
			
			///////////////////////////////////////////
			/// DISCONNECT AND EXIT
			///////////////////////////////////////////
			Robokind.disconnect();
			System.exit(0);
		}
	}

	private void moveRobot(double x, double y, double z, int mode, RemoteRobot myRobot) {

		// head joints
		JointId neck_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_PITCH));
		JointId neck_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(NECK_YAW));
		JointId brows = new JointId(myRobot.getRobotId(), new Joint.Id(BROWS));
		JointId eyelids = new JointId(myRobot.getRobotId(), new Joint.Id(EYELIDS));

		// right hand joints
		JointId left_elbow_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_ELBOW_YAW));
		JointId left_elbow_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_ELBOW_PITCH));
		JointId left_shoulder_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_PITCH));
		JointId left_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_SHOULDER_ROLL));
		JointId left_wrist_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_WRIST_YAW));
		JointId left_hand_grasp = new JointId(myRobot.getRobotId(), new Joint.Id(LEFT_HAND_GRASP));

		// right hand joints
		JointId right_elbow_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_ELBOW_YAW));
		JointId right_elbow_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_ELBOW_PITCH));
		JointId right_shoulder_pitch = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SHOULDER_PITCH));
		JointId right_shoulder_roll = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_SHOULDER_ROLL));
		JointId right_wrist_yaw = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_WRIST_YAW));
		JointId right_hand_grasp = new JointId(myRobot.getRobotId(), new Joint.Id(RIGHT_HAND_GRASP));

		double default_neck_pitch_position = 0.3;
		double shoulder_pitch_max = 0.65;
		double shoulder_pitch_min = 0.185;
		double shoulder_r_max = 0.5;
		double shoulder_r_min = 0.39;

		double d = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		double angle_v = Math.toDegrees(Math.atan(d / z));
		double angle_h = Math.toDegrees(Math.atan(x / y));

		// System.out.println(angle_h);

		double brows_value = angle_v / 90;
		double neck_pitch_value = default_neck_pitch_position / 90 * angle_v;
		double neck_yaw_value = (90 - angle_h) / 180;
		double shoulder_pitch_value = shoulder_pitch_min + ((shoulder_pitch_max - shoulder_pitch_min) / 90 * angle_v);
		double shoulder_r_value = shoulder_r_max - ((shoulder_r_max - shoulder_r_min) / 90) * angle_h;

		Robokind.sleep(400);

		// head
		myGoalPositions = new RobotPositionHashMap();
		if (mode == 2 || mode == 4) {
			myGoalPositions.put(neck_pitch, new NormalizedDouble(neck_pitch_value));
			myGoalPositions.put(neck_yaw, new NormalizedDouble(neck_yaw_value));
			myGoalPositions.put(brows, new NormalizedDouble(brows_value));
		}
		// hands
		if (mode == 3 || mode == 4) {
			if (x >= 0) {
				myGoalPositions.put(right_shoulder_pitch, new NormalizedDouble(shoulder_pitch_value));
				myGoalPositions.put(right_wrist_yaw, new NormalizedDouble(0));
				myGoalPositions.put(right_shoulder_roll, new NormalizedDouble(shoulder_r_value));
			} else {
				myGoalPositions.put(left_shoulder_pitch, new NormalizedDouble(shoulder_pitch_value));
				myGoalPositions.put(left_wrist_yaw, new NormalizedDouble(0));
				myGoalPositions.put(right_shoulder_roll, new NormalizedDouble(shoulder_r_value));
			}
		}

		myRobot.move(myGoalPositions, 1000);

	}

	// private void moveRobotToDefault(int timeFrame) {
	// myGoalPositions = new RobotPositionHashMap();
	// myGoalPositions.put(left_elbow_yaw, new NormalizedDouble(0.38));
	// myGoalPositions.put(left_elbow_pitch, new NormalizedDouble(0.99));
	// myGoalPositions.put(left_shoulder_r, new
	// NormalizedDouble(0.055555555555556));
	// // move robot back to defaults
	// myRobot.move(myGoalPositions, 1000);
	//
	// }

}