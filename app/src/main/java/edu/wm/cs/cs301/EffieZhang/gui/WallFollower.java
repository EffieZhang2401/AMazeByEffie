package edu.wm.cs.cs301.EffieZhang.gui;

//import static org.junit.Assert.assertEquals;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Turn;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class specifies a robot driver--Wallfollower--that operates a robot to escape from a given maze. 
 * Collaborators: UnreliableRobot
 * 
 * @author Effie Zhang
 *
 */
public class WallFollower extends Wizard implements RobotDriver {
	private Robot robot;
	private static final String KEY = "my message key";  //message key
	private Maze mazeConfig;
	private static final int INITIAL_ENERGY = 3500;
	private boolean failedAtLeft = false;
	private static final String TAG = "WallFollower";  //message key
	private Thread wallFollowerThread;
	private int speed = 400;
	private int[] speedOptions;
	private boolean lost;
	public static Handler wallFollowerHandler;

	/**
	 * Constructs a new Wall Follower object with
	 * the boolean value lost set too false, since the
	 * driver has not lost yet, and initializes array
	 * speedOptions with the possible animation speeds
	 */
	public WallFollower() {
		speedOptions = new int[]{2000, 1500, 1000, 900, 800, 700, 600, 500, 450, 400, 350, 300, 250, 200, 150, 100, 50, 25, 10, 5, 1};
		lost = false;
	}


	/**
	 * Sets the robot for the driver to push through the maze
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		this.robot = r;

	}

	/**
	 * Sets the maze that the driver is going to push the robot through
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
	public void runThread(int d){
		final int distance = d;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (distance >= 0 && wallFollowerThread != null) {
					try {
						boolean facingExit = drive1Step2Exit();
						if (robot.isAtExit() && !facingExit) {
							robot.move(1);
							stopFailureAndRepairThreads();
							break;
						}
					} catch (Exception e) {
						Log.v(TAG, "Error: Robot has run out of energy or crashed");
						wallFollowerThread = null;
						stopFailureAndRepairThreads();
						lost = true;
					}
					Log.v(TAG, "Running thread inside run method");
					try {
						Thread.sleep(speed);
					} catch (InterruptedException e) {
						System.out.println("Thread was interrupted");
					}
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putBoolean("lost", lost);
					bundle.putInt(KEY, (int) robot.getBatteryLevel());
					message.setData(bundle);
					PlayAnimationActivity.myHandler.sendMessage(message);
				}
				Log.v(TAG, "Thread is done running");
			}
		};
		Log.v(TAG, "making new thread");
		wallFollowerThread = new Thread(runnable);
		wallFollowerThread.start();
	}

	/**
	 * Terminates the wallFollower thread and
	 * stops the driver from running
	 */
	@Override
	public void terminateThread(){
		if(wallFollowerThread != null){
			wallFollowerThread.interrupt();
			wallFollowerThread = null;
			stopFailureAndRepairThreads();
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
			int[] currentPos = robot.getCurrentPosition();
			int distance = mazeConfig.getDistanceToExit(currentPos[0], currentPos[1]);
			runThread(distance);
		}
		catch(IndexOutOfBoundsException e) {
			System.out.println("Error: Current Position Not In Maze5");
		}
		return false;
	}


