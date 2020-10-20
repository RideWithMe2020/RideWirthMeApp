package com.example.ridewithme.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.ridewithme.R;
import com.google.android.material.button.MaterialButton;


public class GoalActivity extends AppCompatActivity {
private MaterialButton goal_BTN_addGoal;
private CheckBox goal_LBL_goalText;
private String inputText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        goal_BTN_addGoal = findViewById(R.id.goal_BTN_addGoal);
        goal_BTN_addGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addText();
            }
        });
    }

    private void addText() {
        AlertDialog.Builder goalDialog = new AlertDialog.Builder(GoalActivity.this);
        goalDialog.setTitle("ADD NEW GOAL");
        final EditText goalInput = new EditText(GoalActivity.this);
        goalDialog.setView(goalInput);
        goalDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputText = goalInput.getText().toString();
                LinearLayout goal_LAY_linealLay = (LinearLayout) findViewById(R.id.goal_LAY_linealLay);
                goal_LBL_goalText = new CheckBox(GoalActivity.this);
                goal_LBL_goalText.setText(inputText);
                goal_LBL_goalText.setTextSize(35);
                goal_LBL_goalText.setClickable(true);
                goal_LAY_linealLay.addView(goal_LBL_goalText);
            }
        });

        goalDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        goalDialog.show();
    }
}