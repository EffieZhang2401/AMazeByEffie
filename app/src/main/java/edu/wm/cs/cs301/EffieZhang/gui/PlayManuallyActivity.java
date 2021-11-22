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
    private Button buttonSubmit;
    private int pathlength;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_playmanually);
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
        Button shortcut = (Button) findViewById(R.id.ShortCut);
        shortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder.setPathlength(pathlength);
                Intent winning = new Intent(getApplicationContext(), WinningActivity.class);
                startActivity(winning);
            }
        });
    }
    @Override
    public void onBackPressed(){
        Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(back2Title);
        Toast.makeText(getApplicationContext(), "Back button", Toast.LENGTH_SHORT).show();
        Log.v("Back Button", "Back button");
    };
}