package edu.wm.cs.cs301.EffieZhang.gui;

/**
 * Working with revisit option. This class store the last maze information,
 * including skill level, algorithm, has or not has room, and the seed.
 *
 * Collaboration:AMazeActivity, Generating Activity
 */
public class Revisit {
    private static int skillLevel;
    private static String algorithm;
    private static boolean room;
    private static int seed;



    public static void setRevisit(int skilllevel, String algor, boolean hasRoom, int mazeseed){
        skillLevel = skilllevel;
        algorithm = algor;
        room = hasRoom;
        seed = mazeseed;
    }

    /**
     * Get the last maze's skill level
     * @return skill level
     */
    public static int getSkillLevel(){
        return skillLevel;
    }

    /**
     * get algorithm
     * @return algorithm
     */
    public static String getAlgorithm(){
        return algorithm;
    }

    /**
     * get room
     * @return has or not has room
     */
    public static boolean getRoom(){
        return room;
    }

    /**
     * get seed
     * @return seed
     */
    public static int getSeed(){
        return seed;
    }


}
