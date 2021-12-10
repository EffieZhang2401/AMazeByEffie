package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import edu.wm.cs.cs301.EffieZhang.R;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * The LosingActivity class displays a losing message for the user, which includes
 * the length of the shortest path through the maze, the length of
 * the path taken by the user, why the robot stopped, and the amount
 * of energy consumed by the robot if the maze was played with a driver.
 * There is also a button in the bottom right hand corner that the user
 * can press to play again, which returns the app to AMazeActivity, or
 * they can also press the backspace button to return to AMazeActivity.
 *
 *  Collaboration: AMazeActivity, PlayAnimationActivity
 *
 * @author Effie Zhang
 */
public class LosingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_losing);
        Button back = (Button) findViewById(R.id.button8);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Play Again", Toast.LENGTH_SHORT).show();
                Log.v("Play Again", "Play Again");
                Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
                startActivity(back2Title);
            }
        });
        setLosingScreenMessages();
    }
    /**
     * This method sets the screen to show the
     * user's path, the shortest possible path to
     * solve the maze, and the overall energy
     * consumption if the maze was not operated manually.
     */
    private void setLosingScreenMessages(){
        TextView shortPath = (TextView) findViewById(R.id.shortestPathLengthTextView);
        TextView userPath = (TextView) findViewById(R.id.userPathLengthTextView);
        TextView energyCon = (TextView) findViewById(R.id.energyConsumptionTextView);
        TextView reason = (TextView) findViewById(R.id.loseReason);
        int pathLength = DataHolder.getPathlength();
        int shortestPathLength = DataHolder.getPathlength();
        int energy = DataHolder.getEnergyConsumption();
        shortPath.setText("Shortest Possible Path Length: " + shortestPathLength);
        userPath.setText("Your Path Length: " + pathLength);
        energyCon.setText("Your Energy Consumption:"+ energy);
        if(energy >= 3500){
            reason.setText("Lost Reason: Lack of energy");
        }
        else{
            reason.setText("Lost Reason: Robot is broken");
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }
    /**
     * This method checks to see if the back button has been pressed, and if
     * finds the answer to be true, makes the app return to AMazeActivity.
     */
    @Override
    public void onBackPressed(){
        Log.v("back", "back button pressed in LosingActivity");
        Intent intent = new Intent(this, AMazeActivity.class);
        startActivity(intent);
    }
}