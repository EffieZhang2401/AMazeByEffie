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

    /**
     * constructor of revisit
     * @param skilllevel
     * @param algorithm
     * @param hasRoom
     * @param seed
     */
    public Revisit(int skilllevel, String algorithm, boolean hasRoom, int seed){
        skillLevel = skilllevel;
        this.algorithm = algorithm;
        room = hasRoom;
        this.seed = seed;
    }

    /**
     * Get the last maze's skill level
     * @return skill level
     */
    public int getSkillLevel(){
        return skillLevel;
    }

    public String getAlgorithm(){
        return algorithm;
    }

    public boolean getRoom(){
        return room;
    }

    public int getSeed(){
        return seed;
    }


}
