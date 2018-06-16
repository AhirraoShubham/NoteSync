package com.example.shubham.notesync;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

/**
 * Created by Shubham on 23-03-2017.
 */
public class RegisterAs extends Activity implements  RadioGroup.OnCheckedChangeListener {
    RadioButton radioButtonTeacher,radioButtonStudent;
    RadioGroup registerGroup;
    boolean isTeacher=false,isStudent=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_as);
        radioButtonTeacher=(RadioButton)findViewById(R.id.radioButtonTeacher);
        radioButtonStudent=(RadioButton)findViewById(R.id.radioButtonStudent);
        registerGroup=(RadioGroup)findViewById(R.id.registerGroup);
        registerGroup.setOnCheckedChangeListener(this);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int id) {
        switch (id){
            case R.id.radioButtonTeacher:
                Intent teach=new Intent(this,TeacherIDActivity.class);
                startActivity(teach);
                isTeacher=true;
                isStudent=false;
                break;

            case R.id.radioButtonStudent:
                Intent stud=new Intent(this,Student_Registration.class);
                startActivity(stud);
                isTeacher=false;
                isStudent=true;
                break;
        }
    }
}