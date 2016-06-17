package com.github.rosjava.my_pub_sub_tutorial.Robokind;

import org.robokind.api.common.position.NormalizedDouble;
import org.robokind.api.motion.Joint;
import org.robokind.api.motion.messaging.RemoteRobot;
import org.robokind.api.speech.messaging.RemoteSpeechServiceClient;

import static org.robokind.api.motion.Robot.*;
import org.robokind.client.basic.Robokind;
import static org.robokind.client.basic.RobotJoints.*;

/**
 * Letters.java
 */
public class Letters {

	/**
	 * Default constructor
	 */
	public Letters() {

	}

	/**
	 * Method to describe a letter
	 * 
	 * @param letter
	 *            robot needs to describe
	 * @param timeFrame
	 *            the amount of time between speech (milliseconds)
	 * @param mySpeaker
	 *            the connected RemoteSpeechServiceClient
	 * @param myRobot
	 *            the connected RemoteRobot
	 * @throws InterruptedException
	 */
	public void describeLetter(String letter, int mode, int i, int timeFrame, RemoteSpeechServiceClient mySpeaker,
			RemoteRobot myRobot, RobotPositionMap myDefaultPositions) throws InterruptedException {

		switch (letter) {
		case "E":
			switch (i) {
			case 0:
				if (mode == 1) {
					mySpeaker.speak("Take the rightmost block and put it horizontally in front of you");
					Robokind.sleep(timeFrame);

				} else {
					mySpeaker.speak("Put this block horizontally in front of you");
					Robokind.sleep(timeFrame);
				}
				break;
			case 1:
				if (mode == 1) {
					mySpeaker.speak(
							"Take the leftmost block and put it vertically from the first block to form 90 degree on the left side");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block vertically to the left of the previous block");
					Robokind.sleep(1200);
					mySpeaker.speak("It should form a right angle");
					Robokind.sleep(timeFrame);
				}
				break;
			case 2:
				if (mode == 1) {
					mySpeaker.speak(
							"Take the farther block and put it vertically in the continuation of the previous block");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block vertically in line with the previous block");
					Robokind.sleep(timeFrame);
				}
				break;
			case 3:
				if (mode == 1) {
					mySpeaker.speak(
							"Take the closest block from you and put it horizontally in the right edge of the previous block and in parallel with the first block");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block horizontally to the right of the previous block ");
					Robokind.sleep(timeFrame);
				}
				break;
			case 4:
				if (mode == 1) {
					mySpeaker.speak(
							"Take the last block and put it horizontally on the right side of vertical block and in parralel of horisontal rectangles");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block  horisontally to the right of the vertical line ");
					Robokind.sleep(1200);
					mySpeaker.speak("It should be in between the other horizontal blocks");
					Robokind.sleep(timeFrame);

				}
				break;
			}
			break;
		case "T":
			switch (i) {
			case 0:
				if (mode == 1) {
					mySpeaker.speak("take the rightmost rectangle and put it horizontally in front of you");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block horizontally in front of you");
					Robokind.sleep(timeFrame);
				}
				break;
			case 1:
				if (mode == 1) {
					mySpeaker.speak(
							"take the leftmost rectangle and put it horizontally and next to the first rectangle");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block horizontally and next to the first rectangle");
					Robokind.sleep(timeFrame);
				}
				break;
			case 2:
				if (mode == 1) {
					mySpeaker.speak(
							"take the farther rectangle from you and put it vertically in the middle of the horisontal line you just constructed");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak(
							"Put this block vertically and in the middle of the horizontal line you just constructed");
					Robokind.sleep(timeFrame);
				}
				break;
			case 3:
				if (mode == 1) {
					mySpeaker.speak(
							"Take the final rectangle and put it vertically in the continuation of the vertical rectangle");
					Robokind.sleep(timeFrame);
				} else {
					mySpeaker.speak("Put this block vertically in the continuation of the previous rectangle");
					Robokind.sleep(timeFrame);
				}
				break;
			}
			break;

		}

	}
}

// switch (letter) {
// case "i" :
// mySpeaker.speak("Put the red square in front of you");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put the yellow square vertically after the red square with
// three-four centimeter distance from it");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put the yellow rectangle vertically and in continuation of
// the yellow square");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put another yellow rectangle on top of the yellow
// rectangle");
// Robokind.sleep(timeFrame);
// break;
// case "C" :
// mySpeaker.speak("Put the blue rectangle horizontally in front of you");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put the red rectangle vertically below the blue rectangle's
// left edge");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put the yellow rectangle vertically in continuation of the
// yellow rectangle");
// Robokind.sleep(timeFrame);
// mySpeaker.speak("Put the yellow rectangle horizontally in the right edge of
// the yellow rectangle and in parallel with the blue rectangle");
// Robokind.sleep(timeFrame);
// break;
