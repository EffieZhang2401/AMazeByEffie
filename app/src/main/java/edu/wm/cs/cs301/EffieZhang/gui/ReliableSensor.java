package edu.wm.cs.cs301.EffieZhang.gui;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;

/**
 *  This class provides information how for it is to a wall for a given position and in a specific direction.
 * 	The sensor is assumed to be mounted at a particular angle relative to the forward direction of the robot.
 * 
 *  This ReliableSensor class can translate the current position and current forward direction
 *  into the current direction in terms of north, south, east, or west. Then it can interact with 
 *  floorplan to get the distance from a wall.
 * 
 * Collaborators: ReliableRobot
 * 
 * Implementing classes: UnreliableSensor
 * 
 * @author Effie Zhang
 *
 */

public class ReliableSensor implements DistanceSensor{

		private Maze mazeConfig;
		private Direction sensorDirection;
		private static final int SENSING_ENERGY = 1;

		public ReliableSensor() {
			sensorDirection = Direction.FORWARD;
		}

		/**
		 * Tells the distance to an obstacle (a wallboard) that the sensor
		 * measures. The sensor is assumed to be mounted in a particular
		 * direction relative to the forward direction of the robot.
		 * Distance is measured in the number of cells towards that obstacle,
		 * e.g. 0 if the current cell has a wallboard in this direction,
		 * 1 if it is one step in this direction before directly facing a wallboard,
		 * Integer.MaxValue if one looks through the exit into eternity.
		 *
		 * This method requires that the sensor has been given a reference
		 * to the current maze and a mountedDirection by calling
		 * the corresponding set methods with a parameterized constructor.
		 *
		 * @param currentPosition is the current location as (x,y) coordinates
		 * @param currentDirection specifies the direction of the robot
		 * @param powersupply is an array of length 1, whose content is modified
		 * to account for the power consumption for sensing
		 * @return number of steps towards obstacle if obstacle is visible
		 * in a straight line of sight, Integer.MAX_VALUE otherwise.
		 * @throws Exception with message
		 * SensorFailure if the sensor is currently not operational
		 * PowerFailure if the power supply is insufficient for the operation
		 * @throws IllegalArgumentException if any parameter is null
		 * or if currentPosition is outside of legal range
		 * ({@code currentPosition[0] < 0 || currentPosition[0] >= width})
		 * ({@code currentPosition[1] < 0 || currentPosition[1] >= height})
		 * @throws IndexOutOfBoundsException if the powersupply is out of range
		 * ({@code powersupply[0] < 0})
		 */
		@Override
		public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply)
				throws Exception {
			if(currentPosition == null || currentDirection == null || powersupply == null || !mazeConfig.isValidPosition(currentPosition[0], currentPosition[1])) {
				System.out.println("ReliableSensor.distanceToObstace: Illegal Argument Exception");
				throw new IllegalArgumentException("Illegal Argument");
			}
			if(powersupply[0] < SENSING_ENERGY) {
				throw new IndexOutOfBoundsException("PowerFailure");
			}
			int px = currentPosition[0];
			int py = currentPosition[1];
			int distance = 0;
			powersupply[0] -= SENSING_ENERGY;
			CardinalDirection newDir = getCurrentSensorDirection(currentDirection);
			switch(newDir) {
				case North :
					while(!mazeConfig.hasWall(px, py, newDir)) {
						if(mazeConfig.getFloorplan().isExitPosition(px, py)) {
							if(!mazeConfig.isValidPosition(px, py - 1)) {
								return Integer.MAX_VALUE;
							}
						}
						py--;
						distance++;
					}
					return distance;
				case South :
					while(!mazeConfig.hasWall(px, py, newDir)) {
						if(mazeConfig.getFloorplan().isExitPosition(px, py)) {
							if(!mazeConfig.isValidPosition(px, py + 1)) {
								return Integer.MAX_VALUE;
							}
						}
						py++;
						distance++;
					}
					return distance;
				case West :
					while(!mazeConfig.hasWall(px, py, newDir)) {
						if(mazeConfig.getFloorplan().isExitPosition(px, py)) {
							if(!mazeConfig.isValidPosition(px-1, py)) {
								return Integer.MAX_VALUE;
							}
						}
						px--;
						distance++;
					}
					return distance;
				case East :
					while(!mazeConfig.hasWall(px, py, newDir)) {
						if(mazeConfig.getFloorplan().isExitPosition(px, py)) {
							if(!mazeConfig.isValidPosition(px+1, py)) {
								return Integer.MAX_VALUE;
							}
						}
						px++;
						distance++;
					}
					return distance;
			}
			return distance;
		}

		/**
		 * Provides the method distanceToObstacle with
		 * the direction of the sensor it is working with.
		 * @param currentDirection specifies the direction of the robot
		 * @return direction the sensor is facing
		 */
		private CardinalDirection getCurrentSensorDirection(CardinalDirection currentDirection) {
			CardinalDirection newDir = currentDirection;
			switch(sensorDirection) {
				case BACKWARD :
					newDir = newDir.oppositeDirection();
					break;
				case FORWARD :
					break;
				case LEFT :
					newDir = newDir.rotateClockwise();
					break;
				case RIGHT :
					newDir = newDir.oppositeDirection().rotateClockwise();
					break;
			}
			return newDir;
		}

		/**
		 * Provides the maze information that is necessary to make
		 * a DistanceSensor able to calculate distances.
		 * @param maze the maze for this game
		 * @throws IllegalArgumentException if parameter is null
		 * or if it does not contain a floor plan
		 */
		@Override
		public void setMaze(Maze maze) {
			this.mazeConfig = maze;
		}

		/**
		 * Provides the angle, the relative direction at which this
		 * sensor is mounted on the robot.
		 * If the direction is left, then the sensor is pointing
		 * towards the left hand side of the robot at a 90 degree
		 * angle from the forward direction.
		 * @param mountedDirection is the sensor's relative direction
		 * @throws IllegalArgumentException if parameter is null
		 */
		@Override
		public void setSensorDirection(Direction mountedDirection) {
			if(mountedDirection == null) {
				throw new IllegalArgumentException("Error: Parameter is null");
			}
			sensorDirection = mountedDirection;
		}

		/**
		 * Returns the amount of energy this sensor uses for
		 * calculating the distance to an obstacle exactly once.
		 * This amount is a fixed constant for a sensor.
		 * @return the amount of energy used for using the sensor once
		 */
		@Override
		public float getEnergyConsumptionForSensing() {
			return SENSING_ENERGY;
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
		 * @param meanTimeBetweenFailures is the mean time in seconds, must be greater than zero
		 * @param meanTimeToRepair is the mean time in seconds, must be greater than zero
		 * @throws UnsupportedOperationException if method not supported
		 */
		@Override
		public void startFailureAndRepairProcess(int meanTimeBetweenFailures, int meanTimeToRepair)
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
		 * @throws UnsupportedOperationException if method not supported
		 */
		@Override
		public void stopFailureAndRepairProcess() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();

		}

		/**
		 * This method tells UnreliableSensor what
		 * directional sensor it is working with so
		 * that it can tell PlayAnimationActivity which
		 * sensor is at what stage of failure or repair
		 * @param sensor
		 */
		@Override
		public void setWhichSensor(String sensor){}

	}