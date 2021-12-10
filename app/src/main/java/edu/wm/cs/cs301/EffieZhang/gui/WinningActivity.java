package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import edu.wm.cs.cs301.EffieZhang.R;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.content.Context;
import android.os.Build;
/**
 * This class shows a winning message for the player, which includes
 * the length of the shortest path through the maze, the length of the
 * path taken by the player, and the amount of energy consumed by the robot
 * if the maze was played with a driver. There is also a button in the
 * bottom right hand corner that the user can press to play again, which
 * returns the app to AMazeActivity, or they can also press the back arrow
 * to return to AMazeActivity.
 *
 * 	Collaboration: AMazeActivity, PlayManuallyActivity, PlayAnimationActivity
 *
 * @author Effie Zhang
 */
public class WinningActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_winning);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
        Button back = (Button) findViewById(R.id.playagain);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Play Again", Toast.LENGTH_SHORT).show();
                Log.v("Play Again", "Play Again");
                Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(back2Title);
            }
        });
        setWinningScreenMessages();
    }
    /**
     * This method sets the screen to show the
     * user's path, the shortest possible path to
     * solve the maze, and the overall energy
     * consumption if the maze was not operated manually.
     */
    private void setWinningScreenMessages(){
        TextView shortPath = (TextView) findViewById(R.id.shortestPathLengthTextView);
        TextView userPath = (TextView) findViewById(R.id.userPathLengthTextView);
        TextView energyCon = (TextView) findViewById(R.id.energyConsumptionTextView);
        int pathLength = DataHolder.getPathlength();
        int shortestPathLength = PlayManuallyActivity.shortestPathLength;
        int energy = DataHolder.getEnergyConsumption();
        shortPath.setText("Shortest Possible Path Length: " + shortestPathLength);
        userPath.setText("Your Path Length: " + pathLength);
        energyCon.setText("Your Energy Consumption:"+ energy);
    }

    /**
     * This method checks to see if the
     * back button has been pressed, and if
     * finds the answer to be true, makes the app return
     * to AMazeActivity.
     */
    @Override
    public void onBackPressed(){
        Log.v("Back to title", "back button pressed in WinningActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}