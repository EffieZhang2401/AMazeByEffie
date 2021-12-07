package edu.wm.cs.cs301.EffieZhang.gui;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;
import android.util.Log;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Turn;
import android.os.Bundle;
import android.os.Message;

/**
 * This class specifies a robot driver that operates a robot to escape from a given maze. 
 * 
 * Collaborators: Robot
 * 
 * @author effiezhang
 *
 */

public class Wizard implements RobotDriver{
	private static final String TAG = "Wizard";  //message key
	private static final String KEY = "my message key";  //message key
	private Robot robot;
	private ReliableSensor sensor;
	private Maze mazeConfig;
	private float batteryLevel1;
	private static final int INITIAL_ENERGY = 3500;
	public static boolean getsToExit = false;
	private boolean lost;
	private Thread wizardThread;
	private int speed = 400;
	private int[] speedOptions;


	/**
	 * Constructs a new Wizard object with
	 * the boolean value lost set too false, since the
	 * driver has not lost yet, and initializes array
	 * speedOptions with the possible animation speeds
	 */
	public Wizard() {
		speedOptions = new int[]{2000, 1500, 1000, 900, 800, 700, 600, 500, 450, 400, 350, 300, 250, 200, 150, 100, 50, 25, 10, 5, 1};
		lost = false;
	}
	/**
	 * Assigns a robot platform to the driver.
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		this.robot = r;

	}

	/**
	 * Provides the robot driver with the maze information.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param maze represents the maze, must be non-null and a fully functional maze object.
	 */
	@Override
	public void setMaze(Maze maze) {
		mazeConfig = maze;

	}


