package edu.wm.cs.cs301.EffieZhang.gui;

import android.app.Application;

public class DataHolder extends Application {
    private static int skillLevel;
    private static String mazeAlgorithm;
    private static Boolean roomsOrNoRooms;
    private static String driverConfig;
    private static String robotConfig;

    public static void setSkillLevel(int skillLevel) {
        DataHolder.skillLevel = skillLevel;
    }

    public static void setMazeAlgorithm(String mazeAlgorithm){
        DataHolder.mazeAlgorithm= mazeAlgorithm;
    }

    public static void setRoomsOrNoRooms(Boolean rooms){
        DataHolder.roomsOrNoRooms = rooms;
    }

    public static void setDriverConfig(String driverConfig){
        DataHolder.driverConfig = driverConfig;
    }

    public static void setRobotConfig(String robotConfig) {
        DataHolder.robotConfig = robotConfig;
    }

    public static int getSkillLevel() { return skillLevel; }

    public static String getMazeAlgorithm() {
        return mazeAlgorithm;
    }

    public static Boolean getRoomsOrNoRooms() {
        return roomsOrNoRooms;
    }

    public static String getDriverConfig() {
        return driverConfig;
    }

    public static String getRobotConfig() {
        return robotConfig;
    }

}
