package edu.wm.cs.cs301.EffieZhang.gui;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;

/**
 * A UnreliableSensor provides information how for it is
 * to a wall for a given position and in a specific direction.
 * 
 * The sensor is assumed to be mounted at a particular angle
 * relative to the forward direction of the robot.
 * This way one can have a left sensor, a right sensor, 
 * a forward, or a backward sensor.
 * 
 * Collaborators: UnreliableRobot
 * 
 * @author Effie Zhang
 *
 */
public class UnreliableSensor extends ReliableSensor implements DistanceSensor, Runnable {

	private Thread sensorThread;
	private int meanTimeBetweenFailures;
	private int meanTimeToRepair;
	private boolean failure = false;
	private static final String FAILURE_KEY = "sensor has failed";  //message key
	private String whichSensor;
	private String[] sensorInfo;
	private String[] sensorInfoRepaired;

	public UnreliableSensor() {
		super();
		sensorInfo = new String[2];
		sensorInfoRepaired = new String[2];
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
	public int distanceToObstacle(int[] currentPosition, CardinalDirection currentDirection, float[] powersupply) throws Exception{
		if(!this.isOperational()) {
			System.out.println("Sensor not operational");
			throw new Exception("UnreliableSensor: This sensor is not operational");
		}
		try {
			return super.distanceToObstacle(currentPosition, currentDirection, powersupply);
		}
		catch(Exception e) {System.out.println("Unreliable sensor we have an exception");}
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
	 */
	public void run() {
		try {
			while(sensorThread != null) {
				setFailureOrRepair(true);
				Thread.sleep(meanTimeBetweenFailures);


				setFailureOrRepair(false);
				Thread.sleep(meanTimeToRepair);
				setFailureOrRepair(true);

			}
		}
		catch(InterruptedException e) {}
		return;
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
		this.meanTimeBetweenFailures = meanTimeBetweenFailures;
		this.meanTimeToRepair = meanTimeToRepair;
		sensorThread = new Thread(this);
		sensorThread.start();

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
		if(null == sensorThread) {
			throw new UnsupportedOperationException("Error: There is no running failure and repair process");
		}
		else if(null != sensorThread) {
			sensorThread.interrupt();
			sensorThread = null;
			setFailureOrRepair(true);
		}

	}

	/**
	 * Sets the boolean failure to true if the sensor is currently
	 * operational and false if the sensor is currently under
	 * repair
	 *
	 * @param failure
	 */
	synchronized private void setFailureOrRepair(boolean failure) {
		this.failure = failure;
	}

	/**
	 * This method checks to see if the sensor is currently
	 * operational or is under repair and returns that answer
	 *
	 * @return failure
	 */
	synchronized public boolean isOperational() {
		return failure;
	}

	/**
	 * Tells the sensor which direction the sensor
	 * it is currently working with has
	 * @param sensor
	 */
	@Override
	public void setWhichSensor(String sensor){
		whichSensor = sensor;
	}
}

