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
    private static final String TAG = "message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_playanimation);
        setSensorColor();
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
        Button win = (Button) findViewById(R.id.go2winning);
        win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent winning = new Intent(getApplicationContext(), WinningActivity.class);
                startActivity(winning);
            }
        });
        Button lose = (Button) findViewById(R.id.go2losing);
        lose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent losing = new Intent(getApplicationContext(), LosingActivity.class);
                startActivity(losing);
            }
        });
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
    @Override
    public void onBackPressed(){
        Intent back2Title = new Intent(getApplicationContext(), AMazeActivity.class);
        startActivity(back2Title);
        Toast.makeText(getApplicationContext(), "Back button", Toast.LENGTH_SHORT).show();
        Log.v("Back Button", "Back button");
    };


}