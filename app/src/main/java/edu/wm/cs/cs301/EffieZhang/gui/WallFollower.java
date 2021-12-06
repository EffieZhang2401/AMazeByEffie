package edu.wm.cs.cs301.EffieZhang.gui;

//import static org.junit.Assert.assertEquals;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Turn;

/**
 * This class specifies a robot driver--Wallfollower--that operates a robot to escape from a given maze. 
 * Collaborators: UnreliableRobot
 * 
 * @author Effie Zhang
 *
 */
public class WallFollower implements RobotDriver {
	private Robot rob;
	private Maze m;
	private float energy_consumed;
	private int path_length;

	/**
	 * constructor for wallfollower.
	 */
	public WallFollower() {
		// nothing necessary
	}

	@Override
	public void setRobot(Robot r) {
		rob = new UnreliableRobot();
		rob = r;
		energy_consumed = 0;
		path_length = 0;

	}
	
	/**
	 * return the robot 
	 * @return robot
	 */
	public Robot getRobot() {
		return rob;
	}

	@Override
	public void setMaze(Maze maze) {
		m = maze;
	}
	
	/**
	 * return the maze
	 * @return maze
	 */
	public Maze getMaze() {
		return m;
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
		while(!rob.isAtExit()) {
			if(!drive1Step2Exit())
				return false;
		
		}
		// moving into the exit
				if (rob.distanceToObstacle(Direction.FORWARD)==Integer.MAX_VALUE) {
					if(rob.getBatteryLevel() < 6) {
						throw new Exception("robot lack of energy");
					}
					rob.move(1);
					path_length++;
				}
				else if (rob.distanceToObstacle(Direction.LEFT)==Integer.MAX_VALUE) {
					if(rob.getBatteryLevel() < 9) {
						throw new Exception("robot lack of energy");
					}
					rob.rotate(Turn.LEFT);
					rob.move(1);
					path_length++;
				}
				else if (rob.distanceToObstacle(Direction.RIGHT)==Integer.MAX_VALUE) {
					if(rob.getBatteryLevel() < 9) {
						throw new Exception("robot lack of energy");
					}
					rob.rotate(Turn.RIGHT);
					rob.move(1);
					path_length++;
				}
				return true;
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
		if(rob.getBatteryLevel() == 0) {
				throw new Exception("lack of energy");
		}
		int leftD = 0;//the left distance to obstacle
		int forwardD = 0;//the forward distance to obstacle
		try {
			leftD = rob.distanceToObstacle(Direction.LEFT);
			
		}
		catch(UnsupportedOperationException e) {
			//if the sensor is not working, use Strategy 1(plan 1)
			//rotate to the working sensor and rotate back 
			leftD = planAdistance(Direction.LEFT);
			}
		try {
			forwardD = rob.distanceToObstacle(Direction.FORWARD);
			
		}
		catch(UnsupportedOperationException e) {
			forwardD = planAdistance(Direction.FORWARD);
			}
		// if there is a wall left, check for other walls
		if (leftD == 0) {
			// if there is a wall forward, return right
			if (forwardD == 0) {
				rob.rotate(Turn.RIGHT);
				return true;
			}
		// if no wall forward, move forward
			else {
							
				rob.move(1);
				path_length++;
				return true;
			}
		}
		// if no wall left, rotate left and move forward
		else {
			rob.rotate(Turn.LEFT);
			rob.move(1);
			path_length++;
			return true;
		}
		}
	
	/**
	 * Solve the problem with the sensor is not working by using the planA solution
	 * @param //Direction direct
	 * @return
	 */
	protected int planAdistance(Direction direct) {
		// if sensor did not work, identify a working sensor
		Direction dir = identifyWorkingSensor();
		// calculate number of left rotations needed to move sensor to sensing direction
		int numRotations = relateDirections(direct, dir);
		int distance = 0;	
		// sense, rotate back to original position, then return recorded distance
		if (numRotations == 1) {
			rob.rotate(Turn.LEFT);
			distance = rob.distanceToObstacle(dir);
			rob.rotate(Turn.RIGHT);
			return distance;
		}
		else if (numRotations == 2) {
			rob.rotate(Turn.AROUND);
			distance = rob.distanceToObstacle(dir);
			rob.rotate(Turn.AROUND);
			return distance;
		}
		else {
			rob.rotate(Turn.RIGHT);
			distance = rob.distanceToObstacle(dir);
			rob.rotate(Turn.LEFT);
			return distance;
		}
	}
	
	/**
	 * return the rotate times by calculating the relate direction
	 * @param sensingDir direction that needs to be sensed
	 * @param sensorDir direction of working sensor
	 * @return number of times robot need to rotate left to get sensorDir to sensingDir
	 */
	private int relateDirections(Direction sensingDir, Direction sensorDir) {
		if (sensorDir == Direction.FORWARD) {
			if (sensingDir == Direction.FORWARD)
				return 0;
			else if (sensingDir == Direction.LEFT)
				return 1;
			else if (sensingDir == Direction.BACKWARD)
				return 2;
			else
				return 3;
		}
		else if (sensorDir == Direction.LEFT) {
			if (sensingDir == Direction.LEFT)
				return 0;
			else if (sensingDir == Direction.BACKWARD)
				return 1;
			else if (sensingDir == Direction.RIGHT)
				return 2;
			else
				return 3;
		}
		else if (sensorDir == Direction.BACKWARD) {
			if (sensingDir == Direction.BACKWARD)
				return 0;
			else if (sensingDir == Direction.RIGHT)
				return 1;
			else if (sensingDir == Direction.FORWARD)
				return 2;
			else
				return 3;
		}
		else {
			if (sensingDir == Direction.RIGHT)
				return 0;
			else if (sensingDir == Direction.FORWARD)
				return 1;
			else if (sensingDir == Direction.LEFT)
				return 2;
			else
				return 3;
		}
	}
	
	/**
	 * goes through directions and identify working sensor. If there is no working sensor,
	 * sleep for 0.3s
	 * if sensor in direction is reliable or operational, return direction
	 * @return direction of working sensor
	 */
	private Direction identifyWorkingSensor() {
		for (Direction dir: Direction.values()) {
			try {
				rob.distanceToObstacle(dir);
				return dir;
			}
			catch(UnsupportedOperationException e) {};
		}
		try {
			// Strategy B: wait until a working sensor becomes available
			Thread.sleep(300);
		} catch (InterruptedException e) {};
		return identifyWorkingSensor();
	}

	@Override
	public float getEnergyConsumption() {
		return 3500-rob.getBatteryLevel();
	}

	@Override
	public int getPathLength() {
		return rob.getOdometerReading();
	}

	@Override
	public void setAnimationSpeed(int speed) {

	}

	@Override
	public void terminateThread() {

	}


}