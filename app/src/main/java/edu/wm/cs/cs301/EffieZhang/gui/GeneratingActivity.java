package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import android.os.Looper;
import android.os.Message;
import java.util.List;
import android.widget.Button;
import edu.wm.cs.cs301.EffieZhang.generation.Order;
import edu.wm.cs.cs301.EffieZhang.generation.StubOrder;
import edu.wm.cs.cs301.EffieZhang.generation.Maze;
import edu.wm.cs.cs301.EffieZhang.generation.MazeFactory;
import java.util.Random;

import edu.wm.cs.cs301.EffieZhang.R;

/**
 * The GeneratingActivity class is connected to the state_generating.
 * it allows the user to pick whether or not they
 * want to play the maze manually or want the maze solved with a
 * Wizard or Wall Follower robot. This class allows
 * the user to choose a robot with four options. The maze is being loaded with
 * a progress bar showing how much of the maze has been loaded at the bottom
 * of the screen. After selecting the driver and robot, user can press the start
 * process to start the game, and the progress bar would start to load. If the user
 * select to play the game manually, the app then goes to PlayManuallyActivity.
 * Otherwise, the app goes to PlayAnimationActivity. If the user presses the back
 * arrow, then the app will return to AMazeActivity.
 *
 * Collaboration: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 *
 * @author Effie Zhang
 */
public class GeneratingActivity extends AppCompatActivity implements Order{
    private static final String TAG = "GeneratingActivity";  //message key
    private static final String PROGRESS_KEY = "my message key";  //message key
    private ProgressBar loadingBar;  //progress bar for loading maze
    private Handler handler;  //handler to send messages from background thread to UI thread
    private String driver;  //driver that the maze is going to use
    private boolean backPressed = false;  //tells whether or not the back button has been pressed
    private String robot;  //sensor configuration that the user chooses
    private Order.Builder builder;  // builder for the maze
    private int skillLevel;  // difficulty level of the maze
    private boolean rooms;  // whether or not the maze has rooms
    private boolean revisit;
    private int seed;  // seed to generate random maze
    protected MazeFactory factory;  // factory created to order maze
    private int percentdone;  // gives the percent that the maze has loaded
    public static Maze mazeConfig;  // static variable for the maze configuration
    private boolean deterministic = false;  // tells whether or not the maze is perfect
    //private final int mode = Activity.MODE_PRIVATE;  // mode for the preference storage
    private final String MYPREFS = "My Preferences";  // string name for myPreferences
    //private SharedPreferences myPreferences;  // storage for maze settings
    //private SharedPreferences.Editor myEditor;  // editor for myPreferences
    private final int DEFAULT_SEED = 13;  // default seed value
    public GeneratingActivity(){
        seed = 13;
        percentdone = 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        factory= new MazeFactory();
        Bundle bundle = getIntent().getExtras();
        revisit = bundle.getBoolean("Revisit");
        init(bundle);
        factory.order(this);
        setContentView(R.layout.state_generating);
        // driverSelect spinner
        List<String> driverSelect = new ArrayList<String>();
        driverSelect.add("Manual");
        driverSelect.add("Wall Follower");
        driverSelect.add("Wizard");

        final Spinner driverSelectSpinner = (Spinner) findViewById(R.id.driverSelect);
        ArrayAdapter<String> driverSelectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, driverSelect);
        driverSelectSpinner.setAdapter(driverSelectAdapter);
        driverSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected: " +
                        driverSelectSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                Log.v("Driver Select toast", "Selected" + driverSelectSpinner.getSelectedItem().toString());
                DataHolder.setDriverConfig(driverSelectSpinner.getSelectedItem().toString());
                robot = driverSelectSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // robotConfig spinner
        final List<String> robotConfig = new ArrayList<String>();
        robotConfig.add("Premium");
        robotConfig.add("Mediocre");
        robotConfig.add("So-so");
        robotConfig.add("Shaky");

        final Spinner robotConfigSpinner = (Spinner) findViewById(R.id.robotConfig);
        ArrayAdapter<String> robotConfigAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, robotConfig);
        robotConfigSpinner.setAdapter(robotConfigAdapter);
        robotConfigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        robotConfigSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected: " +
                        robotConfigSpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                Log.v("Robot Config toast", "Selected" + robotConfigSpinner.getSelectedItem().toString());
                DataHolder.setRobotConfig(robotConfigSpinner.getSelectedItem().toString());
                driver = robotConfigSpinner.getSelectedItem().toString();
                //showStartButton(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        loadingBar = (ProgressBar) findViewById(R.id.progressBar);
        loadingBar.setMax(100);
        //runThread(loadingBar);
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                int progressBarMessage = bundle.getInt(PROGRESS_KEY);
                //showStartButton(loadingBar);
            }
        };

        Button btn = (Button)findViewById(R.id.progressButton);
        btn.setOnClickListener(new View.OnClickListener() {
            /**
             * Runs a thread that increments the loading bar when pressing the "start process" button.
             */
            @Override
            public void onClick(View v) {
                if (DataHolder.getDriverConfig().equals("Wall Follower")||DataHolder.getDriverConfig().equals("Wizard")) {
                    Intent intent = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * Initializes each of the elements in the order
     * with the values chosen by the user in AMazeActivity
     * @param bundle
     */
    private void init(Bundle bundle){
        factory = new MazeFactory();
        switch(bundle.getString("Maze Generator")){
            case "Prim":
                builder = Builder.Prim;
                break;
            case "Boruvka":
                builder = Builder.Boruvka;
                break;
            case "DFS":
                builder = Builder.DFS;
                break;
        }
        skillLevel = bundle.getInt("Skill Level");
        rooms = bundle.getBoolean("Rooms");
        if(!deterministic&& revisit==false){
            Random rand = new Random();
            seed = rand.nextInt();
        }
        Revisit.setRevisit(skillLevel,bundle.getString("Maze Generator"),rooms,seed);
        if(revisit == true){
            seed = Revisit.getSeed();
        }
    }

    /**
     * Returns the difficulty level that the user chooses
     * @return int skill level
     */
    @Override
    public int getSkillLevel() {
        return skillLevel;
    }

    /**
     * Returns the builder that the user chooses
     * @return Builder builder
     */
    @Override
    public Builder getBuilder() {
        return builder;
    }

    /**
     * Returns whether or not the user wants rooms
     * @return boolean rooms
     */
    @Override
    public boolean isPerfect() {
        return !rooms;
    }

    /**
     * Returns the random seed
     * @return int seed
     */
    @Override
    public int getSeed() {
        return seed;
    }


    /**
     * Sets the static variable mazeConfig so that
     * every class can access the maze once it has been
     * created
     */
    @Override
    public void deliver(Maze mazeConfig) {
        this.mazeConfig = mazeConfig;
    }

    @Override
    public void updateProgress(int percentage) {
        Log.v(TAG, "Updating progress loaded to " + percentage);
        if (this.percentdone < percentage && percentage <= 100) {
            this.percentdone = percentage;
            loadingBar.setProgress(percentdone);
        }
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt(PROGRESS_KEY, this.percentdone);
        message.setData(bundle);
        handler.sendMessage(message);
    }

}