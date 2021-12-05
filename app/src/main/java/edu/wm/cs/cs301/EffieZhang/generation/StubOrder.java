package edu.wm.cs.cs301.EffieZhang.generation;

public class StubOrder implements Order{
    private int skill;
    private Builder builder;
    private boolean perfect;
    private Maze mazeConfiguration;
    int percentDone;

    public StubOrder(int skill, Builder builder, boolean perfect){
        this.skill = skill;
        this.builder = builder;
        this.perfect= perfect;
    }

    @Override
    public int getSkillLevel() {
        return skill;
    }

    @Override
    public Builder getBuilder() {
        return builder;
    }

    @Override
    public boolean isPerfect() {
        return perfect;
    }

    @Override
    public void deliver(Maze mazeConfig) {
        this.mazeConfiguration = mazeConfig;
    }

    @Override
    public void updateProgress(int percentage) {
        this.percentDone = percentage;
    }

    public Maze getMaze(){
        return mazeConfiguration;
    }


    @Override
    public int getSeed() {
        return 0;
    }
}
