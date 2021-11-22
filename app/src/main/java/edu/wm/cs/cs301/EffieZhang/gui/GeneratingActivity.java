package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.widget.Button;

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
public class GeneratingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Button btn = (Button)findViewById(R.id.progressButton);
        btn.setOnClickListener(new View.OnClickListener() {
            /**
             * Runs a thread that increments the loading bar when pressing the "start process" button.
             */
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        while (progressBar.getProgress() < 100) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressBar.incrementProgressBy(10);
                        }
                        Intent next;
                        if (driverSelectSpinner.getSelectedItem().toString() == "Manual") {
                            next = new Intent(getApplicationContext(), PlayManuallyActivity.class);
                        } else {
                            next = new Intent(getApplicationContext(), PlayAnimationActivity.class);
                        }
                        startActivity(next);
                    }
                }).start();
            }
        });
    }
}