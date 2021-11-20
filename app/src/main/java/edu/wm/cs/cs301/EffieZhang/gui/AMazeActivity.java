package edu.wm.cs.cs301.EffieZhang.gui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.cs301.EffieZhang.R;

/**
 * This class shows the welcome page of the app and allows the user
 * to choose a difficulty level between one and sixteen, a maze generator
 * that is either DFS, Prim, or Boruvka, and whether or not the maze will have rooms.
 * It also allows the user to either press an explore button, which sends the
 * app to the GeneratingActivity with the selected maze generator, difficulty
 * level, and room values, or a revisit button, that once again sends the app
 * to the GeneratingActivity, but this time with the same maze generator, difficulty
 * level, and room values that were used in the last implementation of the game.
 *
 * 	Collaboration: GeneratingActivity, WinningActivity, LosingActivity
 *
 * @author Effie Zhang
 */
public class AMazeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_title);
        // use findViewById() to get the Button
        SeekBar seekbar = (SeekBar)findViewById(R.id.skillBar);
        seekbar.setMax(9);
        seekbar.setProgress(0);
        final int[] chosenSkillLevel = {0};
        int seekBarValue= seekbar.getProgress();
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                chosenSkillLevel[0] = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(AMazeActivity.this, "Maze level is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                DataHolder.setSkillLevel(chosenSkillLevel[0]);
            }
        });

        /**
         * Two spinners that selects the maze algorithm and to have or not have rooms.
         *
         * referenced from Anupam Chugh at JournalDev.com
         * altered by Spencer Bao
         */

        List<String> mazeAlgorithms = new ArrayList<String>();
        mazeAlgorithms.add("DFS");
        mazeAlgorithms.add("Prim");
        mazeAlgorithms.add("Boruvka");

        final Spinner mazeSelectSpinner = (Spinner) findViewById(R.id.builderSpinner);
        ArrayAdapter<String> mazeSelectAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, mazeAlgorithms);
        mazeSelectSpinner.setAdapter(mazeSelectAdapter);
        mazeSelectSpinner.setSelection(1,true);
        mazeSelectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mazeSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected: " +
                        mazeSelectSpinner.getSelectedItem().toString() ,Toast.LENGTH_SHORT).show();
                Log.v("Maze Select toast", "Selected" + mazeSelectSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final List<String> roomsNoRooms = new ArrayList<String>();
        roomsNoRooms.add("Yes");
        roomsNoRooms.add("No");

        final Spinner roomSpinner = (Spinner) findViewById(R.id.roomSpinner);
        ArrayAdapter<String> roomAdapter = new ArrayAdapter <String>(this,
                android.R.layout.simple_spinner_item, roomsNoRooms);
        roomSpinner.setAdapter(roomAdapter);
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected: " +
                        roomSpinner.getSelectedItem().toString() ,Toast.LENGTH_SHORT).show();
                Log.v("RoomOrNoRooms toast", "Selected" + roomSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button revisitButton = (Button) findViewById(R.id.revisitButton);
        revisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), GeneratingActivity.class);
                next.putExtra("Skill Level", seekbar.getProgress());
                Toast.makeText(getApplicationContext(), "Pressed Revisit", Toast.LENGTH_SHORT).show();
                Log.v("Revisit toast", "Pressed Revisit");
                startActivity(next);
            }
        });

        Button exploreButton = (Button) findViewById(R.id.exploreButton);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(), GeneratingActivity.class);
//                next.putExtra("Skill Level", skillBar.getProgress());
//                next.putExtra("Maze Algorithm", mazeSelectSpinner.getSelectedItem().toString());
//                next.putExtra("Rooms", roomSpinner.getSelectedItem().toString());

                DataHolder.setMazeAlgorithm(mazeSelectSpinner.getSelectedItem().toString());
                if(roomSpinner.getSelectedItem().toString().equals("Yes")){
                    DataHolder.setRoomsOrNoRooms(true);
                } else{
                    DataHolder.setRoomsOrNoRooms(false);
                }

                Toast.makeText(getApplicationContext(), "Pressed Explore", Toast.LENGTH_SHORT).show();
                Log.v("Explore toast", "Pressed Explore");
                startActivity(next);
            }
        });
    }
}