	/**
	 * Calls the stopFailureAndRepairProcess in UnreliableSensor
	 * if the sensor is unreliable in order to stop each sensor's
	 * thread. Otherwise, this method catches an exception from
	 * ReliableSensor if the sensor is a reliable sensor.
	 */
	private void stopFailureAndRepairThreads() {
		try {
			robot.stopFailureAndRepairProcess(Direction.BACKWARD);
		}
		catch(UnsupportedOperationException e) {}
		try {
			robot.stopFailureAndRepairProcess(Direction.FORWARD);
		}
		catch(UnsupportedOperationException e) {}
		try {
			robot.stopFailureAndRepairProcess(Direction.LEFT);
		}
		catch(UnsupportedOperationException e) {}
		try {
			robot.stopFailureAndRepairProcess(Direction.RIGHT);
		}
		catch(UnsupportedOperationException e) {}
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
			if(robot.isAtExit()) {
				while(robot.distanceToObstacle(Direction.FORWARD) != Integer.MAX_VALUE){

					robot.rotate(Turn.LEFT);
					if(robot.hasStopped()) {
						throw new Exception("OH NO: Robot has run out of energy or crashed");
					}
				}
				if(robot.distanceToObstacle(Direction.FORWARD) == Integer.MAX_VALUE) {
					return false;
				}
			}
			try {
				failedAtLeft = true;
				if(robot.distanceToObstacle(Direction.LEFT) > 0) {
					failedAtLeft = false;
					robot.rotate(Turn.LEFT);
					robot.move(1);
					if(robot.hasStopped()) {
						throw new Exception("OH NO: Robot has run out of energy or crashed");
					}
					return true;
				}
				else if(robot.distanceToObstacle(Direction.FORWARD) > 0){
					robot.move(1);
					if(robot.hasStopped()) {
						throw new Exception("OH NO: Robot has run out of energy or crashed");
					}
					return true;
				}
				else {
					robot.rotate(Turn.RIGHT);
					if(robot.hasStopped()) {
						throw new Exception("OH NO: Robot has run out of energy or crashed");
					}
				}
				if(robot.hasStopped()) {
					throw new Exception();
				}
			}
			catch(IndexOutOfBoundsException e) {
				System.out.println("WallFollower.java Error: Ran out of energy");
			}
			catch(IllegalArgumentException e) {
				System.out.println("WallFollower.java Error: Passed an illegal argument");
			}
			catch(UnsupportedOperationException e) {
				try {
					return planA();
				}
				catch(Exception ex) {
					System.out.println("WallFollower.java Error: Ran out of energy or crashed into a wall");
					throw new Exception("Power Failure or Crashed into wall");
				}
			}

		}
		catch(IndexOutOfBoundsException e) {
			System.out.println("WallFollower.java Error: Ran out of energy");
		}
		catch(IllegalArgumentException e) {
			System.out.println("WallFollower.java Error: Passed an illegal argument");
		}
		catch(UnsupportedOperationException e) {
			try {
				return planA();
			}
			catch(Exception ex) {
				System.out.println("WallFollower.java Error: Ran out of energy or crashed into a wall");
				throw new Exception("Power Failure or Crashed into wall");
			}
		}
		return false;
	}/**
	 * Returns the total energy consumption of the journey, i.e.,
	 * the difference between the robot's initial energy level at
	 * the starting position and its energy level at the exit position.
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total energy consumption of the journey
	 */
	@Override
	public float getEnergyConsumption() {
		return INITIAL_ENERGY - robot.getBatteryLevel();
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


	/**
	 * This method substitutes a failed sensor at runtime with a working one by
	 * identifying a working sensor, rotating a robot to bring the working
	 * sensor into position, measuring the distance with the working sensor, and rotating the
	 * robot back to previous orientation such that the driver can proceed as if the
	 * distance were measured with the failed sensor
	 *
	 * @return
	 * @throws Exception
	 */
	private boolean planA() throws Exception {
		int leftDistance;
		int frontDistance;
		Log.v(TAG, "inside PLAN A");
		if(failedAtLeft) {
			try {
				leftDistance = getPlanALeftFailed();
				if(leftDistance == -1) {
					planB();
					return false;
				}
			}
			catch(Exception ex) {
				System.out.println("Power Failure");
				throw new Exception("Power Failure");
			}
			try {
				frontDistance = robot.distanceToObstacle(Direction.FORWARD);
			}
			catch(Exception e) {
				frontDistance = getPlanAFrontFailed();
				if(frontDistance == -1) {
					planB();
					return false;
				}
			}
		}
		else {
			try {
				leftDistance = robot.distanceToObstacle(Direction.LEFT);
				frontDistance = getPlanAFrontFailed();
				if(leftDistance == -1) {
					planB();
					return false;
				}
			}
			catch(Exception ex){
				System.out.println("Power Failure");
				throw new Exception("Power Failure or failed sensor");
			}
		}
		if(robot.isAtExit()) {
			while(frontDistance != Integer.MAX_VALUE){
				robot.rotate(Turn.LEFT);
				if(robot.hasStopped()) {
					throw new Exception("OH NO: Robot has run out of energy or crashed");
				}
				try {
					frontDistance = robot.distanceToObstacle(Direction.FORWARD);
				}
				catch(Exception e) {
					frontDistance = getPlanAFrontFailed();
					if(frontDistance == -1) {
						planB();
						return false;
					}
				}
			}
			if(frontDistance == Integer.MAX_VALUE) {
				return false;
			}
		}
		else {
			try {
				return drive1Step2ExitPlanA(leftDistance, frontDistance);
			}
			catch(Exception e) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
		}
		return false;
	}


	/**
	 * This method takes the distance to obstacle values for the left
	 * and front sensors found by using plan A and
	 * runs those values through the drive1Step2Exit algorithm
	 * for WallFollower.
	 *
	 * @param leftDistance sensor's distance to an obstacle
	 * @param frontDistance sensor's distance to an obstacle
	 * @return true if robot moves a step forwards and false
	 * if robot does not move forwards
	 * @throws Exception if the robot runs out or energy or crashes into a wall
	 */
	private boolean drive1Step2ExitPlanA(int leftDistance, int frontDistance) throws Exception {
		if(leftDistance > 0) {
			robot.rotate(Turn.LEFT);
			robot.move(1);
			if(robot.hasStopped()) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			return true;
		}
		else if(frontDistance > 0){
			robot.move(1);
			if(robot.hasStopped()) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			return true;
		}
		else {
			robot.rotate(Turn.RIGHT);
			if(robot.hasStopped()) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			return false;
		}
	}


	/**
	 * This method waits until a sensor is operational,
	 * then calls drive1Step2Exit
	 */
	private void planB() {
		try {
			Thread.sleep(2000);
		}
		catch(Exception e) {}
	}


	/**
	 * This method finds the distance to obstacle value for the left
	 * sensor when the left sensor has failed by trying to use its
	 * working sensors to get the distance, but returns -1
	 * if none of the robot's sensors are operational at the time.
	 *
	 * @return distance to obstacle based on left sensor
	 * if robot does not move forwards
	 * @throws Exception if the robot runs out or energy or crashes into a wall
	 */
	private int getPlanALeftFailed() throws Exception {
		int rotate = 0;
		int tempDistance = -1;
		int finishRotate = 4;
		Direction[] direction = new Direction[] {Direction.LEFT, Direction.FORWARD, Direction.RIGHT, Direction.BACKWARD};
		while(rotate < finishRotate) {
			robot.rotate(Turn.LEFT);
			if(robot.hasStopped()) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			rotate++;
			try {
				tempDistance = robot.distanceToObstacle(direction[rotate]);
				switch(direction[rotate]) {
					case FORWARD:
						robot.rotate(Turn.RIGHT);
						break;
					case RIGHT:
						robot.rotate(Turn.AROUND);
						break;
					case BACKWARD:
						robot.rotate(Turn.LEFT);
						break;
					case LEFT:
						break;
				}
				if(robot.hasStopped()) {
					throw new Exception("OH NO: Robot has run out of energy or crashed");
				}
				return tempDistance;
			}
			catch(Exception e) {continue;}
		}
		return tempDistance;
	}


	/**
	 * This method finds the distance to obstacle value for the front
	 * sensor when the left sensor has failed by trying to use its
	 * working sensors to get the distance, but returns -1
	 * if none of the robot's sensors are operational at the time.
	 *
	 * @return distance to obstacle based on front sensor
	 * if robot does not move forwards
	 * @throws Exception if the robot runs out or energy or crashes into a wall
	 */
	private int getPlanAFrontFailed() throws Exception {
		int rotate = 0;
		int tempDistance = -1;
		int finishRotate = 4;
		Direction[] direction = new Direction[] {Direction.FORWARD, Direction.RIGHT, Direction.BACKWARD, Direction.LEFT};
		while(rotate < finishRotate) {
			robot.rotate(Turn.LEFT);
			if(robot.hasStopped()) {
				throw new Exception("OH NO: Robot has run out of energy or crashed");
			}
			rotate++;
			try {
				tempDistance = robot.distanceToObstacle(direction[rotate]);
				switch(direction[rotate]) {
					case FORWARD:
						break;
					case RIGHT:
						robot.rotate(Turn.RIGHT);
						break;
					case BACKWARD:
						robot.rotate(Turn.AROUND);
						break;
					case LEFT:
						robot.rotate(Turn.LEFT);
						break;
				}
				if(robot.hasStopped()) {
					throw new Exception("OH NO: Robot has run out of energy or crashed");
				}
				return tempDistance;
			}
			catch(Exception e) {continue;}
		}
		return tempDistance;
	}


	/**
	 * Sets the animation speed for the driver
	 * @param speed
	 */
	@Override
	public void setAnimationSpeed(int speed){
		this.speed = speedOptions[speed];
	}
}