	/**
	 * Creates a new background thread for the
	 * driver to run on, which calls the drive1Step2Exit method
	 * until the robot reaches the exit or runs out of energy/crashes
	 * and controls how fast the animation speed is
	 * @param d distance to the exit
	 */
	public void runThread(int d) throws Exception{
		final int distance = d;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (distance >= 0 && wizardThread != null) {
					try {
						boolean facingExit = drive1Step2Exit();
						if(!facingExit) {
							robot.move(1);
							Log.v(TAG, "about to return true");
							setGetsToExit(true);
							Log.v(TAG, "gets to Exit: " + getsToExit);
							break;
						}
					}
					catch(Exception e){
						Log.v(TAG, "Error: Robot has run out of energy or crashed");
						wizardThread = null;
						lost = true;
					}
					Log.v(TAG, "Running thread inside run method");
					try{
						Thread.sleep(speed);
					}
					catch (InterruptedException e){
						System.out.println("Thread was interrupted");
					}
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("lost", lost);
					bundle.putInt(KEY, (int)robot.getBatteryLevel());
					message.setData(bundle);
					PlayAnimationActivity.myHandler.sendMessage(message);
				}
				Log.v(TAG, "Thread is done running");
			}
		};
		Log.v(TAG, "making new thread");
		wizardThread = new Thread(runnable);
		wizardThread.start();
	}


	/**
	 * Terminates the wizard thread and
	 * stops the driver from running
	 */
	@Override
	public void terminateThread(){
		if(wizardThread != null){
			wizardThread.interrupt();
			wizardThread = null;
		}
	}


	/**
	 * Drives the robot towards the exit following
	 * its solution strategy and given the exit exists and
	 * given the robot's energy supply lasts long enough.
	 * When the robot reached the exit position and its forward
	 * direction points to the exit the search terminates and
	 * the method returns true.
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * If the method determines that it is not capable of finding the
	 * exit it returns false, for instance, if it determines it runs
	 * in a cycle and can't resolve this.
	 * @return true if driver successfully reaches the exit, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive2Exit() throws Exception {
		try {
			Log.v(TAG, "inside drive2exit method in wizard");
			int[] currentPos = robot.getCurrentPosition();
			Log.v(TAG, "currentPos[0]: " + currentPos[0] + " currentPos[1]: " + currentPos[1]);
			int distance = mazeConfig.getDistanceToExit(currentPos[0], currentPos[1]);
			runThread(distance);
			return getsToExit;
		}
		catch(IndexOutOfBoundsException e) {
			Log.v(TAG, "Error: Current Position Not In Maze5");
			System.out.println("Error: Current Position Not In Maze5");
		}
		return false;
	}

	/**
	 * Drives the robot one step towards the exit following
	 * its solution strategy and given the exists and
	 * given the robot's energy supply lasts long enough.
	 * It returns true if the driver successfully moved
	 * the robot from its current location to an adjacent
	 * location.
	 * At the exit position, it rotates the robot
	 * such that if faces the exit in its forward direction
	 * and returns false.
	 * If the robot failed due to lack of energy or crashed, the method
	 * throws an exception.
	 * @return true if it moved the robot to an adjacent cell, false otherwise
	 * @throws Exception thrown if robot stopped due to some problem, e.g. lack of energy
	 */
	@Override
	public boolean drive1Step2Exit() throws Exception {
		try {
			Log.v(TAG, "inside drive1step2exit method in wizard");
			int[] currentPos = robot.getCurrentPosition();
			int[] neighbor = mazeConfig.getNeighborCloserToExit(currentPos[0], currentPos[1]) ;
			if (null == neighbor)
				return false;
			makeRobotFaceCorrectDirectionForNextMove(currentPos, neighbor);
			robot.move(1);
			if(robot.hasStopped()) {
				Log.v(TAG, "OH NO: Robot has run out of energy or crashed");
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			if(robot.isAtExit()) {
				try {
					currentPos = robot.getCurrentPosition();
					sensor = new ReliableSensor();
					sensor.setMaze(mazeConfig);
					batteryLevel1 = robot.getBatteryLevel();
					float[] batteryLevel = new float[]{batteryLevel1};
					while(sensor.distanceToObstacle(currentPos, robot.getCurrentDirection(), batteryLevel) != Integer.MAX_VALUE){
						robot.rotate(Turn.LEFT);
						if(robot.hasStopped()) {
							Log.v(TAG, "OH NO: Robot has run out of energy or crashed2");
							throw new Exception("OH NO: Robot has run out of energy or crashed");
						}
						batteryLevel[0] = robot.getBatteryLevel();
					}
					if(sensor.distanceToObstacle(currentPos, robot.getCurrentDirection(), batteryLevel) == Integer.MAX_VALUE) {
						return false;
					}
				}
				catch(IndexOutOfBoundsException e) {
					Log.v(TAG, "Error: Current Position Not In Maze");
					System.out.println("Error: Current Position Not In Maze");
				}
			}
		}
		catch(IndexOutOfBoundsException e){
			Log.v(TAG, "Error: Current Position Not In Maze2");
			System.out.println("Error: Current Position Not In Maze");
		}
		return true;
	}


	/**
	 * Turns the robot so that it faces the next spot in the
	 * solution to get to the exit, allowing the robot to
	 * move forward one space closer to the exit.
	 * @param currentPos position of the robot
	 * @param neighbor of the next step in the solution
	 */
	private void makeRobotFaceCorrectDirectionForNextMove(int[] currentPos, int[] neighbor) {
		switch(robot.getCurrentDirection()) {
			case East :
				if(currentPos[1] + 1 < mazeConfig.getHeight() && neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[1] - 1 >= 0 && neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[0] - 1 < mazeConfig.getWidth()&& neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case West :
				if(currentPos[1] + 1 < mazeConfig.getHeight() && neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[1] - 1 >= 0 && neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[0] + 1 < mazeConfig.getWidth()&& neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case North :
				if(currentPos[0] + 1 < mazeConfig.getWidth() && neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[0] - 1 >= 0 && neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[1] + 1 < mazeConfig.getHeight()&& neighbor[1] == currentPos[1] + 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
			case South :
				if(currentPos[0] + 1 < mazeConfig.getWidth() && neighbor[0] == currentPos[0] + 1) {
					robot.rotate(Turn.RIGHT);
				}
				else if(currentPos[0] - 1 >= 0 && neighbor[0] == currentPos[0] - 1) {
					robot.rotate(Turn.LEFT);
				}
				else if(currentPos[1] - 1 < mazeConfig.getHeight()&& neighbor[1] == currentPos[1] - 1) {
					robot.rotate(Turn.AROUND);
				}
				break;
		}
	}


	/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position.
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		return INITIAL_ENERGY - batteryLevel1;
	}

	/**
	 * Returns the total length of the journey in number of cells traversed.
	 * Being at the initial position counts as 0.
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		return robot.getOdometerReading();
	}

	private void setGetsToExit(boolean atExit){
		getsToExit = atExit;
	}

	/**
	 * Sets the animation speed for the driver
	 * @param speed
	 */
	public void setAnimationSpeed(int speed){
		this.speed = speedOptions[speed];
	}

}
