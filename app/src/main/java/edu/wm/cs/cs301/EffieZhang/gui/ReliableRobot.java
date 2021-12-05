package edu.wm.cs.cs301.EffieZhang.gui;

//import static org.junit.Assert.assertEquals;
import android.util.Log;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Wallboard;
import edu.wm.cs.cs301.EffieZhang.gui.Constants.UserInput;
/**
 * A reliable robot interacts with the game controller to perform operations like turn and
 * move. The robot has four sensor to sensor objects. It is configured by mounting distance sensors to a robot such that it can
 * measure the distance to an obstacle, a wall, in the direction that sensor
 * has been mounted on the robot.
 * It provides an operating platform for a robot driver that experiences a maze (the real world) 
 * through the sensors and actuators of this robot interface.
 *
 * 
 * Collaborators: a controller that holds a maze to be explored, 
 * ReliableSensor
 * 
 * @author Effie Zhang
 *
 */
public class ReliableRobot implements Robot{
	private static final String TAG = "ReliableRobot";  //string message key
	private StatePlaying statePlaying;
	private PlayAnimationActivity playAnimationActivity;
	private ReliableSensor sensor;
	private float batteryLevel;
	private int odometer = 0;
	private boolean hasStopped = false;
	private static final int ROTATE_90_ENERGY = 3;
	private static final int MOVE1_ENERGY = 6;
	private static final int JUMP_ENERGY = 40;
	private static final int INITIAL_ENERGY = 3500;

	public ReliableRobot() {
		batteryLevel = INITIAL_ENERGY;
		sensor = new ReliableSensor();
	}

	/**
	 * Provides the robot with a reference to the controller to cooperate with.
	 * The robot memorizes the controller such that this method is most likely called only once
	 * and for initialization purposes. The controller serves as the main source of information
	 * for the robot about the current position, the presence of walls, the reaching of an exit.
	 * The controller is assumed to be in the playing state.
	 * @param statePlaying is the communication partner for robot
	 * @throws IllegalArgumentException if controller is null,
	 * or if controller is not in playing state,
	 * or if controller does not have a maze
	 */
	@Override
	public void setStatePlaying(StatePlaying statePlaying) {
		if(statePlaying == null || GeneratingActivity.mazeConfig == null) {
			throw new IllegalArgumentException();
		}
		this.statePlaying = statePlaying;
	}

