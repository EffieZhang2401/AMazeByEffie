package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import edu.wm.cs.cs301.EffieZhang.R;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.wm.cs.cs301.EffieZhang.gui.Robot.Direction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
/**
 * This class plays the maze with either a Wall Follower or Wizard driver,
 * depending on what the user chose in GeneratingActivity, and goes to
 * the WinningActivity if the driver reaches the end of the maze successfully,
 * or goes to the LosingState if the driver runs out of energy or fails for
 * some other reason. If the player presses the back arrow, then the app
 * will return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, GeneratingActivity, WinningActivity, LosingActivity
 *
 * @author Effie Zhang
 */
public class PlayAnimationActivity extends AppCompatActivity {
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MAX_ANIMATION_SPEED = 20;  //max animation speed for the robot
    private static final int MIN_MAP_SIZE = 1;
    private static final int ROBOT_INITIAL_ENERGY = 3500;  //amount of energy driver starts with
    private static final String TAG = "message";
    private int mapSize = 15;  //default map size
    private StatePlaying statePlaying;  // allows the user to play the game
    private MazePanel panel;  // panel to draw the maze on
    private static final String KEY = "my message key";  //message key
    private static final String FAILURE_KEY = "sensor has failed";  //message key
    private String sensorConfig;  //sensor configuration chosen by user
    private int animationSpeed = 10;  //default animation speed
    private ProgressBar remainingEnergy;  //remaining energy of robot
    private int shortestPathLength;  //shortest possible path length through the maze
    private String reasonLost = "Ran Out of Energy"; //if the robot lost, tells why
    private Robot robot;  // robot that runs through the maze
    private Wizard wizard;  // driver to make robot go through the maze
    private RobotDriver wallFollower;  // driver that follows wallFollower algorithm
    boolean[] reliableSensor;  // tells what sensors are reliable and which are not
    boolean[] currentSensors;
    private DistanceSensor leftSensor;  // robot's left sensor
    private DistanceSensor rightSensor;  // robot's right sensor
    private DistanceSensor frontSensor;  // robot's front sensor
    private DistanceSensor backSensor;  // robot's back sensor
    private static final int MEAN_TIME_BETWEEN_FAILURES = 4000;  // average time between sensor failures
    private static final int MEAN_TIME_TO_REPAIR = 2000;  // average time to repair sensor failures
    private boolean isWizard = false;  // tells if the driver is the wizard algorithm
    public static Handler myHandler;  // handler used to send messages between classes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_playanimation);
        setProgressBar();
        int[] startPos = GeneratingActivity.mazeConfig.getStartingPosition();
        shortestPathLength = GeneratingActivity.mazeConfig.getDistanceToExit(startPos[0], startPos[1])-1;
        PlayManuallyActivity.shortestPathLength = shortestPathLength;

        //set sensor
        sensorConfig = DataHolder.getRobotConfig();
        currentSensors = new boolean[]{true, true, true, true};
        setRobotSensors(sensorConfig);

        //show maze
        statePlaying = new StatePlaying();
        panel = findViewById(R.id.mazePanelViewAnimated);
        statePlaying.setPlayAnimationActivity(this);
        statePlaying.start(panel);

        //set driver
        String driver = DataHolder.getDriverConfig();
        if(driver.equals("Wizard")){
            setWizardPlaying();
        }
        else{
            //setWallFollowerPlaying();
        }
        setSizeOfMap();
        setSensorColor();

        setAnimationSpeed();

