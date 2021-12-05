package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar;
import edu.wm.cs.cs301.EffieZhang.R;

/**
 * PlayingManuallyActivity class allows the player to play the game manually by
 * using the front, right, and left arrows, and the jump button in the middle.
 * The player can also choose to show or not show the map, solution, and visible
 * walls. The player can zoom in or out the size of by using the progress bar on
 * the top.When player reach the exit, then it automatically goes to WinningActivity.
 * If the player presses the back arrow, then the app will return to AMazeActivity.
 *
 * Collaboration: AMazeActivity, GeneratingActivity, WinningActivity
 *
 * @author Effie Zhang
 */
public class PlayManuallyActivity extends AppCompatActivity {
    private ToggleButton hasWall, showMap, solution;
    private static final int MAX_MAP_SIZE = 80;  //max size that the map can be
    private static final int MIN_MAP_SIZE = 1;  //min size that the map can be
    private int pathLength = 0;  //length of the path the user has taken
    private int mapSize = 15;  //default map size
    private int shortestPathLength = 0;  //shortest possible path length, temp value
    private Button buttonSubmit;
    private int pathlength;
    private MazePanel panel;  // panel used to draw maze
    StatePlaying statePlaying;  // class used to operate maze
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_playmanually);
        final MazePanel panel = (MazePanel) findViewById(R.id.manualMazePanel);
        statePlaying = new StatePlaying();
        statePlaying.setPlayManuallyActivity(this);
        statePlaying.start(panel);
        setSizeOfMap();
        pathlength = 0;
        ImageButton upButton = (ImageButton) findViewById(R.id.upButton);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show();
                Log.v("Forward", "Forward");
                pathlength++;
            }
        });

        ImageButton leftButton = (ImageButton) findViewById(R.id.leftButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                Log.v("left", "left");
            }
        });

        ImageButton rightButton = (ImageButton) findViewById(R.id.rightButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Right", Toast.LENGTH_SHORT).show();
                Log.v("Right", "Right");
            }
        });
        Button jump = (Button) findViewById(R.id.jumpButton);
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Jump", Toast.LENGTH_SHORT).show();
                Log.v("Jump", "Jump");
                pathlength++;
            }
        });
    }
    /**
     * This method sets the size of the map to the
     * size requested by the user.
     */
    private void setSizeOfMap(){
        final SeekBar mapSize1 = (SeekBar) findViewById(R.id.mapSizeSeekBar);
        //final TextView skillLevelText = (TextView) findViewById(R.id.skillLevelTextView);
        mapSize1.setMin(MIN_MAP_SIZE);
        mapSize1.setProgress(15);
        mapSize1.setMax(MAX_MAP_SIZE);
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
        //Log.v("Map Size: " + mapSize);
    }
    @Override
    public void onBackPressed(){
        Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(back2Title);
        Toast.makeText(getApplicationContext(), "Back button", Toast.LENGTH_SHORT).show();
        Log.v("Back Button", "Back button");
    };
}