	public void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity){
		this.playAnimationActivity = playAnimationActivity;
	}

	/**
	 * Provides the current position as (x,y) coordinates for
	 * the maze as an array of length 2 with [x,y].
	 * @return array of length 2, x = array[0], y = array[1]
	 * and ({@code 0 <= x < width, 0 <= y < height}) of the maze
	 * @throws Exception if position is outside of the maze
	 */
	@Override
	public int[] getCurrentPosition() throws Exception {
		int[] currentPosition = statePlaying.getCurrentPosition();
		if(currentPosition[0] >= 0 && currentPosition[0] < GeneratingActivity.mazeConfig.getWidth() && currentPosition[1] >= 0 && currentPosition[1] < GeneratingActivity.mazeConfig.getHeight()) {
			return currentPosition;
		}
		else {
			Log.v(TAG, "ReliableRobot.getCurrentPosition: Out of Bounds Exception");
			System.out.println("ReliableRobot.getCurrentPosition: Out of Bounds Exception");
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Provides the robot's current direction.
	 * @return cardinal direction is the robot's current direction in absolute terms
	 */
	@Override
	public CardinalDirection getCurrentDirection() {
		return statePlaying.getCurrentDirection();
	}

	/**
	 * Returns the current battery level.
	 * The robot has a given battery level (energy level)
	 * that it draws energy from during operations.
	 * The particular energy consumption is device dependent such that a call
	 * for sensor distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @return current battery level, {@code level > 0} if operational.
	 */
	@Override
	public float getBatteryLevel() {
		return batteryLevel;
	}

	/**
	 * Sets the current battery level.
	 * The robot has a given battery level (energy level)
	 * that it draws energy from during operations.
	 * The particular energy consumption is device dependent such that a call
	 * for distance2Obstacle may use less energy than a move forward operation.
	 * If battery {@code level <= 0} then robot stops to function and hasStopped() is true.
	 * @param level is the current battery level
	 * @throws IllegalArgumentException if level is negative
	 */
	@Override
	public void setBatteryLevel(float level) {
		if(level <= 0) {
			Log.v(TAG, "ReliableRobot.setBatteryLevel: Illegal Argument Exception");
			System.out.println("ReliableRobot.setBatteryLevel: Illegal Argument Exception");
			throw new IllegalArgumentException();
		}
		batteryLevel = level;

	}

	/**
	 * Gives the energy consumption for a full 360 degree rotation.
	 * Scaling by other degrees approximates the corresponding consumption.
	 * @return energy for a full rotation
	 */
	@Override
	public float getEnergyForFullRotation() {
		return ROTATE_90_ENERGY * 4;
	}

	/**
	 * Gives the energy consumption for moving forward for a distance of 1 step.
	 * For simplicity, we assume that this equals the energy necessary
	 * to move 1 step backwards and that scaling by a larger number of moves is
	 * approximately the corresponding multiple.
	 * @return energy for a single step forward
	 */
	@Override
	public float getEnergyForStepForward() {
		return MOVE1_ENERGY;
	}

	/**
	 * Gets the distance traveled by the robot.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance
	 * that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 * The counter can be reset to 0 with resetOdomoter().
	 * @return the distance traveled measured in single-cell steps forward
	 */
	@Override
	public int getOdometerReading() {
		return odometer;
	}

	/**
	 * Resets the odometer counter to zero.
	 * The robot has an odometer that calculates the distance the robot has moved.
	 * Whenever the robot moves forward, the distance
	 * that it moves is added to the odometer counter.
	 * The odometer reading gives the path length if its setting is 0 at the start of the game.
	 */
	@Override
	public void resetOdometer() {
		odometer = 0;

	}

	/**
	 * Turn robot on the spot for amount of degrees.
	 * If robot runs out of energy, it stops,
	 * which can be checked by hasStopped() == true and by checking the battery level.
	 * @param turn is the direction to turn and relative to current forward direction.
	 */
	@Override
	public void rotate(Turn turn) {
		if(batteryLevel < ROTATE_90_ENERGY) {
			hasStopped = true;
		}
		else if(turn == Turn.LEFT) {
			statePlaying.keyDown(UserInput.LEFT, 1);
			batteryLevel -= ROTATE_90_ENERGY;
		}
		else if(turn == Turn.RIGHT) {
			statePlaying.keyDown(UserInput.RIGHT, 1);
			batteryLevel -= ROTATE_90_ENERGY;
		}
		else if(turn == Turn.AROUND) {
			statePlaying.keyDown(UserInput.RIGHT, 1);
			statePlaying.keyDown(UserInput.RIGHT, 1);
			batteryLevel -= ROTATE_90_ENERGY * 2;
		}

	}

	/**
	 * Moves robot forward a given number of steps. A step matches a single cell.
	 * If the robot runs out of energy somewhere on its way, it stops,
	 * which can be checked by hasStopped() == true and by checking the battery level.
	 * If the robot hits an obstacle like a wall, it remains at the position in front
	 * of the obstacle and also hasStopped() == true as this is not supposed to happen.
	 * This is also helpful to recognize if the robot implementation and the actual maze
	 * do not share a consistent view on where walls are and where not.
	 * @param distance is the number of cells to move in the robot's current forward direction
	 * @throws IllegalArgumentException if distance not positive
	 */
	@Override
	public void move(int distance) {
		int[] currentPos = new int[2];
		try {
			currentPos = getCurrentPosition();
		}
		catch(Exception E) {
			Log.v(TAG, "Current Position not in maze");
			System.out.println("Current Position not in maze");
		}
		if(distance < 0) {
			Log.v(TAG, "ReliableRobot.move: Illegal Argument Exception");
			System.out.println("ReliableRobot.move: Illegal Argument Exception");
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < distance; i++) {
			if(batteryLevel < MOVE1_ENERGY) {
				hasStopped = true;
				break;
			}
			if(GeneratingActivity.mazeConfig.hasWall(currentPos[0], currentPos[1], getCurrentDirection())) {
				hasStopped = true;
				break;
			}
			odometer++;
			statePlaying.keyDown(UserInput.UP, 1);
			batteryLevel -= MOVE1_ENERGY;
		}
	}

	/**
	 * Makes robot move in a forward direction even if there is a wall
	 * in front of it. In this sense, the robot jumps over the wall
	 * if necessary. The distance is always 1 step and the direction
	 * is always forward.
	 * If the robot runs out of energy somewhere on its way, it stops,
	 * which can be checked by hasStopped() == true and by checking the battery level.
	 * If the robot tries to jump over an exterior wall and
	 * would land outside of the maze that way,
	 * it remains at its current location and direction,
	 * hasStopped() == true as this is not supposed to happen.
	 */
	@Override
	public void jump() {
		try {
			int[] currentPos = getCurrentPosition();
			if(currentPos[0]+1 < GeneratingActivity.mazeConfig.getWidth() && currentPos[1]+1 < GeneratingActivity.mazeConfig.getHeight()) {
				if(batteryLevel > JUMP_ENERGY) {
					statePlaying.keyDown(UserInput.JUMP, 1);
					odometer++;
					batteryLevel -= JUMP_ENERGY;
				}
				else {
					hasStopped = true;
				}
			}
			else {
				hasStopped = true;
			}
		}
		catch(Exception e){
			Log.v(TAG, "Error: Current Position Not In Maze1");
			System.out.println("Error: Current Position Not In Maze1");
		}
	}

	/**
	 * Tells if the current position is right at the exit but still inside the maze.
	 * The exit can be in any direction. It is not guaranteed that
	 * the robot is facing the exit in a forward direction.
	 * @return true if robot is at the exit, false otherwise
	 */
	@Override
	public boolean isAtExit() {
		try {
			int[] currentPos = getCurrentPosition();
			if(GeneratingActivity.mazeConfig.getFloorplan().isExitPosition(currentPos[0], currentPos[1])) {
				return true;
			}
		}
		catch(Exception e) {
			Log.v(TAG, "Error: Current Position Not In Maze2");
			System.out.println("Error: Current Position Not In Maze2");
		}
		return false;
	}

	/**
	 * Tells if current position is inside a room.
	 * @return true if robot is inside a room, false otherwise
	 */
	@Override
	public boolean isInsideRoom() {
		try {
			int[] currentPos = getCurrentPosition();
			if(GeneratingActivity.mazeConfig.getFloorplan().isInRoom(currentPos[0], currentPos[1])) {
				return true;
			}
		}
		catch(Exception e) {
			Log.v(TAG, "Error: Current Position Not In Maze3");
			System.out.println("Error: Current Position Not In Maze3");
		}
		return false;
	}

	/**
	 * Tells if the robot has stopped for reasons like lack of energy,
	 * hitting an obstacle, etc.
	 * Once a robot is has stopped, it does not rotate or
	 * move anymore.
	 * @return true if the robot has stopped, false otherwise
	 */
	@Override
	public boolean hasStopped() {
		if(batteryLevel == 0) {
			return true;
		}
		if(hasStopped) {
			return true;
		}
		return false;
	}

	/**
	 * Tells the distance to an obstacle (a wall)
	 * in the given direction.
	 * The direction is relative to the robot's current forward direction.
	 * Distance is measured in the number of cells towards that obstacle,
	 * e.g. 0 if the current cell has a wallboard in this direction,
	 * 1 if it is one step forward before directly facing a wallboard,
	 * Integer.MaxValue if one looks through the exit into eternity.
	 * @param direction specifies the direction of the sensor
	 * @return number of steps towards obstacle if obstacle is visible
	 * in a straight line of sight, Integer.MAX_VALUE otherwise
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public int distanceToObstacle(Direction direction) throws UnsupportedOperationException {
		sensor.setSensorDirection(direction);
		try {
			int[] currentPos = getCurrentPosition();
			float[] tempBatteryLevel = new float[] {batteryLevel};
			return sensor.distanceToObstacle(currentPos, getCurrentDirection(), tempBatteryLevel);
		}
		catch(Exception e) {
			Log.v(TAG, "Error: Current Position Not In Maze4");
			System.out.println("Error: Current Position Not In Maze4");
		}

		return 0;
	}

	/**
	 * Tells if a sensor can identify the exit in the given direction relative to
	 * the robot's current forward direction from the current position.
	 * @param direction is the direction of the sensor
	 * @return true if the exit of the maze is visible in a straight line of sight
	 * @throws UnsupportedOperationException if robot has no sensor in this direction
	 * or the sensor exists but is currently not operational
	 */
	@Override
	public boolean canSeeThroughTheExitIntoEternity(Direction direction) throws UnsupportedOperationException {
		sensor.setSensorDirection(direction);
		try {
			int[] currentPos = getCurrentPosition();
			float[] tempBatteryLevel = new float[] {batteryLevel};
			if(sensor.distanceToObstacle(currentPos, getCurrentDirection(), tempBatteryLevel) == Integer.MAX_VALUE) {
				return true;
			}
		}
		catch(Exception e) {
			Log.v(TAG, "Error: Current Position Not In Maze5");
			System.out.println("Error: Current Position Not In Maze5");
		}
		return false;
	}

	/**
	 * Method starts a concurrent, independent failure and repair
	 * process that makes the sensor fail and repair itself.
	 * This creates alternating time periods of up time and down time.
	 * Up time: The duration of a time period when the sensor is in
	 * operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeBetweenFailures.
	 * Down time: The duration of a time period when the sensor is in repair
	 * and not operational is characterized by a distribution
	 * whose mean value is given by parameter meanTimeToRepair.
	 *
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 *
	 * @param direction the direction the sensor is mounted on the robot
	 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
	 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void startFailureAndRepairProcess(Direction direction, int meanTimeBetweenFailures, int meanTimeToRepair)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();

	}

	/**
	 * This method stops a failure and repair process and
	 * leaves the sensor in an operational state.
	 *
	 * It is complementary to starting a
	 * failure and repair process.
	 *
	 * Intended use: If called after starting a process, this method
	 * will stop the process as soon as the sensor is operational.
	 *
	 * If called with no running failure and repair process,
	 * the method will return an UnsupportedOperationException.
	 *
	 * This an optional operation. If not implemented, the method
	 * throws an UnsupportedOperationException.
	 *
	 * @param direction the direction the sensor is mounted on the robot
	 * @throws UnsupportedOperationException if method not supported
	 */
	@Override
	public void stopFailureAndRepairProcess(Direction direction) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();

	}

}
