package edu.wm.cs.cs301.EffieZhang.gui;

/**
 * A unreliable robot interacts with the game controller to perform operations like turn and
 * move. The robot has four unreliable sensor by default. It could have reliable sensor if the user
 * input in the command line.
 * It is configured by mounting distance sensors to a robot such that it can
 * measure the distance to an obstacle, a wall, in the direction that sensor
 * has been mounted on the robot.
 * It provides an operating platform for a robot driver that experiences a maze (the real world) 
 * through the sensors and actuators of this robot interface.
 * 
 * 
 * Collaborators: a controller that holds a maze to be explored, 
 * UnreliableSensor
 * 
 * @author Effie Zhang
 */
public class UnreliableRobot extends ReliableRobot implements Robot {

	private StatePlaying statePlaying;
	private PlayAnimationActivity playAnimationActivity;
	private DistanceSensor leftSensor;
	private DistanceSensor rightSensor;
	private DistanceSensor frontSensor;
	private DistanceSensor backSensor;


	public UnreliableRobot(){
		super();
	}

	public int[] getCurrentPosition() throws Exception {
		return super.getCurrentPosition();
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
		super.setStatePlaying(statePlaying);
		this.statePlaying = statePlaying;
	}

	public void setPlayAnimationActivity(PlayAnimationActivity playAnimationActivity){
		this.playAnimationActivity = playAnimationActivity;
		this.leftSensor = playAnimationActivity.getSensor(Direction.LEFT);
		this.rightSensor = playAnimationActivity.getSensor(Direction.RIGHT);
		this.frontSensor = playAnimationActivity.getSensor(Direction.FORWARD);
		this.backSensor = playAnimationActivity.getSensor(Direction.BACKWARD);
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
		if(getSensor(direction) == null) {
			System.out.println("Unreliable robot distance problem");
			throw new UnsupportedOperationException();
		}
		DistanceSensor sensor = getSensor(direction);
		try {
			int[] currentPos = getCurrentPosition();
			float[] tempBatteryLevel = new float[] {super.getBatteryLevel()};

			return sensor.distanceToObstacle(currentPos, getCurrentDirection(), tempBatteryLevel);
		}
		catch(IndexOutOfBoundsException e) {
			System.out.println("UnreliableRobot.java Error: Ran out of energy");
		}
		catch(IllegalArgumentException e) {
			System.out.println("UnreliableRobot.java Error: Passed an illegal argument");
		}
		catch(Exception e) {
			System.out.println("catching exception in unreliable robot");
			throw new UnsupportedOperationException();
		}
		return 0;
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
		if(getSensor(direction) == null) {
			throw new UnsupportedOperationException("UnreliableRobot.java Unsupported Operation");
		}
		getSensor(direction).startFailureAndRepairProcess(meanTimeBetweenFailures, meanTimeToRepair);
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
		try {
			getSensor(direction).stopFailureAndRepairProcess();
		}
		catch(UnsupportedOperationException e) {
			throw new UnsupportedOperationException();
		}

	}


	/**
	 * This method gets the sensor in the direction that it receives
	 * as its parameter and returns that sensor
	 *
	 * @param direction
	 * @return correct sensor
	 */
	private DistanceSensor getSensor(Direction direction) {
		switch(direction){
			case FORWARD :
				return playAnimationActivity.getSensor(Direction.FORWARD);
			case BACKWARD :
				return playAnimationActivity.getSensor(Direction.BACKWARD);
			case LEFT :
				return playAnimationActivity.getSensor(Direction.LEFT);
			case RIGHT :
				return playAnimationActivity.getSensor(Direction.RIGHT);
		}
		return frontSensor;
	}

}
