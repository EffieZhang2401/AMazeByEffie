package edu.wm.cs.cs301.EffieZhang.gui;

import android.app.Application;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;

/**
 * This dataholder can save the data collected through user's input
 * It has the set method and get method to receive and transfer data
 * It stores the data of mazelevel, driver, robot, algorithm,
 * hasroom, and pathlength.
 * @author effiezhang
 */
public class DataHolder{
    private static int level;
    private static String driverConfig;
    private static String robotConfig;
    private static int pathlength;
    private static String algorithm;
    private static Boolean hasroom;
    private static int energyConsumption;
    private static Maze maze;
    private static int seed;

    /**
     * set the seed
     * @param s
     */
    public static void setSeed(int s){
        seed = s;
    }

    /**
     * get the seed
     * @return seed
     */
    public static int getSeed(){
        return seed;
    }

    /**
     * set the maze
     * @param m
     */
    public static void setMaze(Maze m){
        maze = m;
    }

    /**
     * get the maze
     * @return maze
     */
    public static Maze getMaze(){
        return maze;
    }

    public static void setEnergyConsumption(int energy){
        energyConsumption = energy;
    }

    public static int getEnergyConsumption(){
        return energyConsumption;
    }
    /**
     * Set the path length to the static int pathlength
     * @param len
     */
    public static void setPathlength(int len){
        DataHolder.pathlength = len;
    }

    /**
     * return the pathlength
     * @return pathlength
     */
    public static int getPathlength(){return pathlength;};

    /**
     * set the level of maze
     * @param skillLevel
     */
    public static void setSkillLevel(int skillLevel) {
        DataHolder.level = skillLevel;
    }

    /**
     * set the maze by inputting algorithm
     * @param mazeAlgorithm
     */
    public static void setMazeAlgorithm(String mazeAlgorithm){
        DataHolder.algorithm= mazeAlgorithm;
    }

    /**
     * determine whether the maze is perfect or not
     * @param rooms
     */
    public static void setRoomsOrNoRooms(Boolean rooms){
        DataHolder.hasroom = rooms;
    }

    /**
     * set the driver, manual or wall follower or wizard
     * @param  driverConfig
     */
    public static void setDriverConfig(String driverConfig){
        DataHolder.driverConfig = driverConfig;
    }

    /**
     * set the robot by sensors
     * @param robotConfig
     */
    public static void setRobotConfig(String robotConfig) {
        DataHolder.robotConfig = robotConfig;
    }

    /**
     * get the level of maze
     * @return skillLevel
     */
    public static int getSkillLevel() { return level; }

    /**
     * get the algorithm of maze
     * @return mazeAlgorithm
     */
    public static String getMazeAlgorithm() {
        return algorithm;
    }

    /**
     * get the room setting
     * @return roomsOrNoRooms
     */
    public static Boolean getRoomsOrNoRooms() {
        return hasroom;
    }

    /**
     * get the driver
     * @return driverConfig
     */
    public static String getDriverConfig() {
        return driverConfig;
    }

    /**
     * get the robot
     * @return robotConfig
     */
    public static String getRobotConfig() {
        return robotConfig;
    }

}