        final ToggleButton playPauseButton = (ToggleButton)  findViewById(R.id.pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playPauseButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                    Log.v("Pause toast", "Pressed Pause");
                }
                else{
                    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
                    Log.v("Start toast", "Pressed Start");
                }
            }
        });

        myHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int remainingEnergyMessage = bundle.getInt(KEY, -1);
                String[] sensorInfo = bundle.getStringArray(FAILURE_KEY);
                if(remainingEnergyMessage != -1){
                    updateProgressBar(remainingEnergyMessage);
                }
                Log.v(TAG, remainingEnergyMessage + "");
                boolean lostGame = bundle.getBoolean("lost", false);
                if(lostGame){
                    sendLosingMessage(panel);
                }
            }
        };
        ToggleButton showMap = (ToggleButton) findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statePlaying.keyDown(Constants.UserInput.TOGGLELOCALMAP, 1);
                Log.v(TAG, "Showing Map: On");
            }
        });
    }

    /**
     * Sets up the progress bar that shows the amount of energy
     * the robot has left
     */
    private void setProgressBar(){
        remainingEnergy = (ProgressBar) findViewById(R.id.remainingEnergy);
        remainingEnergy.setMax(ROBOT_INITIAL_ENERGY);
        remainingEnergy.setProgress(ROBOT_INITIAL_ENERGY);
        final TextView remainingEnergyText = (TextView) findViewById(R.id.remainingEnergyTextView);
        remainingEnergyText.setText("Remaining Energy: " + remainingEnergy.getProgress());
    }

    /**
     * Updates the progress bar that tells the user
     * how much energy the robot has left with the current value
     * passed in by the handler
     * @param remainingEnergy of the robot
     */
    private void updateProgressBar(int remainingEnergy){
        final TextView remainingEnergyText = (TextView) findViewById(R.id.remainingEnergyTextView);
        this.remainingEnergy.setProgress(remainingEnergy);
        remainingEnergyText.setText("Remaining Energy: " + this.remainingEnergy.getProgress());
    }

    /**
     * This method sets the speed of the animation to what
     * is requested by the user.
     */
    private void setAnimationSpeed(){
        final SeekBar animationSpeed1 = (SeekBar) findViewById(R.id.animationSpeedSeekBar);
        //final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        animationSpeed1.setMax(MAX_ANIMATION_SPEED);
        animationSpeed1.setProgress(animationSpeed);
        animationSpeed1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempAnimationSpeed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempAnimationSpeed = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setTheAnimationSpeed(tempAnimationSpeed);
            }
        });
    }

    /**
     * This method sets the animation to be
     * the speed requested by the user, which
     * was passed down to it through setAnimationSpeed().
     * @param speed of the animation
     */
    private void setTheAnimationSpeed(int speed){
        animationSpeed = speed;
        if(wizard!= null){
            wizard.setAnimationSpeed(animationSpeed);
        }
        else{
            wallFollower.setAnimationSpeed(animationSpeed);
        }
        Log.v(TAG, "Animation Speed: " + animationSpeed);
    }

    /**
     * Sets up the wizard class and everything
     * that is necessary for the wizard to run
     * so that the maze is solved automatically by
     * the wizard algorithm
     */
    private void setWizardPlaying() {
        Log.v(TAG, "setting Wizard as Animated Driver");
        isWizard = true;
        ReliableRobot robot = new ReliableRobot();
        wizard = new Wizard();
        robot.setStatePlaying(statePlaying);
        robot.setPlayAnimationActivity(this);
        wizard.setRobot(robot);
        wizard.setMaze(GeneratingActivity.mazeConfig);
        try {
            boolean wizard1 = wizard.drive2Exit();
        }
        catch(Exception e) {
            Log.d("exception e", e.getMessage());
            sendLosingMessage(panel);
        }
    }

    /**
     * This method returns the sensor that corresponds
     * to the direction that is passed in
     * @param direction of the sensor that is requested
     * @return the sensor with the passed in direction
     */
    public DistanceSensor getSensor(Direction direction) {
        switch(direction){
            case FORWARD :
                return frontSensor;
            case BACKWARD :
                return backSensor;
            case LEFT :
                return leftSensor;
            case RIGHT :
                return rightSensor;
        }
        return frontSensor;
    }

    /**
     * Move to the losing activity, store the pathlength and energy consumption
     * @param view
     */
    public void sendLosingMessage(View view){
        Intent intent = new Intent(this, LosingActivity.class);
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength();
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength();
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        DataHolder.setPathlength(pathLength);
        DataHolder.setEnergyConsumption(energyConsump);
        startActivity(intent);
    }

    /**
     * This method makes the app switch
     * to the WinningActivity
     * @param view of the GO2WINNING button
     */
    public void sendWinningMessage(View view){
        Intent intent = new Intent(this, WinningActivity.class);
        int energyConsump = 0;
        int pathLength = 0;
        if(isWizard){
            pathLength = wizard.getPathLength()-1;
            energyConsump = (int)wizard.getEnergyConsumption();
        }
        else{
            pathLength = wallFollower.getPathLength()-1;
            energyConsump = (int)wallFollower.getEnergyConsumption();
        }
        DataHolder.setPathlength(pathLength);
        DataHolder.setEnergyConsumption(energyConsump);
        startActivity(intent);
    }

    /**
     * This method makes the sensors on the screen
     * show up as green if they are reliable
     * and red if they are unreliable
     * @param robot with specified sensors picked by the user
     */
    private void setRobotSensors(String robot){
        Log.v(TAG, "Robot; " + robot);
        reliableSensor = new boolean[]{true, true, true, true};
        if(robot.equals("Mediocre")){
            reliableSensor[1] = false;
            reliableSensor[2] = false;
        }
        else if(robot.equals("Soso")){
            reliableSensor[0] = false;
            reliableSensor[3] = false;
        }
        else if(robot.equals("Shaky")){
            reliableSensor = new boolean[]{false, false, false, false};
        }
    }

    /**
     * Creates a new reliable or unreliable
     * sensor in the direction passed in by the
     * parameter
     * @param whichSensor that tells what sensor is being set
     * @param reliable that is true if the sensor
     * is supposed to be reliable and false if
     * it is supposed to be unreliable
     */
    public void setSensor(String whichSensor, boolean reliable) {
        if(whichSensor.equalsIgnoreCase("left")) {
            if(reliable) {
                leftSensor = new ReliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                leftSensor = new UnreliableSensor();
                leftSensor.setSensorDirection(Direction.LEFT);
                leftSensor.setMaze(GeneratingActivity.mazeConfig);
                leftSensor.setWhichSensor("Left");
                robot.startFailureAndRepairProcess(Direction.LEFT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("right")) {
            if(reliable) {
                rightSensor = new ReliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                rightSensor = new UnreliableSensor();
                rightSensor.setSensorDirection(Direction.RIGHT);
                rightSensor.setMaze(GeneratingActivity.mazeConfig);
                rightSensor.setWhichSensor("Right");
                robot.startFailureAndRepairProcess(Direction.RIGHT, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("front")) {
            if(reliable) {
                frontSensor = new ReliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                frontSensor = new UnreliableSensor();
                frontSensor.setSensorDirection(Direction.FORWARD);
                frontSensor.setMaze(GeneratingActivity.mazeConfig);
                frontSensor.setWhichSensor("Front");
                robot.startFailureAndRepairProcess(Direction.FORWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
        else if(whichSensor.equalsIgnoreCase("back")) {
            if(reliable) {
                backSensor = new ReliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(GeneratingActivity.mazeConfig);
            }
            else {
                backSensor = new UnreliableSensor();
                backSensor.setSensorDirection(Direction.BACKWARD);
                backSensor.setMaze(GeneratingActivity.mazeConfig);
                backSensor.setWhichSensor("Back");
                robot.startFailureAndRepairProcess(Direction.BACKWARD, MEAN_TIME_BETWEEN_FAILURES, MEAN_TIME_TO_REPAIR);
            }
        }
    }

    /**
     * set the sensor color according to the user's selection
     * if sensor is reliable, then sensortext is green; otherwise,
     * sensortext is red
     */
    private void setSensorColor(){
        Intent intent = getIntent();
        String robotConfig = DataHolder.getRobotConfig();
        System.out.println(robotConfig);
        TextView sensorText;
        switch (robotConfig){
            case "Premium":
                break;
            case "Mediocre":
                sensorText = (TextView) findViewById(R.id.leftSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                sensorText = (TextView) findViewById(R.id.rightSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                break;
            case "So-so":
                sensorText = (TextView) findViewById(R.id.frontSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                sensorText = (TextView) findViewById(R.id.backSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                break;
            case "Shaky":
                sensorText = (TextView) findViewById(R.id.leftSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                sensorText = (TextView) findViewById(R.id.rightSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                sensorText = (TextView) findViewById(R.id.frontSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                sensorText = (TextView) findViewById(R.id.backSensorTextView);
                sensorText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                break;
        }

    }
    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.animatedMapSizeSeekBar);
        //final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setMin(1);
        mapSize1.setMax(MAX_MAP_SIZE);
        mapSize1.setProgress(mapSize);
        mapSize1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int tempMapSize = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempMapSize = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMapSize(tempMapSize);
            }
        });
    }
    /**
     * This method sets the map to be
     * the size requested by the user, which
     * was passed down to it through setSizeOfMap().
     * @param size of the map
     */
    private void setMapSize(int size){
        mapSize = size;
        statePlaying.setMapScale(mapSize);
        Log.v(TAG, "Map Size: " + mapSize);
    }
    @Override
    public void onBackPressed(){
        if(isWizard){
            try {
                wizard.terminateThread();
            }
            catch(Exception e){}
        }
        else{
            try {
                wallFollower.terminateThread();
            }
            catch(Exception e){}
        }
        Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(back2Title);
        Toast.makeText(getApplicationContext(), "Back button", Toast.LENGTH_SHORT).show();
        Log.v("Back Button", "Back button");
    };


}