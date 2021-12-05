package edu.wm.cs.cs301.EffieZhang.gui;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;

import edu.wm.cs.cs301.EffieZhang.generation.CardinalDirection;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Turn;

/**
 * This class specifies a robot driver that operates a robot to escape from a given maze. 
 * 
 * Collaborators: Robot
 * 
 * @author effiezhang
 *
 */

public class Wizard extends WallFollower implements RobotDriver{
	private Robot rob;
	private Maze m;
	private float energy_consumed;
	private int pathlength;

	public Wizard() {
		super();
		System.out.println("Use Wizard to solve the game");
	}
	/**
	 * Assigns a robot platform to the driver. 
	 * The driver uses a robot to perform, this method provides it with this necessary information.
	 * @param r robot to operate
	 */
	@Override
	public void setRobot(Robot r) {
		rob = new ReliableRobot();
		rob = r;
		energy_consumed = 0;
		pathlength = 0;
		
	}
	
	/**
	 * return the robot used in wizard
	 * @return the robot rob
	 */
	public Robot getRobot() {
		return rob;
	}
	
	/**
	 * return the maze
	 * @return Maze m
	 */
	public Maze getMaze() {
		return m;
	}

	/**
	 * Provides the robot driver with the maze information.
	 * Only some drivers such as the wizard rely on this information to find the exit.
	 * @param maze represents the maze, must be non-null and a fully functional maze object.
	 */
	@Override
	public void setMaze(Maze maze) {
		m = maze;
		//assertNotNull(m);
		
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
		while(rob.isAtExit()==false) {
			if(!drive1Step2Exit())
				return false;
		}
		// moving into the exit
		if (rob.canSeeThroughTheExitIntoEternity(Direction.FORWARD)) {
			if(3500-energy_consumed < 6) {
				throw new Exception("robot lack of energy");
			}
			rob.move(1);
			float prev = energy_consumed;
			energy_consumed +=6;
			//assertEquals((int)prev+6, (int)getEnergyConsumption());
		}
		if (rob.canSeeThroughTheExitIntoEternity(Direction.LEFT)) {
			if(3500-energy_consumed < 9) {
				throw new Exception("robot lack of energy");
			}
			rob.rotate(Turn.LEFT);
			float prev = energy_consumed;
			energy_consumed +=3;
			rob.move(1);
			energy_consumed +=6;
			//assertEquals((int)prev+9, (int)getEnergyConsumption());
		}
		if (rob.canSeeThroughTheExitIntoEternity(Direction.RIGHT)) {
			if(3500-energy_consumed < 9) {
				throw new Exception("robot lack of energy");
			}
			rob.rotate(Turn.RIGHT);
			float prev = energy_consumed;
			energy_consumed +=3;
			rob.move(1);
			energy_consumed +=6;
			//assertEquals((int)prev+9, (int)getEnergyConsumption());
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
		int forward = 0;
		int[] position = m.getNeighborCloserToExit(rob.getCurrentPosition()[0], rob.getCurrentPosition()[1]);
		// find direction of position
		CardinalDirection dir;
		if (position[0] == rob.getCurrentPosition()[0]+1) {
			dir = CardinalDirection.East;
		}
		else if (position[0] == rob.getCurrentPosition()[0]-1) {
			dir = CardinalDirection.West;
		}
		else if (position[1] == rob.getCurrentPosition()[1]+1) {
			dir = CardinalDirection.South;
		}
		else {
			dir = CardinalDirection.North;
		}
		
		while (dir != rob.getCurrentDirection()) {
			if(3500-energy_consumed < 3) {
				throw new Exception("robot lack of energy");
			}
			rob.rotate(Turn.LEFT);
			//increase the energy_consumed
			energy_consumed +=3;
		}
		try {
			forward = rob.distanceToObstacle(Direction.FORWARD);
		}
		catch (UnsupportedOperationException e){
			forward = planAdistance(Direction.FORWARD);
		}
		if (forward == 0) {
			if(3500-energy_consumed < 40) {
				throw new Exception("robot lack of energy");
			}
			rob.jump();
			energy_consumed +=40;
			pathlength ++;
		}
		else {
			if(3500-energy_consumed < 6) {
				throw new Exception("robot lack of energy");
			}
			rob.move(1);
			energy_consumed +=6;
			pathlength ++;
		}
		return true;
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
		return energy_consumed;
	}

	/**
	 * Returns the total length of the journey in number of cells traversed. 
	 * Being at the initial position counts as 0. 
	 * This is used as a measure of efficiency for a robot driver.
	 * @return the total length of the journey in number of cells traversed
	 */
	@Override
	public int getPathLength() {
		// TODO Auto-generated method stub
		return pathlength;
	}

}
